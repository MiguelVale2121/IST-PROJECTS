package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacherDashboard", orphanRemoval = true)
    private List<QuizStats> quizStats = new ArrayList<>();

    @OneToMany
    private final List<StudentStats> studentStats = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<QuestionStats> questionStats = new ArrayList<>();
    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
        this.questionStats.forEach((stats -> {
            stats.setCourseExecution(courseExecution);
        }));
        this.studentStats.forEach(studentStat -> {
            studentStat.setCourseExecution(courseExecution);
        });
        this.quizStats.forEach(quizStats -> {
            quizStats.setCourseExecution(courseExecution);
        });
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public List<QuestionStats> getQuestionStats() {
        return questionStats;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public void addQuizStats(QuizStats quizStats) {
        this.quizStats.add(quizStats);
    }

    public void update() {
        this.quizStats.forEach(quizStats -> {
            quizStats.update();
        });

        this.studentStats.forEach(studentStat -> {
            studentStat.update();
        });

        this.questionStats.forEach(questionStat -> {
            questionStat.update();
        });
    }

    public void addQuestionStats(QuestionStats questionStats) {
        this.questionStats.add(questionStats);
    }



    public void addStudentStats(StudentStats stats) {
        this.studentStats.add(stats);
    }

    public List<StudentStats> getStudentStats() {
        return studentStats;
    }

    public List<QuizStats> getQuizStats() {
        return this.quizStats;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }
}
