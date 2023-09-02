package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

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

@DataJpaTest
class StudentStatsTest extends TeacherDashbosrdUtilsDomain {
    def teacherDashboard

    def setup() {
        createExternalCourseAndExecution()

        def teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }

    def createExternalCourseExecution() {
        externalCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(externalCourse)

        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(externalCourseExecution)
        return externalCourseExecution
    }

    def "create empty student stat"() {
        when: "a student stat is created"
        def studentStat = createStudentStat_ST()

        then: "a student stat is persisted"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumStudents() == 0
        result.getNumMore75CorrectQuestions() == 0
        result.getNumAtLeast3Quizzes() == 0

        and: "a student stat is returned"
        studentStat.getId() != 0
        studentStat.getCourseExecution().getId() == externalCourseExecution.getId()
        studentStat.getTeacherDashboard().getId() == teacherDashboard.getId()
        studentStat.getNumStudents() == 0
        studentStat.getNumMore75CorrectQuestions() == 0
        studentStat.getNumAtLeast3Quizzes() == 0

        and: "the dashboard has a reference for the student stat"
        teacherDashboard.getStudentStats().size() == 1
        teacherDashboard.getStudentStats().contains(result)
    }

    def "persist student stats with defined fields"() {
        when: "a student stat is created"
        def studentStat = createStudentStat_ST()
        studentStat.setNumStudents(3)
        studentStat.setNumMore75CorrectQuestions(2)
        studentStat.setNumAtLeast3Quizzes(1)

        then: "a student stat is persisted"
        studentStatsRepository.count() == 1L
        def result = studentStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()
        result.getNumStudents() == 3
        result.getNumMore75CorrectQuestions() == 2
        result.getNumAtLeast3Quizzes() == 1

        and: "a student stat is returned"
        studentStat.getId() != 0
        studentStat.getCourseExecution().getId() == externalCourseExecution.getId()
        studentStat.getTeacherDashboard().getId() == teacherDashboard.getId()
        studentStat.getNumStudents() == 3
        studentStat.getNumMore75CorrectQuestions() == 2
        studentStat.getNumAtLeast3Quizzes() == 1

        and: "the dashboard has a reference for the student stat"
        teacherDashboard.getStudentStats().size() == 1
        teacherDashboard.getStudentStats().contains(result)
    }

    def "update a student stat with 2 students"() {
        given: "A StudentStat object referring to a CourseExecution with 2 students"
        def studentStats = createStudentStat_ST()
        def student1 = createStudent_ST("student1")
        def student2 = createStudent_ST("student2")

        when: "StudentStats is updated"
        studentStats.update()

        then: "The total number of students is 2"
        studentStats.getNumStudents() == 2
        studentStats.getNumMore75CorrectQuestions() == 0
        studentStats.getNumAtLeast3Quizzes() == 0
    }

    def "update a student stat with 5 students and 1 quiz"() {
        given: "A StudentStats object referring to a CourseExecution with 5 students and 1 quiz"
        def studentStats = createStudentStat_ST()
        def student1 = createStudent_ST("student1")
        def student2 = createStudent_ST("student2")
        def student3 = createStudent_ST("student3")
        def student4 = createStudent_ST("student4")
        def student5 = createStudent_ST("student5")
        def quiz = createQuiz_ST()

        when: "3 students submit the quiz with 4/4, 3/4 and 1/4 correct answers"
        def quizQuestion1 = createQuizQuestion_ST(quiz, 0)
        def quizQuestion2 = createQuizQuestion_ST(quiz, 1)
        def quizQuestion3 = createQuizQuestion_ST(quiz, 2)
        def quizQuestion4 = createQuizQuestion_ST(quiz, 3)
        
        def quizAns1 = createQuizAnswer_ST(quiz, student1)
        createQuestionAnswer_ST(quizAns1, quizQuestion1, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion2, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion3, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion4, true)

        def quizAns2 = createQuizAnswer_ST(quiz, student2)
        createQuestionAnswer_ST(quizAns2, quizQuestion1, true)
        createQuestionAnswer_ST(quizAns2, quizQuestion2, true)
        createQuestionAnswer_ST(quizAns2, quizQuestion3, true)
        createQuestionAnswer_ST(quizAns2, quizQuestion4, false)

        def quizAns3 = createQuizAnswer_ST(quiz, student3)
        createQuestionAnswer_ST(quizAns3, quizQuestion1, true)
        createQuestionAnswer_ST(quizAns3, quizQuestion2, false)
        createQuestionAnswer_ST(quizAns3, quizQuestion3, false)
        createQuestionAnswer_ST(quizAns3, quizQuestion4, false)

        and: "1 student starts the quiz but does not submit it"
        def quizAns4 = createQuizAnswer_ST(quiz, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns1, quizQuestion1, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion2, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion3, true)
        createQuestionAnswer_ST(quizAns1, quizQuestion4, true)

        and: "1 student only answers 1 question"
        def quizAns5 = createQuizAnswer_ST(quiz, student5)
        createQuestionAnswer_ST(quizAns5, quizQuestion1, true)

        and: "StudentStats is updated"
        studentStats.update()

        then: "The total number of students is 5"
        studentStats.getNumStudents() == 5

        and: "The number of students with more than 75% correct answers is 1"
        studentStats.getNumMore75CorrectQuestions() == 1

        and: "The number of students with at least 3 quizzes is 1"
        studentStats.getNumAtLeast3Quizzes() == 0
    }

