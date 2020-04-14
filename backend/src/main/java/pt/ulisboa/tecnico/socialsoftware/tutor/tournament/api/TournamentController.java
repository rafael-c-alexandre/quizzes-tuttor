package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        formatDates(tournamentDto);
        return this.tournamentService.createTournament(tournamentDto);
    }

    //listTournament
    @GetMapping("/tournaments/open")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<TournamentDto> listOpenTournaments() {
        return tournamentService.listOpenTournaments();
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

    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getAvailableDate() != null && !tournament.getAvailableDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setAvailableDate(LocalDateTime.parse(tournament.getAvailableDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getConclusionDate() !=null && !tournament.getConclusionDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setConclusionDate(LocalDateTime.parse(tournament.getConclusionDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }

}