package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

public class StudentStatsDto {

    int numStudents;
    int numAtLeast3Quizzes;
    int numMore75CorrectQuestions;

    public StudentStatsDto() {
    }

    public int getNumStudents() {
        return numStudents;
    }

    public int getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public void setNumMore75CorrectQuestions(int numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }
}
