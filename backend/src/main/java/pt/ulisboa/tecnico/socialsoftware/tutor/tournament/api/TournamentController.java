package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;


import javax.validation.Valid;
import java.util.List;

@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;


    //createTournament
    @PostMapping(value = "/tournament/created/")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament(@Valid @RequestBody TournamentDto tournamentDto) {
        tournamentDto.setState(Tournament.TournamentState.CLOSED);
        return this.tournamentService.createTournament(tournamentDto);
    }

    //listTournament
    @GetMapping("/tournaments/open")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listOpenTournaments() {
        return tournamentService.listTournamentsByState("OPEN");
    }

}