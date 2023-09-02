package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import spock.lang.Unroll


@DataJpaTest
class CreateStudentStatsTest extends SpockTest {
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
	}

	def 'create an empty StudentStats'() {
		given: "a teacher dashboard"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
		dashboard = teacherDashboardRepository.findAll().get(0)

        when: "StudentStats is created"
		studentStats = new StudentStats(externalCourseExecution, dashboard)
		
		then: "an empty StudentsStats is created"
 		studentStats.getNumStudents() == 0
        studentStats.getNumMore75CorrQuestions() == 0
        studentStats.getNumAtLeast3Quizzes() == 0
        studentStats.getCourseExecution().getId() == externalCourseExecution.getId()
        studentStats.getTeacherDashboard().getId() == dashboard.getId()
	}
	
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}