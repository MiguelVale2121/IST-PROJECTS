package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class UpdateTeacherDashboardTest extends SpockTest {

    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def createQuiz () {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
    }

    def createQuizQuestion(quiz, question, seq) {
        def quizQuestion = new QuizQuestion(quiz, question, seq)
        quizQuestionRepository.save(quizQuestion)
    }

    def createQuestionStats() {
        def questionStats = new QuestionStats(teacherDashboard, externalCourseExecution)
        questionStatsRepository.save(questionStats)
        return questionStats
    }

    def createQuizStat() {
        def quizStat = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStat)
        return quizStat
    }

    def createStudent(name, user, mail) {
        def student = new Student(name, user, mail, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
    }

    def createQuestion(available=true) {
        def question = new Question()
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        if(available == true){
            question.setStatus(Question.Status.AVAILABLE)
        }
        else{
            question.setStatus(Question.Status.SUBMITTED)
        }
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionRepository.save(question)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return question;
    }

    def createQuizAnswer (user, quiz, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def createQuestionAnswer (quizQuestion, quizAnswer, sequence, correct, answered = true) {
        def questionAnswer = new QuestionAnswer ()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setSequence(sequence)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        def incorrectOption = quizQuestion.getQuestion().getQuestionDetails().getOptions().stream().filter(option -> option != correctOption).findAny().orElse(null)
        if (answered && correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        else if (answered && !correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, incorrectOption)
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def createStudentStat() {
        def studentStat = new StudentStats(teacherDashboard, externalCourseExecution)
        studentStatsRepository.save(studentStat)
        return studentStat
    }



    def "update teacher dashboard by id"() {
        given: "three students"
        def s1 = createStudent(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def s2 = createStudent(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def s3 = createStudent(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL)


        and: "four questions"
        def q1 = createQuestion()
        def q2 = createQuestion()
        def q3 = createQuestion()
        def q5 = createQuestion()

        and: "a submitted question"
        def q4 = createQuestion(false)

        and: "three quizzes"
        def quiz = createQuiz()
        def quiz1 = createQuiz()
        def quiz2 = createQuiz()

        and: "four quiz questions"
        def qq1 = createQuizQuestion(quiz, q1, 0)
        def qq2 = createQuizQuestion(quiz, q2, 1)
        def qq3 = createQuizQuestion(quiz, q3, 2)
        def qq4 = createQuizQuestion(quiz1, q5, 3)

        and: "question stats"
        def questionStats = createQuestionStats()

        and: "quiz stats"
        def quizStats = createQuizStat()

        and: "student stats"
        def studentStats = createStudentStat()

        and: "students answer questions"

        def quizAs1 = createQuizAnswer(s1, quiz)
        createQuestionAnswer(qq1, quizAs1, 0, true)
        createQuestionAnswer(qq2, quizAs1,0,true)
        createQuestionAnswer(qq3, quizAs1,0,true)

        def quizAs2 = createQuizAnswer(s2, quiz)
        createQuestionAnswer(qq1, quizAs2, 1, true)
        createQuestionAnswer(qq2, quizAs2, 0, true)
        createQuestionAnswer(qq3, quizAs2, 0, true)

        def quizAs3 = createQuizAnswer(s3, quiz1)
        createQuestionAnswer(qq1, quizAs3, 1,false)
        createQuestionAnswer(qq2, quizAs3, 0,false)
        createQuestionAnswer(qq3, quizAs3, 0,false)

        when: "the stats are updates"
        teacherDashboardService.updateTeacherDashboard(teacherDashboard.getId())

        then: "all stats are updated"
        questionStatsRepository.getById(questionStats.getId()).getAnsweredQuestionsUnique() == 3
        questionStatsRepository.getById(questionStats.getId()).getAverageQuestionsAnswered() == 3.0f
        questionStatsRepository.getById(questionStats.getId()).getNumAvailable() == 4
        quizStatsRepository.getById(quizStats.getId()).getNumQuizzes() == 3
        quizStatsRepository.getById(quizStats.getId()).getNumUniqueAnsweredQuizzes() == 2
        quizStatsRepository.getById(quizStats.getId()).getAverageQuizzesSolved() == 1.0f
        studentStatsRepository.getById(studentStats.getId()).getNumStudents() == 3
        studentStatsRepository.getById(studentStats.getId()).getNumMore75CorrectQuestions() == 2
        studentStatsRepository.getById(studentStats.getId()).getNumAtLeast3Quizzes() == 0
    }

    def "update an empty teacher dashboard by id"(){
        when: "an incorrect dashboard id is updated"
        teacherDashboardService.updateTeacherDashboard(dashboardId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]

    }

    def "update all teacher dashboard "() {
        given: "three students"
        def s1 = createStudent(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL)
        def s2 = createStudent(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL)
        def s3 = createStudent(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL)


        and: "four questions"
        def q1 = createQuestion()
        def q2 = createQuestion()
        def q3 = createQuestion()
        def q5 = createQuestion()

        and: "a submitted question"
        def q4 = createQuestion(false)

        and: "three quizzes"
        def quiz = createQuiz()
        def quiz1 = createQuiz()
        def quiz2 = createQuiz()

        and: "four quiz questions"
        def qq1 = createQuizQuestion(quiz, q1, 0)
        def qq2 = createQuizQuestion(quiz, q2, 1)
        def qq3 = createQuizQuestion(quiz, q3, 2)
        def qq4 = createQuizQuestion(quiz1, q5, 3)

        and: "question stats"
        def questionStats = createQuestionStats()

        and: "quiz stats"
        def quizStats = createQuizStat()

        and: "student stats"
        def studentStats = createStudentStat()

        and: "students answer questions"

        def quizAs1 = createQuizAnswer(s1, quiz)
        createQuestionAnswer(qq1, quizAs1, 0, true)
        createQuestionAnswer(qq2, quizAs1,0,true)
        createQuestionAnswer(qq3, quizAs1,0,true)

        def quizAs2 = createQuizAnswer(s2, quiz)
        createQuestionAnswer(qq1, quizAs2, 1, true)
        createQuestionAnswer(qq2, quizAs2, 0, true)
        createQuestionAnswer(qq3, quizAs2, 0, true)

        def quizAs3 = createQuizAnswer(s3, quiz1)
        createQuestionAnswer(qq1, quizAs3, 1,false)
        createQuestionAnswer(qq2, quizAs3, 0,false)
        createQuestionAnswer(qq3, quizAs3, 0,false)

        when: "the stats are updates"
        teacherDashboardService.updateAllTeacherDashboard()

        then: "all stats are updated for one teacher dashboard"
        teacherDashboardRepository.findAll().size() == 1L

        questionStats.getAnsweredQuestionsUnique() == 3
        questionStats.getAverageQuestionsAnswered() == 3.0f
        questionStats.getNumAvailable() == 4
        quizStats.getNumQuizzes() == 3
        quizStats.getNumUniqueAnsweredQuizzes() == 2
        quizStats.getAverageQuizzesSolved() == 1.0f
        studentStats.getNumStudents() == 3
        studentStats.getNumMore75CorrectQuestions() == 2
        studentStats.getNumAtLeast3Quizzes() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}