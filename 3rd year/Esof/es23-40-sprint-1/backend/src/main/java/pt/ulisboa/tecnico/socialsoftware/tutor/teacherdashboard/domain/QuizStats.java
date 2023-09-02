package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;

@Entity
public class QuizStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    @OneToOne
    private CourseExecution courseExecution;

    private Integer numQuizzes = 0;

    private Integer uniqueQuizzesSolved = 0;

    private Float averageQuizzesSolved = (float) 0;

    public QuizStats() {
    }

    public QuizStats(TeacherDashboard teacherDashboard, CourseExecution courseExecution) {
        setTeacherDashboard(teacherDashboard);
        setCourseExecution(courseExecution);
    }

    public void update() {
        // Sets the number of available quizzes
        this.setNumQuizzes(this.courseExecution.getNumberOfQuizzes());

        int solvedQuizzes = 0;

        // Sum the number of unique quizzes that have an answer
        for (Quiz quiz : this.courseExecution.getQuizzes()) {
            //if quiz has an answer then it has been solved by a student
            if (quiz.getQuizAnswers().size() != 0) {
                solvedQuizzes++;
            }
        }
        setUniqueQuizzesSolved(solvedQuizzes);

        // Calculate the average number of unique quizzes that have an answer per student
        if (this.courseExecution.getStudents().size() == 0) {
            setAverageQuizzesSolved((float) 0);
        } else {
            //Divide the total number of answers in all quizzes by the number of students
            int sumOfStudentQuizzes = 0;

            for (Quiz quiz : this.courseExecution.getQuizzes()) {
                sumOfStudentQuizzes += quiz.getQuizAnswers().size();
            }

            setAverageQuizzesSolved((float) sumOfStudentQuizzes / this.courseExecution.getStudents().size());
        }
    }

    public Integer getId() {
        return id;
    }

    public TeacherDashboard getTeacherDashboard() {
        return this.teacherDashboard;
    }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
    }

    public CourseExecution getCourseExecution() {
        return this.courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public int getNumQuizzes() {
        return this.numQuizzes;
    }

    public void setNumQuizzes(Integer numQuizzes) {
        if (numQuizzes != null && numQuizzes >= 0)
            this.numQuizzes = numQuizzes;
    }

    public int getUniqueQuizzesSolved() {
        return this.uniqueQuizzesSolved;
    }

    public void setUniqueQuizzesSolved(Integer uniqueQuizzesSolved) {
        if (uniqueQuizzesSolved != null && uniqueQuizzesSolved >= 0)
            this.uniqueQuizzesSolved = uniqueQuizzesSolved;
    }

    public float getAverageQuizzesSolved() {
        return this.averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(Float averageQuizzesSolved) {
        if (averageQuizzesSolved != null && averageQuizzesSolved >= 0)
            this.averageQuizzesSolved = averageQuizzesSolved;
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "QuizStats{" +
                "id=" + id +
                ", numQuizzes=" + numQuizzes +
                ", uniqueQuizzesSolved=" + uniqueQuizzesSolved +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                '}';
    }
}
