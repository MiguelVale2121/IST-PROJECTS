package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class toStringQuizStatsTest extends SpockTest {
    def teacher
    def question

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

    }

    def 'get quizStats with valid numbers'() {
        given: "a quizStats"
        def teacherDashboard = createTeacherDashboardAndPersist()
        def quizStats = createQuizStatsAndPersist(teacherDashboard)

        when: "quizStats is set"
        quizStats.setNumQuizzes(2)
        quizStats.setAverageQuizzesSolved(2)
        quizStats.setUniqueQuizzesSolved(2)

        def str = "QuizStats{" +
                "id=" + 1 +
                ", numQuizzes=" + 2 +
                ", uniqueQuizzesSolved=" + 2 +
                ", averageQuizzesSolved=" + 2.0 +
                '}';

        then: "quizStats toString is correct"
        quizStats.toString() == str
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