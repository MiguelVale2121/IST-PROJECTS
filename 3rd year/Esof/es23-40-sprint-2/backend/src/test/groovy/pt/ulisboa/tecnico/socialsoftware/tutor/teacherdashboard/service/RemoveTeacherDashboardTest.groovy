package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll

@DataJpaTest
class RemoveTeacherDashboardTest extends SpockTest {

    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def createTeacherDashboard() {
        def dashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(dashboard)
        return dashboard
    }

    def "cannot remove a dashboard twice"() {
        given: "a removed dashboard"
        def dashboard = createTeacherDashboard()
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        when: "the dashboard is removed for the second time"
        teacherDashboardService.removeTeacherDashboard(dashboard.getId())

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
    }

    def "remove a dashboard"() {
        given:
        teacher.addCourse(externalCourseExecution)
        def dto = teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        when: "a dashboard is removed"
        teacherDashboardService.removeTeacherDashboard(dto.getId())

        then: "the statistics are removed"
        teacherDashboardRepository.findAll().size() == 0L
        questionStatsRepository.findAll().size() == 0L
        quizStatsRepository.findAll().size() == 0L
        studentStatsRepository.findAll().size() == 0L
    }

    @Unroll
    def "cannot remove a dashboard that doesn't exist with the dashboardId=#dashboardId"() {
        when: "an incorrect dashboard id is removed"
        teacherDashboardService.removeTeacherDashboard(dashboardId)

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
