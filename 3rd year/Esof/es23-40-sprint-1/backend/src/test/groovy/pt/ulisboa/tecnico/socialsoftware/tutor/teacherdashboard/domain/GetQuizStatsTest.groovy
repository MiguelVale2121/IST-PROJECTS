package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetQuizStatsTest extends SpockTest {
    def teacher
    def teacherDashboard
    def QuizStats

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacher.addDashboard(teacherDashboard)
    }

    def 'get quiz stats with valid numbers'() {
        given: "a quiz stats"
        def quizStats = new QuizStats()
        quizStatsRepository.save(quizStats)

        when: "quiz stats is set"
        quizStats.setTeacherDashboard(teacherDashboard)
        quizStats.setCourseExecution(externalCourseExecution)
        quizStats.setNumQuizzes(5)
        quizStats.setUniqueQuizzesSolved(5)
        quizStats.setAverageQuizzesSolved((float) 5)

        then: "quiz stats contain their assigned value"
        quizStats.getNumQuizzes() == 5
        quizStats.getUniqueQuizzesSolved() == 5
        quizStats.getAverageQuizzesSolved() == (float) 5
        quizStats.getTeacherDashboard().getId() == teacherDashboard.getId()
        quizStats.getCourseExecution().getId() == externalCourseExecution.getId()
        quizStats.getId() == 1
    }

    def 'get quiz stats with invalid numbers'() {
        given: "a quiz stats"
        def quizStats = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStats)

        when: "quiz stats is set"
        quizStats.setNumQuizzes(-5)
        quizStats.setUniqueQuizzesSolved(-5)
        quizStats.setAverageQuizzesSolved((float) -5)

        then: "quiz stats ignores values"
        quizStats.getNumQuizzes() >= 0
        quizStats.getUniqueQuizzesSolved() >= 0
        quizStats.getAverageQuizzesSolved() >= (float) 0
    }

    def 'get quiz stats with null'() {
        given: "a quiz stats"
        def quizStats = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStats)

        when: "quiz stats is set"
        quizStats.setNumQuizzes(null)
        quizStats.setUniqueQuizzesSolved(null)
        quizStats.setAverageQuizzesSolved(null)

        then: "quiz stats ignores values"
        quizStats.getNumQuizzes() >= 0
        quizStats.getUniqueQuizzesSolved() >= 0
        quizStats.getAverageQuizzesSolved() >= (float) 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
