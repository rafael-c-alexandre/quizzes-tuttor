package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private UserService userService;

    @GetMapping("/executions/{executionId}/stats/quiz")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuizStatsDto getQuizzStats(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getQuizStats(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/stats/tournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentStatsDto getTournamentStats(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getTournamentStats(user.getId(), executionId);
    }

    @GetMapping("/courses/{courseId}/stats/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionStatsDto getStudentQuestionsStats(Principal principal, @PathVariable int courseId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getStudentQuestionsStats(user.getId());
    }

    @GetMapping("/executions/{executionId}/stats/tournament/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentStatsDto getTournamentStats(@PathVariable int executionId,@PathVariable int userId) {

        return statsService.getTournamentStats(userId, executionId);
    }

    @GetMapping("/courses/{courseId}/stats/users/{userId}/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public StudentQuestionStatsDto getOtherStudentQuestionsStats(Principal principal, @PathVariable int courseId,@PathVariable Integer userId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getStudentQuestionsStats(userId);
    }

    @PutMapping("users/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity changeUserDashboardPrivacy(Principal principal){

        User user = (User)((Authentication) principal).getPrincipal();


        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        userService.changeUserDashboardPrivacy(user.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("users/privacy")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserDto getUserPrivacyStatus(Principal principal){

        User user = (User)((Authentication) principal).getPrincipal();


        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return userService.getPrivacyStatus(user.getId());


    }

    @GetMapping("/users/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<UserDto> getPublicDashboardUsers(Principal principal){
        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return userService.getPublicDashboardUsers();
    }

}