package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;

import java.util.Iterator;
import java.util.Set;

import javax.persistence.*;

@Entity
public class StudentStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private TeacherDashboard dashboard;

    @OneToOne
    private CourseExecution courseExecution;

    private Integer numStudents;

    private Integer numMore75CorrectQuestions;

    private Integer numAtLeast3Quizzes;

    public StudentStats() {
    }

    public StudentStats(CourseExecution courseExecution, TeacherDashboard dashboard) {
        this.courseExecution = courseExecution;
        this.dashboard = dashboard;
        this.numStudents = 0;
        this.numMore75CorrectQuestions = 0;
        this.numAtLeast3Quizzes = 0;
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() {
        return dashboard;
    }

    public void setTeacherDashboard(TeacherDashboard dashboard) {
        this.dashboard = dashboard;
    }

    public Integer getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(Integer numStudents) {
        if (numStudents != null && numStudents >= 0) {
            this.numStudents = numStudents;
        }
    }

    public Integer getNumMore75CorrQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrQuestions(Integer numMore75CorrectQuestions) {
        if (numMore75CorrectQuestions != null && numMore75CorrectQuestions >= 0) {
            this.numMore75CorrectQuestions = numMore75CorrectQuestions;
        }
    }

    public Integer getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(Integer numAtLeast3Quizzes) {
        if (numAtLeast3Quizzes != null && numAtLeast3Quizzes >= 0) {
            this.numAtLeast3Quizzes = numAtLeast3Quizzes;
        }
    }

    public void update() {
        Set<Student> students = courseExecution.getStudents();
        setNumStudents((Integer) students.size());
        numMore75CorrectQuestions = 0;
        numAtLeast3Quizzes = 0;
        Iterator<Student> itr = students.iterator();
        while (itr.hasNext()) {
            Student st = itr.next();
            StudentDashboard dashboard = st.getCourseExecutionDashboard(courseExecution);
            double results;
            if (dashboard.getNumberOfStudentAnswers() == 0) {
                results = 0.0;
            } else {
                results = (dashboard.getNumberOfCorrectStudentAnswers()
                        / (double) dashboard.getNumberOfStudentAnswers());
            }
            if (results > 0.75) {
                setNumMore75CorrQuestions(getNumMore75CorrQuestions() + 1);
            }
            if (dashboard.getNumberOfStudentQuizzes() >= 3) {
                numAtLeast3Quizzes++;
            }
        }
    }

    @Override
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }

}
