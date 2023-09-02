package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class CreateAQuizStatsTest extends SpockTest {
    def teacher
    def question

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def "create empty quizStats"() {
        given: "a teacher dashboard"
        def teacherDashboard = createTeacherDashboardAndPersist()

        when: "a QuizStats is created"
        def quizStats = createQuizStatsAndPersist(teacherDashboard)

        then: "the new quizStats is correctly persisted"
        quizStatsRepository.count() == 1L
        quizStats.getNumQuizzes() == 0
        quizStats.getAverageQuizzesSolved() == 0
        quizStats.getUniqueQuizzesSolved() == 0
        quizStats.getTeacherDashboard().getId() == teacherDashboard.getId()

        def result = QuizStatsRepository.findAll().get(0)
        result.getCourseExecution().getId() == externalCourseExecution.getId()
    }

    def "create empty quiz statistics"() {

        when: "a quiz statistics is created"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)

        then: "the new quiz statistics is correctly persisted"
        quizStatsRepository.count() == 1L
        def result = QuizStatsRepository.findAll().get(0)
        result.getCourseExecution().getId() == externalCourseExecution.getId()
    }

    def createTeacherDashboardAndPersist() {
        def teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
        return teacherDashboard
    }

    def createQuizStatsAndPersist(TeacherDashboard teacherDashboard) {
        def quizStats = new QuizStats(teacherDashboard, teacherDashboard.getCourseExecution())
        quizStatsRepository.save(quizStats)
        return quizStats
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}