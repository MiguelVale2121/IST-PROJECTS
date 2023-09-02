package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher


@DataJpaTest
class CreateQuestionStatsTest extends SpockTest {
    def teacher
    def dashboard
    def questionStats
    def student

    def setup() {
        createExternalCourseAndExecution()
        teacher = new Teacher(USER_1_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)
    }

    def 'create an empty QuestionStats'() {
        given: "a teacher dashboard"
            teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
            dashboard = teacherDashboardRepository.findAll().get(0)

        when: "QuestionStats is created"
            questionStats = new QuestionStats(externalCourseExecution, dashboard)

        then: "an empty QuestionStats is created"
            questionStats.getNumAvailable() == 0
            questionStats.getAnsweredQuestionUnique() == 0
            questionStats.getCourseExecution().getId() == externalCourseExecution.getId()
            questionStats.getTeacherDashboard().getId() == dashboard.getId()
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
