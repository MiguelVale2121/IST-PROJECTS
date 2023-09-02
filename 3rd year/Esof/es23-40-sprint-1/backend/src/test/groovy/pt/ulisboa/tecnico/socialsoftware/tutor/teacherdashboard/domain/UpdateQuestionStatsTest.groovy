package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question.Status
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher

@DataJpaTest
class UpdateQuestionStatsTest extends SpockTest {
    def teacher
    def dashboard
    def questionStats
    def questions
    def student1
    def student2
    def question_1
    def question_2

    def setup() {
        createExternalCourseAndExecution()
        student1 = new Student(USER_1_NAME,false)
        student2 = new Student(USER_2_NAME,false)
        teacher = new Teacher(USER_1_NAME, false)
        teacher.addCourse(externalCourseExecution)
        userRepository.save(teacher)

        teacherDashboardService.createTeacherDashboard(externalCourseExecution.getId(), teacher.getId())
        dashboard = teacherDashboardRepository.findAll().get(0)
    }

    def "number of total available questions is updated"() {
        given:
            questionStats = new QuestionStats(externalCourseExecution, dashboard)
            dashboard.addQuestionStats(questionStats)
            createFakeQuestion(QUESTION_1_TITLE, Status.AVAILABLE)
            createFakeQuestion(QUESTION_2_TITLE, Status.DISABLED)
            createFakeQuestion(QUESTION_3_TITLE, Status.AVAILABLE)

        when:
            dashboard.update()

        then:
            dashboard.getQuestionStats().first().getNumAvailable() == 2
    }

    def "update total answered unique questions"() {
        given:
        questionStats = new QuestionStats(externalCourseExecution, dashboard)
        dashboard.addQuestionStats(questionStats)
        question_1 = createFakeQuestion(QUESTION_1_TITLE, Status.AVAILABLE)
        question_2 = createFakeQuestion(QUESTION_2_TITLE, Status.AVAILABLE)
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(question_1,student1))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(question_1,student1))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(question_2,student2))

        when:
        dashboard.update()

        then:
        dashboard.getQuestionStats().first().getAnsweredQuestionUnique() == 2
    }

    def "update average answered unique question"() {
        given:
        questionStats = new QuestionStats(externalCourseExecution, dashboard)
        dashboard.addQuestionStats(questionStats)
        question_1 = createFakeQuestion(QUESTION_1_TITLE, Status.AVAILABLE)
        question_2 = createFakeQuestion(QUESTION_2_TITLE, Status.AVAILABLE)
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(question_1,student1))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(question_2,student1))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(createFakeQuestion(QUESTION_3_TITLE, Status.AVAILABLE),student1))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(createFakeQuestion(QUESTION_4_TITLE, Status.AVAILABLE),student2))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(createFakeQuestion(QUESTION_5_TITLE, Status.AVAILABLE),student2))
        externalCourseExecution.addQuestionSubmission(createFakeQuestionSubmission(createFakeQuestion("QUESTION_6_TITLE", Status.AVAILABLE),student2))

        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)

        when:
        dashboard.update()

        then:
        dashboard.getQuestionStats().first().getAverageQuestionsAnswered() == 3
    }

    def createFakeQuestion(String name, Status status) {
        Question question = new Question()
        question.setCourse(externalCourse)
        question.setTitle(name)
        question.setStatus(status)
        questionRepository.save(question)

        return question
    }

    def createFakeQuestionSubmission(Question question, Student student) {
        QuestionSubmission questionSubmission = new QuestionSubmission()
        questionSubmission.setCourseExecution(externalCourseExecution)
        questionSubmission.setQuestion(question)
        questionSubmission.setSubmitter(student)

        return questionSubmission
    }




    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}