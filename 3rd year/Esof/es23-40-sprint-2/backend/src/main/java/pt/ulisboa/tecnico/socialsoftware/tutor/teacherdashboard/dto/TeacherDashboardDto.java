package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;
    private List<StudentStatsDto> studentStatsDtos;
    private List<QuizStatsDto> quizStatsDtoList;
    private List<QuestionStatsDto> questionStatsDtos;
    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public List<StudentStatsDto> getStudentStatsDto() {
        return studentStatsDtos;
    }

    public void setStudentStatsDtos(List<StudentStatsDto> studentStatsDtos) {
        this.studentStatsDtos = studentStatsDtos;
    }

    public List<QuizStatsDto> getQuizStatsDtoList() {
        return this.quizStatsDtoList;
    }

    public void setQuizStatsDtoList(List<QuizStatsDto> quizStatsDtoList) {
        this.quizStatsDtoList = quizStatsDtoList;
    }

    public List<QuestionStatsDto> getQuestionStatsDtos() {
        return questionStatsDtos;
    }

    public void setQuestionStatsDtos(List<QuestionStatsDto> questionStatsDtos) {
        this.questionStatsDtos = questionStatsDtos;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                "}";
    }
}
