package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.*;

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
<
    //cancel tournament
    @DeleteMapping("/tournaments/{tournamentId}/{creatorId}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public TournamentDto cancelTournament(@PathVariable Integer tournamentId,@PathVariable Integer creatorId){
        logger.debug("cancelTournament torunamentId: {}: ", tournamentId);
        logger.debug("cancelTournament creatorId: {}: ", creatorId);
        return tournamentService.cancelTournament(tournamentId,creatorId);
    }

    /*

    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public ResponseEntity removeQuestion(@PathVariable Integer questionId) throws IOException {
        logger.debug("removeQuestion questionId: {}: ", questionId);
        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;

        questionService.removeQuestion(questionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        return ResponseEntity.ok().build();
    }

     */


}