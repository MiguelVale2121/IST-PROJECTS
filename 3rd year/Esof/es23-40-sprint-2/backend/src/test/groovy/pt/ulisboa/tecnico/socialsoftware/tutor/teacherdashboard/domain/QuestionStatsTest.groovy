package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration;
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class QuestionStatsTest extends TeacherDashbosrdUtilsDomain {

    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def "create an empty question stats"() {
        when: "a question stat is created"
        def questionStat = createQuestionStats_QS()

        then: "a question stat is persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        
        and: "a question stat is returned"
        result.getNumAvailable() == 0
        result.getAnsweredQuestionsUnique() == 0
        result.getAverageQuestionsAnswered() == 0.0f

        and: "the dashboard has a reference for the question stat"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    def "update a question stats without students"() {
        given: "a question stat"
        def questionStat = createQuestionStats_QS()

        when: "a question stat is updated"
        questionStat.update()

        then: "a question stat is persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        
        and: "a question stat is returned"
        result.getNumAvailable() == 0
        result.getAnsweredQuestionsUnique() == 0
        result.getAverageQuestionsAnswered() == 0.0f

        and: "the dashboard has a reference for the question stat"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)
    }

    def "update a question stats with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent_QS(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent_QS(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion_QS(1)
        def q2 = createQuestion_QS(2)
        def q3 = createQuestion_QS(3)

        and: "a submitted question"
        def q4 = createQuestion_QS(4, false)
        
        and: "a quiz"
        def quiz = createQuiz_QS()
        def qq1 = createQuizQuestion_QS(quiz, q1, 0)
        def qq2 = createQuizQuestion_QS(quiz, q2, 1)
        def qq3 = createQuizQuestion_QS(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats_QS()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer_QS(s1, quiz)        
        def quizAs2 = createQuizAnswer_QS(s2, quiz)
        createQuestionAnswer_QS(qq1, quizAs1, 0, true)
        createQuestionAnswer_QS(qq2, quizAs1, 1, false)
        createQuestionAnswer_QS(qq2, quizAs2, 0, true)

        when: "the stats are updates"
        stats.update()

        then: "the stats are correct"
        stats.getAnsweredQuestionsUnique() == 2
        stats.getAverageQuestionsAnswered() == (float)3/2
        stats.getNumAvailable() == 3
    }

    def "generate a string of question stats after update with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent_QS(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent_QS(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion_QS(1)
        def q2 = createQuestion_QS(2)
        def q3 = createQuestion_QS(3)

        and: "a submitted question"
        def q4 = createQuestion_QS(4, false)
        
        and: "a quiz"
        def quiz = createQuiz_QS()
        def qq1 = createQuizQuestion_QS(quiz, q1, 0)
        def qq2 = createQuizQuestion_QS(quiz, q2, 1)
        def qq3 = createQuizQuestion_QS(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats_QS()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer_QS(s1, quiz)        
        def quizAs2 = createQuizAnswer_QS(s2, quiz)
        createQuestionAnswer_QS(qq1, quizAs1, 0, true)
        createQuestionAnswer_QS(qq2, quizAs1, 1, false)
        createQuestionAnswer_QS(qq2, quizAs2, 0, true)

        when: "the stats are updates"
        stats.update()
        def res = stats.toString()

        then: "the stats are correct"

        res.equals ("QuestionStats{" +
            "id=" + stats.getId() +
            ", teacherDashboard=" + teacherDashboard.getId() +
            ", courseExecution=" + externalCourseExecution.getId() +
            ", numAvailable=" + 3 +
            ", answeredQuestionsUnique=" + 2 +
            ", averageQuestionsAnswered=" + (float) 3/2 +
            '}');
    }

    def "remove a question stat"() {
        given: "a question stat"
        def stats = createQuestionStats_QS()

        when: "the question stat is removed"
        stats.remove()
        questionStatsRepository.delete(stats)

        then: "the question stat is removed"
        questionStatsRepository.count() == 0L
        teacherDashboard.getQuestionStats().size() == 0
    }

    def "update with 3 available questions and 2 students"() {
        given: "two students"
        def s1 = createStudent_QS(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL) 
        def s2 = createStudent_QS(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)

        and: "three questions"
        def q1 = createQuestion_QS(1)
        def q2 = createQuestion_QS(2)
        def q3 = createQuestion_QS(3)

        and: "a submitted question"
        def q4 = createQuestion_QS(4, false)
        
        and: "a quiz"
        def quiz = createQuiz_QS()
        def qq1 = createQuizQuestion_QS(quiz, q1, 0)
        def qq2 = createQuizQuestion_QS(quiz, q2, 1)
        def qq3 = createQuizQuestion_QS(quiz, q3, 2)

        and: "question stats"
        def stats = createQuestionStats_QS()

        and: "students answer questions"
        def quizAs1 = createQuizAnswer_QS(s1, quiz)        
        def quizAs2 = createQuizAnswer_QS(s2, quiz)
        createQuestionAnswer_QS(qq1, quizAs1, 0, true)
        createQuestionAnswer_QS(qq2, quizAs1, 1, false)
        createQuestionAnswer_QS(qq2, quizAs2, 0, true)

        when: "the dashboard is updated"
        teacherDashboard.update()

        then: "the stats are correct"
        def statsLst = teacherDashboard.getQuestionStats()
        statsLst.size() == 1
        def stats2 = statsLst.get(0)
        stats2.getAnsweredQuestionsUnique() == 2
        stats2.getAverageQuestionsAnswered() == (float)3/2
        stats2.getNumAvailable() == 3
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
