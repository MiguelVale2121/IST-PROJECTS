package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services.TeacherDashboardService;

import java.security.Principal;

@RestController
public class TeacherDashboardController {
    @Autowired
    private TeacherDashboardService teacherDashboardService;

    TeacherDashboardController(TeacherDashboardService teacherDashboardService) {
        this.teacherDashboardService = teacherDashboardService;
    }

    @GetMapping("/teachers/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public TeacherDashboardDto getTeacherDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int teacherId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return teacherDashboardService.getTeacherDashboard(courseExecutionId, teacherId);
    }

    @DeleteMapping("/teachers/dashboards/executions/removeDashboard/{dashboardId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void removeTeacherDashboard(@PathVariable int dashboardId) {
        teacherDashboardService.removeTeacherDashboard(dashboardId);
    }

    @PutMapping("/teachers/dashboards/executions/updateDashboard/{dashboardId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public void updateTeacherDashboard(@PathVariable int dashboardId) {
        teacherDashboardService.updateTeacherDashboard(dashboardId);
    }

    @PostMapping("/teachers/dashboards/executions/updateAllTeacherDashboards")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateAllTeacherDashboards() {
        teacherDashboardService.updateAllTeacherDashboard();
    }
}
