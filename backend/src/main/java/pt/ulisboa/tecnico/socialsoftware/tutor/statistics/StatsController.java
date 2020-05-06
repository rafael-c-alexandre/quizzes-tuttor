package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

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

    @GetMapping("/executions/{executionId}/stats/tournament/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentStatsDto getTournamentStats(@PathVariable int executionId,@PathVariable int userId) {

        return statsService.getTournamentStats(userId, executionId);
    }



    //2 diferentes
    //1 vai ter como param um principal e um userid + execution
    //1 vai ter como param um principal só + execution

}