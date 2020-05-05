package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;

@RestController
public class TournamentController {
    private static final Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;


    //createTournament
    @PostMapping(value = "/tournaments/{executionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(Principal principal, @RequestBody TournamentDto tournamentDto, @PathVariable Integer executionId) {
        formatDates(tournamentDto);
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return this.tournamentService.createTournament(tournamentDto, user.getId(), executionId);
    }

    //listTournament
    @GetMapping("/tournaments/open")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listOpenTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return tournamentService.listOpenTournaments(user.getId());
    }

    //listTournament
    @GetMapping("/tournaments/closed")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listClosedTournaments() {
        return tournamentService.listClosedTournaments();
    }

    //listTournament
    @GetMapping("/tournaments/signable")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listSignableTournaments(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return tournamentService.listSignableTournaments(user.getId());
    }

    //listTournament
    @GetMapping("/tournaments/")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listTournaments() {
        return tournamentService.listTournaments();
    }

    //cancel tournament
    @DeleteMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto cancelTournament(Principal principal, @PathVariable Integer tournamentId) {
        logger.debug("cancelTournament tournamentId: {}: ", tournamentId);
        User user = (User) ((Authentication) principal).getPrincipal();
        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return tournamentService.cancelTournament(tournamentId, user.getId());
    }

    //enroll in tournament
    @PutMapping("/tournaments/{tournamentId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto enrollInTournament(Principal principal, @PathVariable Integer tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return tournamentService.enrollInTournament(tournamentId, user.getId());
    }

    @PutMapping("/users/privacy")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity changeUserDashboardPrivacy(Principal principal){
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) throw new TutorException(AUTHENTICATION_ERROR);

        userService.changeUserDashboardPrivacy(user.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<UserDto> getPublicDashboardUsers(){
        return userService.getPublicDashboardUsers();
    }


    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getAvailableDate() != null && !tournament.getAvailableDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")) {
            tournament.setAvailableDate(LocalDateTime.parse(tournament.getAvailableDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getConclusionDate() != null && !tournament.getConclusionDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setConclusionDate(LocalDateTime.parse(tournament.getConclusionDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }

}