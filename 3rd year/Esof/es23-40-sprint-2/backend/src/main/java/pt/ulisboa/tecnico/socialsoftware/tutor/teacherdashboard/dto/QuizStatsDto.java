package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;

public class QuizStatsDto {

    private int id;

    private int numQuizzes;

    private int numUniqueAnsweredQuizzes;

    private float averageQuizzesSolved;

    public QuizStatsDto() {
    }

    public QuizStatsDto(QuizStats quizStats) {
        this.id = quizStats.getId();
        this.numQuizzes = quizStats.getNumQuizzes();
        this.numUniqueAnsweredQuizzes = quizStats.getNumUniqueAnsweredQuizzes();
        this.averageQuizzesSolved = quizStats.getAverageQuizzesSolved();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumQuizzes() {
        return numQuizzes;
    }

    public void setNumQuizzes(Integer numQuizzes) {
        this.numQuizzes = numQuizzes;
    }

    public int getNumUniqueAnsweredQuizzes() {
        return numUniqueAnsweredQuizzes;
    }

    public void setNumUniqueAnsweredQuizzes(int numUniqueAnsweredQuizzes) {
        this.numUniqueAnsweredQuizzes = numUniqueAnsweredQuizzes;
    }

    public float getAverageQuizzesSolved() {
        return averageQuizzesSolved;
    }

    public void setAverageQuizzesSolved(float averageQuizzesSolved) {
        this.averageQuizzesSolved = averageQuizzesSolved;
    }


    @Override
    public String toString() {
        return "QuizStats{" +
                "id=" + id +
                ", numQuizzes=" + numQuizzes +
                ", numUniqueAnsweredQuizzes=" + numUniqueAnsweredQuizzes +
                ", averageQuizzesSolved=" + averageQuizzesSolved +
                "}";
    }
}
