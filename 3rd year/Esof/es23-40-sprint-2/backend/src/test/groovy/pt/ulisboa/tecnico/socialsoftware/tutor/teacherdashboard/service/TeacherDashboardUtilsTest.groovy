package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
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
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import java.time.LocalDateTime

import java.lang.reflect.Field


class StudentStatsUtilsTest extends SpockTestIT {

    def createExternalCourse(Course externalCourse, LocalDateTime time, String term) {
        def externalCourseExec = new CourseExecution(externalCourse, COURSE_1_ACRONYM, term, Course.Type.TECNICO, time)
        courseExecutionRepository.save(externalCourseExec)
    }

	def createQuiz(courseExecution) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(Quiz.QuizType.PROPOSED.toString())
        quiz.setCourseExecution(courseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    // def createStudentStat() {
    //     def studentStat = new StudentStats(teacherDashboard, externalCourseExecution)
    //     studentStatsRepository.save(studentStat)
    //     return studentStat
    // }

    def createStudent(username, courseExecution) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(courseExecution)
        userRepository.save(student)
        return student;
    }

    def createQuestion(course) {
        def newQuestion = new Question()
        newQuestion.setTitle("title")
        newQuestion.setCourse(course)
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

    def createAvailableQuestion(course, available=true) {
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

    def createQuizQuestion(quiz, sequence, course) {
        def question = createAvailableQuestion(course)
        def quizQuestion = new QuizQuestion(quiz, question, sequence)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def createQuestionAnswer(quizAnswer, quizQuestion, correct) {
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

    def createQuizAnswer(quiz, student, date = DateHandler.now(), completed = true) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)
        return quizAnswer
    }

    def maxVerification(int number) {
        if(number > 2) {
            return 3
        } 
        return number + 1
    }

    def createPreviousExecutions(int numberOfExecutions, Course externalCourse) {
        for (int i = 0; i < numberOfExecutions; i++) {
            createExternalCourse(externalCourse, DateHandler.now().minusDays(i+1), "C3" + i)
        }
    }

    def createAfterExecutions(int numberOfExecutions, Course externalCourse) {
        for (int i = 0; i < numberOfExecutions; i++) {
            createExternalCourse(externalCourse, DateHandler.now().plusDays(i+1), "C3" + i)
        }
    }
}