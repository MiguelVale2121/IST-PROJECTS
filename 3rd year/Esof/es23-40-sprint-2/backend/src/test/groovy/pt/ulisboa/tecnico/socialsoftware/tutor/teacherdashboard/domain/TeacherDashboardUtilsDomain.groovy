package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;
//////////////////////////////////////////////////////////////////////////////////////
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

import java.lang.reflect.Field
import spock.lang.Unroll;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthDemoUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthExternalUser
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser


class TeacherDashbosrdUtilsDomain extends SpockTest {

    /* Student stats */ 

    def createQuiz_ST(courseExecution = externalCourseExecution) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createStudentStat_ST() {
        def studentStat = new StudentStats(teacherDashboard, externalCourseExecution)
        studentStatsRepository.save(studentStat)
        return studentStat
    }

    def createStudent_ST(username) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student;
    }

    def createQuestion_ST() {
        def newQuestion = new Question()
        newQuestion.setTitle("title")
        newQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)

        def option = new Option()
        option.setContent("1")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        def optionKO = new Option()
        optionKO.setContent("2")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)
        questionDetailsRepository.save(questionDetails)
        optionRepository.save(option)
        optionRepository.save(optionKO)
        return newQuestion
    }

    def createQuizQuestion_ST(quiz, sequence) {
        def question = createQuestion_ST()
        def quizQuestion = new QuizQuestion(quiz, question, sequence)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def createQuestionAnswer_ST(quizAnswer, quizQuestion, correct) {
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)

        def option
        if(correct) {
            option = quizQuestion.getQuestion().getQuestionDetails().getOptions().get(0)
        }
        else {
            option = quizQuestion.getQuestion().getQuestionDetails().getOptions().get(1)
        }
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, option)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def createQuizAnswer_ST(quiz, student, date = DateHandler.now(), completed = true) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
        return quizAnswer
    }

    /* Question stats */

    def createQuiz_QS() {
        def quiz = new Quiz()
        quiz.setKey(1)
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
    }

    def createQuizQuestion_QS(quiz, question, seq) {
        def quizQuestion = new QuizQuestion(quiz, question, seq)
        quizQuestionRepository.save(quizQuestion)
    } 

    def createQuestionStats_QS() {
        def questionStats = new QuestionStats(teacherDashboard, externalCourseExecution)
        questionStatsRepository.save(questionStats)
        return questionStats
    }

    def createStudent_QS(name, user, mail) {
        def student = new Student(name, user, mail, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
    }

    def createQuestion_QS(key, available=true) {
        def question = new Question()
        question.setTitle("Question Title")
        question.setCourse(externalCourse)
        if(available == true){
            question.setStatus(Question.Status.AVAILABLE)
        }
        else{
            question.setStatus(Question.Status.SUBMITTED)
        }
        question.setKey(key)
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

    def createQuizAnswer_QS(user, quiz, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
    }

    def createQuestionAnswer_QS(quizQuestion, quizAnswer, sequence, correct, answered = true) {
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


    /* Quiz stats */

    def createQuizStat() {
        def quizStat = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStat)
        return quizStat
    }

    def createQuiz(type = Quiz.QuizType.PROPOSED.toString()) {
        // Quiz
        quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)

        // Add Question
        def question = createQuestion()
        quizQuestion = createQuizQuestion(quiz, question)

        return quiz
    }

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle(QUESTION_1_TITLE)
        newQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent(OPTION_1_CONTENT)
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(quizQuestion, quiz, student, completed = true, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        def correctOption = quizQuestion.getQuestion().getQuestionDetails().getCorrectOption()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, correctOption)
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    def createStudent(username) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student;
    }
}