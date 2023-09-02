package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.FailedAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll


@DataJpaTest
class UpdateStudentStatsTest extends SpockTest {
	def teacher
	def dashboard
	def studentStats
	def student
	def studentdashboard
	def question
	def quiz
	def quizQuestion
	def questionAnswer

	def setup() {
		createExternalCourseAndExecution()

		teacher = new Teacher(USER_1_NAME, false)
		teacher.addCourse(externalCourseExecution)
		userRepository.save(teacher)

		teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
		dashboard = teacherDashboardRepository.findAll().get(0)
	}

	def 'number of students is equal to the courseExecution number of students'() {
		given:
		studentStats = new StudentStats(externalCourseExecution, dashboard)
		dashboard.addStudentStats(studentStats)
		createFakeStudent(USER_2_NAME)
		createFakeStudent(USER_3_NAME)
		
		when:
		dashboard.update()

		then:
		dashboard.getStudentStats().first().getNumStudents() == 2

	}

	def 'number of students with more than 75% questions right is equal to the set number'() {
		given:
		studentStats = new StudentStats(externalCourseExecution, dashboard)
		dashboard.addStudentStats(studentStats)
		createFakeStudentWithXCorrAnswer(USER_2_NAME, 4)
		when:
		dashboard.update()

		then:
		dashboard.getStudentStats().first().getNumMore75CorrQuestions() == 1

	}

	def 'number of students with at least 3 quizzes awnsered is equal to the set number'() {
		given:
		studentStats = new StudentStats(externalCourseExecution, dashboard)
		dashboard.addStudentStats(studentStats)
		createFakeStudentWithXCorrAnswer(USER_2_NAME, 2)
		when:
		dashboard.update()

		then:
		dashboard.getStudentStats().first().getNumAtLeast3Quizzes() == 1

	}

	def createFakeStudent(String name){
		Student student = new Student(name, false)
		StudentDashboard studentdashboard = new StudentDashboard(externalCourseExecution, student)
		externalCourseExecution.addUser(student)
		userRepository.save(student)
	}

	def createFakeStudentWithXCorrAnswer(String name, Integer right){
		Student student = new Student(name, false)
		StudentDashboard studentdashboard = new StudentDashboard(externalCourseExecution, student)
		def c = 0
		while (c < right) {
			c = c + 1
			question = createQuestion()
			quiz = createQuiz()
			quizQuestion = createQuizQuestion(quiz, question)
			questionAnswer = answerQuiz(true, true, true, quizQuestion, quiz, student)
			studentdashboard.statistics(questionAnswer.getQuizAnswer())
		}
		question = createQuestion()
        quiz = createQuiz()
        quizQuestion = createQuizQuestion(quiz, question)
		questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, student)
		studentdashboard.statistics(questionAnswer.getQuizAnswer())
		externalCourseExecution.addUser(student)
		studentDashboardRepository.save(studentdashboard)
		userRepository.save(student)
	}

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

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

        return newQuestion;
    }

    def createQuiz(type = Quiz.QuizType.TOURNAMENT.toString()) {
        def quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createQuizQuestion(quiz, question) {
        def quizQuestion = new QuizQuestion(quiz, question, 0)
        quizQuestionRepository.save(quizQuestion)
        return quizQuestion
    }

    def answerQuiz(answered, correct, completed, quizQuestion, quiz, student, date = DateHandler.now()) {
        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(completed)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        // quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        // questionAnswerRepository.save(questionAnswer)

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
        // answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }
	
    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}