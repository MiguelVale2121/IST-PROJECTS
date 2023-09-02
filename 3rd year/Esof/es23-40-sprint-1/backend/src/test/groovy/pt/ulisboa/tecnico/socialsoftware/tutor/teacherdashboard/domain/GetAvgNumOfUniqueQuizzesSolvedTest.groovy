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
class GetAvgNumOfUniqueQuizzesSolvedTest extends SpockTest {
    def student1
    def student2
    def teacher
    def teacherDashboard
    def quizStats
    def quiz1
    def quiz2
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

        quiz1 = new Quiz()
        quiz1.setKey(1)
        quiz1.setTitle("QUIZ_1_TITLE")
        quiz1.setType(Quiz.QuizType.PROPOSED.toString())
        quiz1.setCourseExecution(externalCourseExecution)
        quiz1.setOneWay(true)
        quizRepository.save(quiz1)

        quiz2 = new Quiz()
        quiz2.setKey(1)
        quiz2.setTitle("QUIZ_1_TITLE")
        quiz2.setType(Quiz.QuizType.PROPOSED.toString())
        quiz2.setCourseExecution(externalCourseExecution)
        quiz2.setOneWay(true)
        quizRepository.save(quiz2)
    }

    def "two students solved the same quiz"() {
        given: "two different students answer the same quiz"
        quizAnswer = new QuizAnswer(student1, quiz1)
        QuizAnswerRepository.save(quizAnswer)
        quiz1.addQuizAnswer(quizAnswer)
        quizAnswer = new QuizAnswer(student2, quiz1)
        QuizAnswerRepository.save(quizAnswer)
        quiz1.addQuizAnswer(quizAnswer)

        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of the average quizzes solved of the dashboard is now 1"
        teacherDashboard.getQuizStats().first().getAverageQuizzesSolved() == 1
    }

    def "two students solved a different quiz"() {
        given: "two students answer different quizzes"
        quizAnswer = new QuizAnswer(student1, quiz1)
        QuizAnswerRepository.save(quizAnswer)
        quiz1.addQuizAnswer(quizAnswer)
        quizAnswer = new QuizAnswer(student2, quiz2)
        QuizAnswerRepository.save(quizAnswer)
        quiz2.addQuizAnswer(quizAnswer)

        when: "the dashboard is updated"
        teacherDashboard.update();

        then: "the number of the average quizzes solved of the dashboard is now 1"
        teacherDashboard.getQuizStats().first().getAverageQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}