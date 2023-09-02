package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetQuestionStatsTest extends SpockTest {

    def teacher
    def dashboard
    def questionStats
    def student

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        dashboard = teacherDashboardRepository.findAll().get(0)
    }

    def 'get question stats values with valid numbers'() {
        given: 'a QuestionStats'
        questionStats = new QuestionStats()
        questionStatsRepository.save(questionStats)

        when: 'QuestionStats values are set'
        questionStats.setNumAvailable(2)
        questionStats.setAnsweredQuestionUnique(2)
        questionStats.setAverageQuestionsAnswered(2.0)
        questionStats.setTeacherDashboard(dashboard)
        questionStats.setCourseExecution(externalCourseExecution)

        then: 'QuestionStats contain the assigned values'
        questionStats.getNumAvailable() == 2
        questionStats.getAnsweredQuestionUnique() == 2
        questionStats.getAverageQuestionsAnswered() == 2.0
        questionStats.getTeacherDashboard().getId() == dashboard.getId()
        questionStats.getCourseExecution().getId() == externalCourseExecution.getId()
        questionStats.getId() == 1
    }

    def 'get question stats values with invalid values'() {
        given: 'a QuestionStats'
        questionStats = new QuestionStats(externalCourseExecution, dashboard)

        when: 'QuestionStats values are set with -1'
        questionStats.setNumAvailable(-1)
        questionStats.setAnsweredQuestionUnique(-1)
        questionStats.setAverageQuestionsAnswered(-1.0)

        then: 'QuestionStats values have to be 0'
        questionStats.getNumAvailable() == 0
        questionStats.getAnsweredQuestionUnique() == 0
        questionStats.getAverageQuestionsAnswered() == 0.0
    }

    def 'get question stats values with null'() {
        given: 'a QuestionStats'
        questionStats = new QuestionStats(externalCourseExecution, dashboard)

        when: 'QuestionStats values are set with null'
        questionStats.setNumAvailable(null)
        questionStats.setAnsweredQuestionUnique(null)
        questionStats.setAverageQuestionsAnswered(null)

        then: 'QuestionStats values have to be 0'
        questionStats.getNumAvailable() == 0
        questionStats.getAnsweredQuestionUnique() == 0
        questionStats.getAverageQuestionsAnswered() == 0.0
    }

    def 'get question stats as a string'() {
        given: 'a QuestionStats'
        questionStats = new QuestionStats(externalCourseExecution, dashboard)
        questionStatsRepository.save(questionStats)
        def testString = 'QuestionStats{' + 'id=' + 2 + ', numAvailable=' + 2 + ', answeredQuestionUnique=' + 4 + ', averageQuestionsAnswered='+ 3.0 + '}'

        when: 'QuestionStats values are set'
        questionStats.setNumAvailable(2)
        questionStats.setAnsweredQuestionUnique(4)
        questionStats.setAverageQuestionsAnswered(3.0)

        then: 'QuestionStats toString has to contain assigned values'
        questionStats.toString().equals(testString)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }

}
