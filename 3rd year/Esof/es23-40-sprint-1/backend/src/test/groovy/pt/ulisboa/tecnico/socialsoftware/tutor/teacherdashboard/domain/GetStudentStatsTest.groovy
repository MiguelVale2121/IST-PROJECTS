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
class GetStudentStatsTest extends SpockTest {
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


        when: "student stats is set"
		studentStats.setNumStudents(2)
		studentStats.setNumMore75CorrQuestions(2)
		studentStats.setNumAtLeast3Quizzes(2)
		studentStats.setTeacherDashboard(dashboard)
		studentStats.setCourseExecution(externalCourseExecution)
		
		then: "student stats contain their assigned value"
 		studentStats.getNumStudents() == 2
 		studentStats.getNumMore75CorrQuestions() == 2
 		studentStats.getNumAtLeast3Quizzes() == 2
		studentStats.getTeacherDashboard().getId() == dashboard.getId()
		studentStats.getCourseExecution().getId() == externalCourseExecution.getId()
		studentStats.getId() == 1
	}


	def 'get student stats with invalid number'() {
		given: "a student stats"
		studentStats = new StudentStats(externalCourseExecution, dashboard)


        when: "student stats is set"
		studentStats.setNumStudents(-1);
		studentStats.setNumMore75CorrQuestions(-1);
		studentStats.setNumAtLeast3Quizzes(-1);
		
		then: "ignores invalid numbers"
 		studentStats.getNumStudents() >= 0
 		studentStats.getNumMore75CorrQuestions() >= 0
 		studentStats.getNumAtLeast3Quizzes() >= 0
	}

	def 'get student stats with null'() {
		given: "a student stats"
		studentStats = new StudentStats(externalCourseExecution, dashboard)


        when: "student stats is set"
		studentStats.setNumStudents(null);
		studentStats.setNumMore75CorrQuestions(null);
		studentStats.setNumAtLeast3Quizzes(null);
		
		then: "ignores null"
 		studentStats.getNumStudents() >= 0
 		studentStats.getNumMore75CorrQuestions() >= 0
 		studentStats.getNumAtLeast3Quizzes() >= 0
	}
	
    @TestConfiguration
    static class LocalBeanConfiguraon extends BeanConfiguration {}
}