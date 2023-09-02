package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

public class QuestionStatsDto {
    int numAvailable, answeredQuestionsUnique;
    float averageQuestionsAnswered;

    public QuestionStatsDto() {
    }

    public int getNumAvailable() {
        return numAvailable;
    }

    public int getAnsweredQuestionsUnique() {
        return answeredQuestionsUnique;
    }

    public float getAverageQuestionsAnswered() {
        return averageQuestionsAnswered;
    }

    public void setNumAvailable(Integer numAvailable) {
        this.numAvailable = numAvailable;
    }

    public void setAnsweredQuestionsUnique(Integer answeredQuestionsUnique) {
        this.answeredQuestionsUnique = answeredQuestionsUnique;
    }

    public void setAverageQuestionsAnswered(Float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }
}
