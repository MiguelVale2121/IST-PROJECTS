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
class toStringStudentStatsTest extends SpockTest {
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

	def 'get student stats string'() {
		given: "a student stats"
		studentStats = new StudentStats()
		studentStatsRepository.save(studentStats)


        when: "with certain values"
		studentStats.setNumStudents(2)
		studentStats.setNumMore75CorrQuestions(2)
		studentStats.setNumAtLeast3Quizzes(2)
		studentStats.setTeacherDashboard(dashboard)
		studentStats.setCourseExecution(externalCourseExecution)
		def str = "StudentStats{" +
					"id=" + 1 +
					", courseExecution=" + externalCourseExecution.toString() +
					", numStudents=" + 2 +
					", numMore75CorrectQuestions=" + 2 +
					", numAtLeast3Quizzes=" + 2 +
					'}';
		
		then: "student stats toString is correct"
		studentStats.toString() == str
 	}
	
    @TestConfiguration
    static class LocalBeanConfiguraon extends BeanConfiguration {}
}