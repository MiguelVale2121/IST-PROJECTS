package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll


@DataJpaTest
class toStringTeacherDashboardTest extends SpockTest {
	def teacher
	def dashboard
	def studentStats
	def student
	def studentdashboard

	def setup() {
		createExternalCourseAndExecution()
		teacher = new Teacher(USER_1_NAME, false)
		teacher.addCourse(externalCourseExecution)
		userRepository.save(teacher)
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
		def auth_user = AuthUser.createAuthUser(teacher, USER_1_NAME, "jumpy@tecnico.ulisboa.pt", AuthUser.Type.DEMO)
		teacher.setAuthUser(auth_user)
	}

	def 'check if string if build correctly'() {
		given: "a teacher dashboard with auth user"
		dashboard = teacherDashboardRepository.findAll().get(0)


        when: "a string is build"
        def str = "Dashboard{" +
                "id=" + 1 +
                ", courseExecution=" + externalCourseExecution.toString() +
                ", teacher=" + teacher +
                '}';
		
		then: "to string build the correct string"
		dashboard.toString() == str
 	}

    @TestConfiguration
    static class LocalBeanConfiguraon extends BeanConfiguration {}
}