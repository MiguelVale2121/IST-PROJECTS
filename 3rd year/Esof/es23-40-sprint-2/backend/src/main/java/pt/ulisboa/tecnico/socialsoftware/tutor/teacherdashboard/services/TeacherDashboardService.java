package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.StudentStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuizStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.QuestionStatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;

    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.map(TeacherDashboardDto::new)
                .orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream()
                .anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);
        teacherDashboardRepository.save(teacherDashboard);
        TeacherDashboardDto teacherDashboardDto = new TeacherDashboardDto(teacherDashboard);

        List<CourseExecution> mostRecCourseExecs = getMostRecentCourseExecutions(
                courseExecution.getCourse().getCourseExecutions(),
                courseExecution.getEndDate());

        List<StudentStatsDto> mostRecStudentStatsDto = new ArrayList<>();
        List<QuizStatsDto> mostRecentQuizzesStatsDto = new ArrayList<>();
        List<QuestionStatsDto> mostRecQuestionStatsDto = new ArrayList<>();

        mostRecCourseExecs.forEach(courseExec -> {
            StudentStats studentStats = new StudentStats(teacherDashboard, courseExec);
            QuizStats quizStats = new QuizStats(teacherDashboard, courseExec);
            QuestionStats questionStats = new QuestionStats(teacherDashboard, courseExec);

            studentStats.update();
            quizStats.update();
            questionStats.update();

            studentStatsRepository.save(studentStats);
            quizStatsRepository.save(quizStats);
            questionStatsRepository.save(questionStats);

            mostRecStudentStatsDto.add(convertStudentStatsToDto(studentStats));
            mostRecentQuizzesStatsDto.add(convertQuizzesStatsToDto(quizStats));
            mostRecQuestionStatsDto.add(convertQuestionStatsToDto(questionStats));
        });
        teacherDashboardDto.setStudentStatsDtos(mostRecStudentStatsDto);
        teacherDashboardDto.setQuizStatsDtoList(mostRecentQuizzesStatsDto);
        teacherDashboardDto.setQuestionStatsDtos(mostRecQuestionStatsDto);

        return teacherDashboardDto;
    }

    private List<CourseExecution> getMostRecentCourseExecutions(Set<CourseExecution> courseExecutions,
            LocalDateTime endDate) {
        List<CourseExecution> filtered = courseExecutions.stream()
                .filter(ce -> ce.getEndDate() != null &&
                        ce.getEndDate().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
        return filtered.stream()
                .sorted(Comparator.comparing(ce -> ce.getEndDate(), Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toList());
    }

    private StudentStatsDto convertStudentStatsToDto(StudentStats studentStat) {
        StudentStatsDto studentStatsDto = new StudentStatsDto();
        studentStatsDto.setNumStudents(studentStat.getNumStudents());
        studentStatsDto.setNumMore75CorrectQuestions(studentStat.getNumMore75CorrectQuestions());
        studentStatsDto.setNumAtLeast3Quizzes(studentStat.getNumAtLeast3Quizzes());
        return studentStatsDto;
    }

    private QuizStatsDto convertQuizzesStatsToDto(QuizStats quizStats) {
        QuizStatsDto quizStatsDto = new QuizStatsDto();
        quizStatsDto.setNumQuizzes(quizStats.getNumQuizzes());
        quizStatsDto.setNumUniqueAnsweredQuizzes(quizStats.getNumUniqueAnsweredQuizzes());
        quizStatsDto.setAverageQuizzesSolved(quizStats.getAverageQuizzesSolved());
        return quizStatsDto;
    }

    private QuestionStatsDto convertQuestionStatsToDto(QuestionStats questionStats) {
        QuestionStatsDto studentStatsDto = new QuestionStatsDto();
        studentStatsDto.setAnsweredQuestionsUnique(questionStats.getAnsweredQuestionsUnique());
        studentStatsDto.setAverageQuestionsAnswered(questionStats.getAverageQuestionsAnswered());
        studentStatsDto.setNumAvailable(questionStats.getNumAvailable());
        return studentStatsDto;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.remove();
        teacherDashboardRepository.delete(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId)
                .orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.update();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboard() {

        teacherDashboardRepository.findAll().forEach(teacherDashboard -> {
            teacherDashboard.update();
        });
    }
}
