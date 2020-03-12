package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService;

@RestController
public class TournamentController {
    @Autowired
    private TournamentService tournamentService;
}
