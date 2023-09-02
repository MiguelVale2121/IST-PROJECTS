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
import spock.lang.Unroll


@DataJpaTest
class setCourseExecutionTeacherDashboardTest extends SpockTest {
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
		dashboard = teacherDashboardRepository.findAll().get(0)
	}

	def 'get student stats with valid numbers'() {
		given: "a student stats"
		studentStats = new StudentStats()
		studentStatsRepository.save(studentStats)
		dashboard.addStudentStats(studentStats)

        when: "student stats is set"
		dashboard.setCourseExecution(externalCourseExecution)
		
		then: "student stats contain their assigned value"
 		externalCourseExecution == studentStats.getCourseExecution()
		externalCourseExecution == dashboard.getCourseExecution()
	}
	
    @TestConfiguration
    static class LocalBeanConfiguraon extends BeanConfiguration {}
}