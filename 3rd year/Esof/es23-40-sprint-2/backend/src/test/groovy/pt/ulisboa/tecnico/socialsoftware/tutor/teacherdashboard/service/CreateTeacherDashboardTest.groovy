package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import java.util.stream.Collectors;
import java.time.LocalDateTime
import spock.lang.Unroll

@DataJpaTest
    class CreateTeacherDashboardTest extends StudentStatsUtilsTest {
    def teacher

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
    }

    def "create an empty dashboard"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        when: "a dashboard is created"
        teacherDashboardService.getTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getId() != 0
        teacherDashboard.getCourseExecution().getId() == externalCourseExecution.getId()
        teacherDashboard.getTeacher().getId() == teacher.getId()

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(teacherDashboard)
    }

    @Unroll
    def "create dashboard with #numberOfExecutions previous executions"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)
        when:
        createPreviousExecutions(numberOfExecutions, externalCourseExecution.getCourse())
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        then:
        teacherDashboardRepository.count() == 1L
        // The verification should be +1 but it's +2 because of the DemoCourse
        courseExecutionRepository.count() == numberOfExecutions + 2
        and:
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        def result = maxVerification(numberOfExecutions)
        teacherDashboard.getId() != 0
        teacherDashboard.getCourseExecution().getId() == externalCourseExecution.getId()
        teacherDashboard.getTeacher().getId() == teacher.getId()
        studentStatsRepository.count() == result
        teacherDashboard.getStudentStats().size() == result
        quizStatsRepository.count() == result
        teacherDashboard.getQuizStats().size() == result
        questionStatsRepository.count() == result
        teacherDashboard.getQuestionStats().size() == result
        where:
        numberOfExecutions << [0, 1, 2, 3, 7]
    }

    @Unroll
    def "create dashboard with #numberOfExecutions after executions"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)
        when:
        createAfterExecutions(numberOfExecutions, externalCourseExecution.getCourse())
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        then:
        teacherDashboardRepository.count() == 1L
        // The verification should be +1 but it's +2 because of the DemoCourse
        courseExecutionRepository.count() == numberOfExecutions + 2
        and:
        def teacherDashboard = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getId() != 0
        teacherDashboard.getCourseExecution().getId() == externalCourseExecution.getId()
        teacherDashboard.getTeacher().getId() == teacher.getId()
        studentStatsRepository.count() == 1
        teacherDashboard.getStudentStats().size() == 1
        quizStatsRepository.count() == 1
        teacherDashboard.getQuizStats().size() == 1
        questionStatsRepository.count() == 1
        teacherDashboard.getQuestionStats().size() == 1
        where:
        numberOfExecutions << [0, 1, 2, 3, 7]
    }

    def "update a courseExecution with 4 quizzes and 4 students, which have 3, 1, 4 and 4 quizzes"() {
        given: "A StudentStats object referring to a CourseExecution with 4 students and 4 quizzes"
        teacher.addCourse(externalCourseExecution)
        def course1 = externalCourseExecution.getCourse()
        def student1 = createStudent("student1", externalCourseExecution)
        def student2 = createStudent("student2", externalCourseExecution)
        def student3 = createStudent("student3", externalCourseExecution)
        def student4 = createStudent("student4", externalCourseExecution)

        def quiz1 = createQuiz(externalCourseExecution)
        def quizQuestion1 = createQuizQuestion(quiz1, 0, course1)

        def quiz2 = createQuiz(externalCourseExecution)
        def quizQuestion2 = createQuizQuestion(quiz2, 0, course1)

        def quiz3 = createQuiz(externalCourseExecution)
        def quizQuestion3 = createQuizQuestion(quiz3, 0, course1)

        def quiz4 = createQuiz(externalCourseExecution)
        def quizQuestion4 = createQuizQuestion(quiz4, 0, course1)

        when: "1 student submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer(quiz1, student1)
        createQuestionAnswer(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer(quiz2, student1)
        createQuestionAnswer(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer(quiz3, student1)
        createQuestionAnswer(quizAns3, quizQuestion3, true)

        and: "1 student submits 1 quiz, with 100% correct answers"
        def quizAns4 = createQuizAnswer(quiz1, student2)
        createQuestionAnswer(quizAns4, quizQuestion1, true)

        and: "1 student submits 4 quizzes, with 50% correct answers"
        def quizAns5 = createQuizAnswer(quiz1, student3)
        createQuestionAnswer(quizAns5, quizQuestion1, true)

        def quizAns6 = createQuizAnswer(quiz2, student3)
        createQuestionAnswer(quizAns6, quizQuestion2, true)

        def quizAns7 = createQuizAnswer(quiz3, student3)
        createQuestionAnswer(quizAns7, quizQuestion3, false)

        def quizAns8 = createQuizAnswer(quiz4, student3)
        createQuestionAnswer(quizAns8, quizQuestion4, false)

        and: "1 student answers 4 quizzes, with 100% correct answers, but does not submit them"
        def quizAns9 = createQuizAnswer(quiz1, student4, DateHandler.now(), false)
        createQuestionAnswer(quizAns9, quizQuestion1, true)

        def quizAns10 = createQuizAnswer(quiz2, student4, DateHandler.now(), false)
        createQuestionAnswer(quizAns10, quizQuestion2, true)

        def quizAns11 = createQuizAnswer(quiz3, student4, DateHandler.now(), false)
        createQuestionAnswer(quizAns11, quizQuestion3, true)

        def quizAns12 = createQuizAnswer(quiz4, student4, DateHandler.now(), false)
        createQuestionAnswer(quizAns12, quizQuestion4, true)

        and: "TeacherDashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "The total number of students is 4"
        def studentStats = teacherDashboardRepository.findAll().get(0).getStudentStats().get(0)
        def quizStats = teacherDashboardRepository.findAll().get(0).getQuizStats().get(0)
        def questionStats = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(0)
        
        studentStatsRepository.findAll().get(0).getNumStudents() == 4
        studentStats.getNumStudents() == 4

        and: "The number of students with more than 75% correct answers is 2"
        studentStatsRepository.findAll().get(0).getNumMore75CorrectQuestions() == 2
        studentStats.getNumMore75CorrectQuestions() == 2
        
        and: "The number of students with at least 3 submitted quizzes is 2"
        studentStatsRepository.findAll().get(0).getNumAtLeast3Quizzes() == 2
        studentStats.getNumAtLeast3Quizzes() == 2

        and: "The total number of quizzes is 4"
        quizStatsRepository.findAll().get(0).getNumQuizzes() == 4
        quizStats.getNumQuizzes() == 4
        
        and: "The number of unique answered quizzes is 4"
        quizStatsRepository.findAll().get(0).getNumUniqueAnsweredQuizzes() == 4
        quizStats.getNumUniqueAnsweredQuizzes() == 4

        and: "The number of average quizzes solved is 2"
        quizStatsRepository.findAll().get(0).getAverageQuizzesSolved() == 2f
        quizStats.getAverageQuizzesSolved() == 2f

        and: "The number of available questions is 4"
        questionStatsRepository.findAll().get(0).getNumAvailable() == 4
        questionStats.getNumAvailable() == 4

        and: "The number of answered questions is 4"
        questionStatsRepository.findAll().get(0).getAnsweredQuestionsUnique() == 4
        questionStats.getAnsweredQuestionsUnique() == 4

        and: "The number of average questions answered is 3.0"
        questionStatsRepository.findAll().get(0).getAverageQuestionsAnswered() == 3f
        questionStats.getAverageQuestionsAnswered() == 3f
    }

    def "update a courseExecution with 3 quizzes and 3 students and previous courseExec with 1 quiz and 1 student"() {
        given: "A StudentStats object referring to a CourseExecution with 4 students and 4 quizzes"
        teacher.addCourse(externalCourseExecution)
        createPreviousExecutions(1, externalCourseExecution.getCourse())
        def externalCourseExecution1 = courseExecutionRepository.findAll().get(2)
        def course1 = externalCourseExecution.getCourse()
        def student1 = createStudent("student1", externalCourseExecution)
        def student2 = createStudent("student2", externalCourseExecution)
        def student3 = createStudent("student3", externalCourseExecution)
        def student4 = createStudent("student4", externalCourseExecution1)

        def quiz1 = createQuiz(externalCourseExecution)
        def quizQuestion1 = createQuizQuestion(quiz1, 0, course1)

        def quiz2 = createQuiz(externalCourseExecution)
        def quizQuestion2 = createQuizQuestion(quiz2, 0, course1)

        def quiz3 = createQuiz(externalCourseExecution)
        def quizQuestion3 = createQuizQuestion(quiz3, 0, course1)

        def quiz4 = createQuiz(externalCourseExecution1)
        def quizQuestion4 = createQuizQuestion(quiz4, 0, course1)

        when: "1 student submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer(quiz1, student1)
        createQuestionAnswer(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer(quiz2, student1)
        createQuestionAnswer(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer(quiz3, student1)
        createQuestionAnswer(quizAns3, quizQuestion3, true)

        and: "1 student submits 1 quiz, with 100% correct answers"
        def quizAns4 = createQuizAnswer(quiz1, student2)
        createQuestionAnswer(quizAns4, quizQuestion1, true)

        and: "1 student submits 3 quizzes, with 66% correct answers"
        def quizAns5 = createQuizAnswer(quiz1, student3)
        createQuestionAnswer(quizAns5, quizQuestion1, true)

        def quizAns6 = createQuizAnswer(quiz2, student3)
        createQuestionAnswer(quizAns6, quizQuestion2, true)

        def quizAns7 = createQuizAnswer(quiz3, student3)
        createQuestionAnswer(quizAns7, quizQuestion3, false)

        and: "1 student answers 1 quiz, with 100% correct answers, but does not submit them"
        def quizAns8 = createQuizAnswer(quiz4, student4)
        createQuestionAnswer(quizAns8, quizQuestion4, true)

        and: "TeacherDashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "The total number of students is 3 for original courseExec"
        def studentStats = teacherDashboardRepository.findAll().get(0).getStudentStats().get(0)
        def quizStats = teacherDashboardRepository.findAll().get(0).getQuizStats().get(0)
        def questionStats = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(0)
        
        studentStatsRepository.findAll().get(0).getNumStudents() == 3
        studentStats.getNumStudents() == 3

        and: "The number of students with more than 75% correct answers is 2 for original courseExec"
        studentStatsRepository.findAll().get(0).getNumMore75CorrectQuestions() == 2
        studentStats.getNumMore75CorrectQuestions() == 2
        
        and: "The number of students with at least 3 submitted quizzes is 2 for original courseExec"
        studentStatsRepository.findAll().get(0).getNumAtLeast3Quizzes() == 2
        studentStats.getNumAtLeast3Quizzes() == 2

        and: "The total number of quizzes is 3 for original courseExec"
        quizStatsRepository.findAll().get(0).getNumQuizzes() == 3
        quizStats.getNumQuizzes() == 3
        
        and: "The number of unique answered quizzes is 3 for original courseExec"
        quizStatsRepository.findAll().get(0).getNumUniqueAnsweredQuizzes() == 3
        quizStats.getNumUniqueAnsweredQuizzes() == 3

        and: "The number of average quizzes solved is 7/3 for original courseExec"
        quizStatsRepository.findAll().get(0).getAverageQuizzesSolved() == (float)2.3333333
        quizStats.getAverageQuizzesSolved() == (float)2.3333333

        and: "The number of available questions is 3 for original courseExec"
        questionStatsRepository.findAll().get(0).getNumAvailable() == 3
        questionStats.getNumAvailable() == 3

        and: "The number of answered questions is 3 for original courseExec"
        questionStatsRepository.findAll().get(0).getAnsweredQuestionsUnique() == 3
        questionStats.getAnsweredQuestionsUnique() == 3

        and: "The number of average questions answered is 7/3 for original courseExec"
        questionStatsRepository.findAll().get(0).getAverageQuestionsAnswered() == (float)2.3333333
        questionStats.getAverageQuestionsAnswered() == (float)2.33333333

        and: "The total number of students is 1 for original courseExec"
        def studentStats1 = teacherDashboardRepository.findAll().get(0).getStudentStats().get(1)
        def quizStats1 = teacherDashboardRepository.findAll().get(0).getQuizStats().get(1)
        def questionStats1 = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(1)
        
        studentStatsRepository.findAll().get(1).getNumStudents() == 1
        studentStats1.getNumStudents() == 1

        and: "The number of students with more than 75% correct answers is 0 for previous courseExec"
        studentStatsRepository.findAll().get(1).getNumMore75CorrectQuestions() == 1
        studentStats1.getNumMore75CorrectQuestions() == 1
        
        and: "The number of students with at least 3 submitted quizzes is 0 for original courseExec"
        studentStatsRepository.findAll().get(1).getNumAtLeast3Quizzes() == 0
        studentStats1.getNumAtLeast3Quizzes() == 0

        and: "The total number of quizzes is 1 for original courseExec"
        quizStatsRepository.findAll().get(1).getNumQuizzes() == 1
        quizStats1.getNumQuizzes() == 1
        
        and: "The number of unique answered quizzes is 1 for original courseExec"
        quizStatsRepository.findAll().get(1).getNumUniqueAnsweredQuizzes() == 1
        quizStats1.getNumUniqueAnsweredQuizzes() == 1

        and: "The number of average quizzes solved is 1 for original courseExec"
        quizStatsRepository.findAll().get(1).getAverageQuizzesSolved() == 1f
        quizStats1.getAverageQuizzesSolved() == 1f

        and: "The number of available questions is 1 for original courseExec"
        questionStatsRepository.findAll().get(1).getNumAvailable() == 1
        questionStats1.getNumAvailable() == 1

        and: "The number of answered questions is 1 for original courseExec"
        questionStatsRepository.findAll().get(1).getAnsweredQuestionsUnique() == 1
        questionStats1.getAnsweredQuestionsUnique() == 1

        and: "The number of average questions answered is 1 for original courseExec"
        questionStatsRepository.findAll().get(1).getAverageQuestionsAnswered() == 1f
        questionStats1.getAverageQuestionsAnswered() == 1f
        
    }

    def "update a courseExecution with 3 quizzes and 3 students and 2 previous courseExec  one with 1 quiz and 1 student another with 2 quizzes and 3 student"() {
        given: "A StudentStats object referring to a CourseExecution with 4 students and 4 quizzes"
        teacher.addCourse(externalCourseExecution)
        createPreviousExecutions(2, externalCourseExecution.getCourse())
        def externalCourseExecution1 = courseExecutionRepository.findAll().get(2)
        def externalCourseExecution2 = courseExecutionRepository.findAll().get(3)
        def course1 = externalCourseExecution.getCourse()
        def student1 = createStudent("student1", externalCourseExecution)
        def student2 = createStudent("student2", externalCourseExecution)
        def student3 = createStudent("student3", externalCourseExecution)
        def student4 = createStudent("student4", externalCourseExecution1)
        def student5 = createStudent("student5", externalCourseExecution2)
        def student6 = createStudent("student6", externalCourseExecution2)
        def student7 = createStudent("student7", externalCourseExecution2)

        def quiz1 = createQuiz(externalCourseExecution)
        def quizQuestion1 = createQuizQuestion(quiz1, 0, course1)

        def quiz2 = createQuiz(externalCourseExecution)
        def quizQuestion2 = createQuizQuestion(quiz2, 0, course1)

        def quiz3 = createQuiz(externalCourseExecution)
        def quizQuestion3 = createQuizQuestion(quiz3, 0, course1)

        def quiz4 = createQuiz(externalCourseExecution1)
        def quizQuestion4 = createQuizQuestion(quiz4, 0, course1)

        def quiz5 = createQuiz(externalCourseExecution2)
        def quizQuestion5 = createQuizQuestion(quiz5, 0, course1)

        def quiz6 = createQuiz(externalCourseExecution2)
        def quizQuestion6 = createQuizQuestion(quiz6, 0, course1)

        when: "1 student from courseExec submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer(quiz1, student1)
        createQuestionAnswer(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer(quiz2, student1)
        createQuestionAnswer(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer(quiz3, student1)
        createQuestionAnswer(quizAns3, quizQuestion3, true)

        and: "1 student from courseExec submits 1 quiz, with 100% correct answers"
        def quizAns4 = createQuizAnswer(quiz1, student2)
        createQuestionAnswer(quizAns4, quizQuestion1, true)

        and: "1 student from courseExec submits 3 quizzes, with 66% correct answers"
        def quizAns5 = createQuizAnswer(quiz1, student3)
        createQuestionAnswer(quizAns5, quizQuestion1, true)

        def quizAns6 = createQuizAnswer(quiz2, student3)
        createQuestionAnswer(quizAns6, quizQuestion2, true)

        def quizAns7 = createQuizAnswer(quiz3, student3)
        createQuestionAnswer(quizAns7, quizQuestion3, false)

        and: "1 student from courseExec1 answers 1 quiz, with 100% correct answers"
        def quizAns8 = createQuizAnswer(quiz4, student4)
        createQuestionAnswer(quizAns8, quizQuestion4, true)

        and: "1 student from courseExec2 answers 2 quizzes, with 100% correct answers"
        def quizAns9 = createQuizAnswer(quiz5, student5)
        createQuestionAnswer(quizAns9, quizQuestion5, true)

        def quizAns10 = createQuizAnswer(quiz6, student5)
        createQuestionAnswer(quizAns10, quizQuestion6, true)

        and: "1 student from courseExec2 answers 2 quizzes, with 50% correct answers"
        def quizAns11 = createQuizAnswer(quiz5, student6)
        createQuestionAnswer(quizAns11, quizQuestion5, true)

        def quizAns12 = createQuizAnswer(quiz6, student6)
        createQuestionAnswer(quizAns12, quizQuestion6, false)

        and: "1 student from courseExec2 answers 2 quizzes, with 50% correct answers"
        def quizAns13 = createQuizAnswer(quiz5, student7)
        createQuestionAnswer(quizAns13, quizQuestion5, true)

        def quizAns14 = createQuizAnswer(quiz6, student7)
        createQuestionAnswer(quizAns14, quizQuestion6, false)

        and: "TeacherDashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "The total number of students is 3 for original courseExec"
        def studentStats = teacherDashboardRepository.findAll().get(0).getStudentStats().get(0)
        def quizStats = teacherDashboardRepository.findAll().get(0).getQuizStats().get(0)
        def questionStats = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(0)
        
        studentStatsRepository.findAll().get(0).getNumStudents() == 3
        studentStats.getNumStudents() == 3

        and: "The number of students with more than 75% correct answers is 2 for original courseExec"
        studentStatsRepository.findAll().get(0).getNumMore75CorrectQuestions() == 2
        studentStats.getNumMore75CorrectQuestions() == 2
        
        and: "The number of students with at least 3 submitted quizzes is 2 for original courseExec"
        studentStatsRepository.findAll().get(0).getNumAtLeast3Quizzes() == 2
        studentStats.getNumAtLeast3Quizzes() == 2

        and: "The total number of quizzes is 3 for original courseExec"
        quizStatsRepository.findAll().get(0).getNumQuizzes() == 3
        quizStats.getNumQuizzes() == 3
        
        and: "The number of unique answered quizzes is 3 for original courseExec"
        quizStatsRepository.findAll().get(0).getNumUniqueAnsweredQuizzes() == 3
        quizStats.getNumUniqueAnsweredQuizzes() == 3

        and: "The number of average quizzes solved is 7/3 for original courseExec"
        quizStatsRepository.findAll().get(0).getAverageQuizzesSolved() == (float)2.3333333
        quizStats.getAverageQuizzesSolved() == (float)2.3333333

        and: "The number of available questions is 3 for original courseExec"
        questionStatsRepository.findAll().get(0).getNumAvailable() == 3
        questionStats.getNumAvailable() == 3

        and: "The number of answered questions is 3 for original courseExec"
        questionStatsRepository.findAll().get(0).getAnsweredQuestionsUnique() == 3
        questionStats.getAnsweredQuestionsUnique() == 3

        and: "The number of average questions answered is 7/3 for original courseExec"
        questionStatsRepository.findAll().get(0).getAverageQuestionsAnswered() == (float)2.3333333
        questionStats.getAverageQuestionsAnswered() == (float)2.33333333

        and: "The total number of students is 1 for courseExec1"
        def studentStats1 = teacherDashboardRepository.findAll().get(0).getStudentStats().get(1)
        def quizStats1 = teacherDashboardRepository.findAll().get(0).getQuizStats().get(1)
        def questionStats1 = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(1)
        
        studentStatsRepository.findAll().get(1).getNumStudents() == 1
        studentStats1.getNumStudents() == 1

        and: "The number of students with more than 75% correct answers is 0 for courseExec1"
        studentStatsRepository.findAll().get(1).getNumMore75CorrectQuestions() == 1
        studentStats1.getNumMore75CorrectQuestions() == 1
        
        and: "The number of students with at least 3 submitted quizzes is 0 for courseExec1"
        studentStatsRepository.findAll().get(1).getNumAtLeast3Quizzes() == 0
        studentStats1.getNumAtLeast3Quizzes() == 0

        and: "The total number of quizzes is 1 for courseExec1"
        quizStatsRepository.findAll().get(1).getNumQuizzes() == 1
        quizStats1.getNumQuizzes() == 1
        
        and: "The number of unique answered quizzes is 1 for courseExec1"
        quizStatsRepository.findAll().get(1).getNumUniqueAnsweredQuizzes() == 1
        quizStats1.getNumUniqueAnsweredQuizzes() == 1

        and: "The number of average quizzes solved is 1 for courseExec1"
        quizStatsRepository.findAll().get(1).getAverageQuizzesSolved() == 1f
        quizStats1.getAverageQuizzesSolved() == 1f

        and: "The number of available questions is 1 for courseExec1"
        questionStatsRepository.findAll().get(1).getNumAvailable() == 1
        questionStats1.getNumAvailable() == 1

        and: "The number of answered questions is 1 for courseExec1"
        questionStatsRepository.findAll().get(1).getAnsweredQuestionsUnique() == 1
        questionStats1.getAnsweredQuestionsUnique() == 1

        and: "The number of average questions answered is 1 for courseExec1"
        questionStatsRepository.findAll().get(1).getAverageQuestionsAnswered() == 1f
        questionStats1.getAverageQuestionsAnswered() == 1f

        and: "The total number of students is 3 for courseExec2"
        def studentStats2 = teacherDashboardRepository.findAll().get(0).getStudentStats().get(2)
        def quizStats2 = teacherDashboardRepository.findAll().get(0).getQuizStats().get(2)
        def questionStats2 = teacherDashboardRepository.findAll().get(0).getQuestionStats().get(2)
        
        studentStatsRepository.findAll().get(2).getNumStudents() == 3
        studentStats2.getNumStudents() == 3

        and: "The number of students with more than 75% correct answers is 1 for courseExec2"
        studentStatsRepository.findAll().get(2).getNumMore75CorrectQuestions() == 1
        studentStats2.getNumMore75CorrectQuestions() == 1
        
        and: "The number of students with at least 3 submitted quizzes is 0 for courseExec2"
        studentStatsRepository.findAll().get(2).getNumAtLeast3Quizzes() == 0
        studentStats2.getNumAtLeast3Quizzes() == 0

        and: "The total number of quizzes is 2 for courseExec2"
        quizStatsRepository.findAll().get(2).getNumQuizzes() == 2
        quizStats2.getNumQuizzes() == 2
        
        and: "The number of unique answered quizzes is 2 for courseExec2"
        quizStatsRepository.findAll().get(2).getNumUniqueAnsweredQuizzes() == 2
        quizStats2.getNumUniqueAnsweredQuizzes() == 2

        and: "The number of average quizzes solved is 2 for courseExec2"
        quizStatsRepository.findAll().get(2).getAverageQuizzesSolved() == 2f
        quizStats2.getAverageQuizzesSolved() == 2f

        and: "The number of available questions is 2 for courseExec2"
        questionStatsRepository.findAll().get(2).getNumAvailable() == 2
        questionStats2.getNumAvailable() == 2

        and: "The number of answered questions is 2 for courseExec2"
        questionStatsRepository.findAll().get(2).getAnsweredQuestionsUnique() == 2
        questionStats2.getAnsweredQuestionsUnique() == 2

        and: "The number of average questions answered is 2 for courseExec2"
        questionStatsRepository.findAll().get(2).getAverageQuestionsAnswered() == 2f
        questionStats2.getAverageQuestionsAnswered() == 2f
        
    }

    def "cannot create multiple dashboards for a teacher on a course execution"() {
        given: "a teacher in a course execution"
        teacher.addCourse(externalCourseExecution)

        and: "an empty dashboard for the teacher"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        when: "a second dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "there is only one dashboard"
        teacherDashboardRepository.count() == 1L

        and: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_ALREADY_HAS_DASHBOARD
    }

    def "cannot create a dashboard for a user that does not belong to the course execution"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())

        then: "exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot create a dashboard with courseExecutionId=#courseExecutionId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(courseExecutionId, teacher.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        where:
        courseExecutionId << [0, 100]
    }

    @Unroll
    def "cannot create a dashboard with teacherId=#teacherId"() {
        when: "a dashboard is created"
        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacherId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        where:
        teacherId << [0, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
