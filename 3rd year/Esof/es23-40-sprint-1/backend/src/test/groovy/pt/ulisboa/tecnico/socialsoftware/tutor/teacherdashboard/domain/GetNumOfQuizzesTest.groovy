package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import java.util.concurrent.ThreadLocalRandom

@DataJpaTest
class GetNumOfQuizzesTest extends SpockTest {
    def teacher
    def teacherDashboard
    def quizStats

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacher.addDashboard(teacherDashboard)

        quizStats = new QuizStats(teacherDashboard, externalCourseExecution);
        teacherDashboard.addQuizStats(quizStats);
    }

    def "create zero quizzes"() {
        given:
        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of quizzes of the dashboard is now 0"
        teacherDashboard.getQuizStats().first().getNumQuizzes() == 0
    }

    def "create two quizzes"() {
        given: "2 new quizzes"
        Quiz quiz1 = new Quiz()
        createFakeQuiz(quiz1, 1, "QUIZ_1_TITLE");
        Quiz quiz2 = new Quiz()
        createFakeQuiz(quiz2, 2, "QUIZ_2_TITLE");

        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of quizzes of the dashboard is now 2"
        teacherDashboard.getQuizStats().first().getNumQuizzes() == 2
    }

    def "create one quiz and then remove it"() {
        given: "1 new quiz"
        Quiz quiz = new Quiz()
        createFakeQuiz(quiz, 1, "QUIZ_1_TITLE");
        teacherDashboard.update();
        quizService.removeQuiz(quiz.getId())

        when: "the dashboard is updated again"
        teacherDashboard.update();

        then: "the number of quizzes of the dashboard is now 0"
        teacherDashboard.getQuizStats().first().getNumQuizzes() == 0
    }

    def createFakeQuiz(Quiz quiz, Integer id, String name) {
        quiz.setKey(id)
        quiz.setTitle(name)
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}