package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class GetNumOfUniqueQuizzesSolved extends SpockTest {
    def student1
    def student2
    def teacher
    def teacherDashboard
    def quizStats
    def quiz
    def quizAnswer

    def setup() {
        createExternalCourseAndExecution()

        student1 = new Student(USER_1_NAME, false)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)

        student2 = new Student(USER_2_NAME, false)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        teacher = new Teacher(USER_3_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacher.addDashboard(teacherDashboard)

        quizStats = new QuizStats(teacherDashboard, externalCourseExecution);
        teacherDashboard.addQuizStats(quizStats);

        quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("QUIZ_1_TITLE")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setOneWay(true)
        quizRepository.save(quiz)
    }

    def "no student solved the quiz"() {
        given: "no student solved the quiz"

        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of quizzes of the dashboard is now 1"
        teacherDashboard.getQuizStats().first().getUniqueQuizzesSolved() == 0
    }

    def "two students solved the same quiz"() {
        given: "two different students answer the same quiz"
        quizAnswer = new QuizAnswer(student1, quiz)
        QuizAnswerRepository.save(quizAnswer)
        quiz.addQuizAnswer(quizAnswer)
        quizAnswer = new QuizAnswer(student2, quiz)
        QuizAnswerRepository.save(quizAnswer)
        quiz.addQuizAnswer(quizAnswer)

        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of quizzes of the dashboard is now 1"
        teacherDashboard.getQuizStats().first().getUniqueQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