    def "update a student stat with 4 quizzes and 4 students, which have 3, 1, 4 and 4 quizzes"() {
        given: "A StudentStats object referring to a CourseExecution with 4 students and 4 quizzes"
        def studentStats = createStudentStat_ST()
        def student1 = createStudent_ST("student1")
        def student2 = createStudent_ST("student2")
        def student3 = createStudent_ST("student3")
        def student4 = createStudent_ST("student4")

        def quiz1 = createQuiz_ST()
        def quizQuestion1 = createQuizQuestion_ST(quiz1, 0)

        def quiz2 = createQuiz_ST()
        def quizQuestion2 = createQuizQuestion_ST(quiz2, 0)

        def quiz3 = createQuiz_ST()
        def quizQuestion3 = createQuizQuestion_ST(quiz3, 0)

        def quiz4 = createQuiz_ST()
        def quizQuestion4 = createQuizQuestion_ST(quiz4, 0)

        when: "1 student submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer_ST(quiz1, student1)
        createQuestionAnswer_ST(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer_ST(quiz2, student1)
        createQuestionAnswer_ST(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer_ST(quiz3, student1)
        createQuestionAnswer_ST(quizAns3, quizQuestion3, true)

        and: "1 student submits 1 quiz, with 100% correct answers"
        def quizAns4 = createQuizAnswer_ST(quiz1, student2)
        createQuestionAnswer_ST(quizAns4, quizQuestion1, true)

        and: "1 student submits 4 quizzes, with 50% correct answers"
        def quizAns5 = createQuizAnswer_ST(quiz1, student3)
        createQuestionAnswer_ST(quizAns5, quizQuestion1, true)

        def quizAns6 = createQuizAnswer_ST(quiz2, student3)
        createQuestionAnswer_ST(quizAns6, quizQuestion2, true)

        def quizAns7 = createQuizAnswer_ST(quiz3, student3)
        createQuestionAnswer_ST(quizAns7, quizQuestion3, false)

        def quizAns8 = createQuizAnswer_ST(quiz4, student3)
        createQuestionAnswer_ST(quizAns8, quizQuestion4, false)

        and: "1 student answers 4 quizzes, with 100% correct answers, but does not submit them"
        def quizAns9 = createQuizAnswer_ST(quiz1, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns9, quizQuestion1, true)

        def quizAns10 = createQuizAnswer_ST(quiz2, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns10, quizQuestion2, true)

        def quizAns11 = createQuizAnswer_ST(quiz3, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns11, quizQuestion3, true)

        def quizAns12 = createQuizAnswer_ST(quiz4, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns12, quizQuestion4, true)

        and: "StudentStats is updated"
        studentStats.update()

        then: "The total number of students is 4"
        studentStats.getNumStudents() == 4

        and: "The number of students with more than 75% correct answers is 2"
        studentStats.getNumMore75CorrectQuestions() == 2

        and: "The number of students with at least 3 submitted quizzes is 2"
        studentStats.getNumAtLeast3Quizzes() == 2
    }

    def "update a teacher dashboard with 4 quizzes and 4 students, which have 3, 1, 4 and 4 quizzes"() {
        given: "A StudentStats object referring to a CourseExecution with 4 students and 4 quizzes and its teacher dashboard"
        def studentStats = createStudentStat_ST()
        def student1 = createStudent_ST("student1")
        def student2 = createStudent_ST("student2")
        def student3 = createStudent_ST("student3")
        def student4 = createStudent_ST("student4")

        def quiz1 = createQuiz_ST()
        def quizQuestion1 = createQuizQuestion_ST(quiz1, 0)

        def quiz2 = createQuiz_ST()
        def quizQuestion2 = createQuizQuestion_ST(quiz2, 0)

        def quiz3 = createQuiz_ST()
        def quizQuestion3 = createQuizQuestion_ST(quiz3, 0)

        def quiz4 = createQuiz_ST()
        def quizQuestion4 = createQuizQuestion_ST(quiz4, 0)

        when: "1 student submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer_ST(quiz1, student1)
        createQuestionAnswer_ST(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer_ST(quiz2, student1)
        createQuestionAnswer_ST(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer_ST(quiz3, student1)
        createQuestionAnswer_ST(quizAns3, quizQuestion3, true)

        and: "1 student submits 1 quiz, with 100% correct answers"
        def quizAns4 = createQuizAnswer_ST(quiz1, student2)
        createQuestionAnswer_ST(quizAns4, quizQuestion1, true)

        and: "1 student submits 4 quizzes, with 50% correct answers"
        def quizAns5 = createQuizAnswer_ST(quiz1, student3)
        createQuestionAnswer_ST(quizAns5, quizQuestion1, true)

        def quizAns6 = createQuizAnswer_ST(quiz2, student3)
        createQuestionAnswer_ST(quizAns6, quizQuestion2, true)

        def quizAns7 = createQuizAnswer_ST(quiz3, student3)
        createQuestionAnswer_ST(quizAns7, quizQuestion3, false)

        def quizAns8 = createQuizAnswer_ST(quiz4, student3)
        createQuestionAnswer_ST(quizAns8, quizQuestion4, false)

        and: "1 student answers 4 quizzes, with 100% correct answers, but does not submit them"
        def quizAns9 = createQuizAnswer_ST(quiz1, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns9, quizQuestion1, true)

        def quizAns10 = createQuizAnswer_ST(quiz2, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns10, quizQuestion2, true)

        def quizAns11 = createQuizAnswer_ST(quiz3, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns11, quizQuestion3, true)

        def quizAns12 = createQuizAnswer_ST(quiz4, student4, DateHandler.now(), false)
        createQuestionAnswer_ST(quizAns12, quizQuestion4, true)

        and: "TeacherDashboard is updated"
        teacherDashboard.update()

        then: "The total number of students is 4"
        studentStats.getNumStudents() == 4

        and: "The number of students with more than 75% correct answers is 2"
        studentStats.getNumMore75CorrectQuestions() == 2

        and: "The number of students with at least 3 submitted quizzes is 2"
        studentStats.getNumAtLeast3Quizzes() == 2
    }

    def "2 students from different course executions"() {
        given: "2 students referring to 2 different course executions"
        def courseExecution1 = createExternalCourseExecution()
        def courseExecution2 = createExternalCourseExecution()
        def student = createStudent_ST("student")
        student.addCourse(courseExecution1)
        student.addCourse(courseExecution2)
        def student2 = createStudent_ST("student2")
        student2.addCourse(courseExecution2)

        def studentStat = new StudentStats(teacherDashboard, courseExecution1)
        studentStatsRepository.save(studentStat)
        def studentStat2 = new StudentStats(teacherDashboard, courseExecution2)
        studentStatsRepository.save(studentStat)

        def quiz1 = createQuiz_ST(courseExecution1)
        def quizQuestion1 = createQuizQuestion_ST(quiz1, 0)

        def quiz2 = createQuiz_ST(courseExecution1)
        def quizQuestion2 = createQuizQuestion_ST(quiz2, 0)

        def quiz3 = createQuiz_ST(courseExecution1)
        def quizQuestion3 = createQuizQuestion_ST(quiz3, 0)

        when: "First student submits 3 quizzes with correct answers"
        def quizAns1 = createQuizAnswer_ST(quiz1, student)
        createQuestionAnswer_ST(quizAns1, quizQuestion1, true)

        def quizAns2 = createQuizAnswer_ST(quiz2, student)
        createQuestionAnswer_ST(quizAns2, quizQuestion2, true)

        def quizAns3 = createQuizAnswer_ST(quiz3, student)
        createQuestionAnswer_ST(quizAns3, quizQuestion3, true)

        and: "Both student stats are updated"
        studentStat.update()
        studentStat2.update()

        then: "The total number of students is 1 in the first course execution"
        studentStat.getNumStudents() == 1

        and: "The number of students with more than 75% correct answers is 1 in the first course execution"
        studentStat.getNumMore75CorrectQuestions() == 1

        and: "The number of students with at least 3 quizzes is 1 in the first course execution"
        studentStat.getNumAtLeast3Quizzes() == 1

        and: "The total number of students from another course execution is 1 in the second course execution"
        studentStat2.getNumStudents() == 2

        and: "The number of students with more than 75% correct answers from another course execution is 1 in the second course execution"
        studentStat2.getNumMore75CorrectQuestions() == 0

        and: "The number of students with at least 3 quizzes from another course execution is 1 in the second course execution"
        studentStat2.getNumAtLeast3Quizzes() == 0

    }

    def "remove a student stat"() {
        given: "a student stat"
        def studentStat = createStudentStat_ST()

        when: "the student stat is removed"
        studentStat.remove()
        studentStatsRepository.delete(studentStat)

        then: "the student stat is no longer persisted"
        studentStatsRepository.count() == 0L
        teacherDashboard.getStudentStats().size() == 0
    }

    def "student stat string representation"() {
        given: "a student stat"
        def studentStat = createStudentStat_ST()

        and: "all fields are defined in student stat"
        def fields = studentStat.getClass().getDeclaredFields().stream()
            .map(Field::getName)
            .toList()
        
        when: "the string representation is requested"
        def studentStatString = studentStat.toString()

        then: "the returned string contains data from all fields"
        fields.every {
            studentStatString.contains(it)
        }
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}