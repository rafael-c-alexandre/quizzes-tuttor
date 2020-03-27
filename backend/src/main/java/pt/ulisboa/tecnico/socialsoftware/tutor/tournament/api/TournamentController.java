package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.*;

import java.util.List;

@RestController
public class TournamentController {
    private static Logger logger = LoggerFactory.getLogger(TournamentController.class);

    @Autowired
    private TournamentService tournamentService;


    //createTournament
    @PostMapping(value = "/tournaments/")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto createTournament(@RequestBody TournamentDto tournamentDto) {
        return this.tournamentService.createTournament(tournamentDto);
    }

    //listTournament
    @GetMapping("/tournaments/open")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listOpenTournaments() {
        return tournamentService.listTournamentsByState("OPEN");
    }

    //cancel tournament
    @DeleteMapping("/tournaments/{tournamentId}/{creatorId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto cancelTournament(@PathVariable Integer tournamentId,@PathVariable Integer creatorId){
        logger.debug("cancelTournament tournamentId: {}: ", tournamentId);
        logger.debug("cancelTournament creatorId: {}: ", creatorId);
        return tournamentService.cancelTournament(tournamentId,creatorId);
    }

    //enroll in tournament
    @PutMapping("/tournaments/{tournamentId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto enrollInTournament(@PathVariable Integer tournamentId,@PathVariable Integer userId){
        logger.debug("cancelTournament tournamentId: {}: ", tournamentId);
        logger.debug("cancelTournament creatorId: {}: ", userId);
        return tournamentService.enrollInTournament(tournamentId,userId);
    }

    //openTournament in tournament
    @PutMapping("/tournaments/open/{tournamentId}/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto openTournament(@PathVariable Integer tournamentId,@PathVariable Integer userId){
        logger.debug("cancelTournament tournamentId: {}: ", tournamentId);
        logger.debug("cancelTournament creatorId: {}: ", userId);
        return tournamentService.openTournament(tournamentId,userId);
    }

}