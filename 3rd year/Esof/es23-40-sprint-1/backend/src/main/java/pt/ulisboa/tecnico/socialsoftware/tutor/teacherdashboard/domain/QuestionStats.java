package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class QuestionStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private Integer numAvailable = 0;

    private Integer answeredQuestionUnique = 0;

    private Float averageQuestionsAnswered = 0.0F;

    public QuestionStats() {
    }

    public QuestionStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard) {
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    public Integer getId() {
        return this.id;
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
    }

    public Integer getNumAvailable() {
        return numAvailable;
    }

    public void setNumAvailable(Integer numAvailable) {
        if (numAvailable != null && numAvailable >= 0) {
            this.numAvailable = numAvailable;
        }
    }

    public Integer getAnsweredQuestionUnique() {
        return answeredQuestionUnique;
    }

    public void setAnsweredQuestionUnique(Integer answeredQuestionUnique) {
        if (answeredQuestionUnique != null && answeredQuestionUnique >= 0) {
            this.answeredQuestionUnique = answeredQuestionUnique;
        }
    }

    public Float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(Float averageQuestionsAnswered) {
        if (averageQuestionsAnswered != null && averageQuestionsAnswered >= 0) {
            this.averageQuestionsAnswered = averageQuestionsAnswered;
        }
    }

    public void update() {
        Set<Student> students = courseExecution.getStudents();
        Set<Integer> submission = new HashSet<>();
        courseExecution.getQuestionSubmissions().forEach(questionSubmission -> {
            submission.add(questionSubmission.getQuestion().getId());
        });

        setNumAvailable(courseExecution.getCourse().getQuestions().stream()
                .filter(question -> question.getStatus().equals(Question.Status.AVAILABLE)).collect(Collectors.toSet())
                .size());

        setAnsweredQuestionUnique(submission.size());

        if (students.size() > 0) {
            setAverageQuestionsAnswered(((float)(submission.size()) / ((float) students.size())));
        }
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
                "id=" + id +
                ", numAvailable=" + numAvailable +
                ", answeredQuestionUnique=" + answeredQuestionUnique +
                ", averageQuestionsAnswered=" + averageQuestionsAnswered +
                '}';
    }
}
