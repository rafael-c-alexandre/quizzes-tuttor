package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament.TournamentState
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

@DataJpaTest
class ListTournamentTest extends Specification{

    @Autowired
    TournamentService tournamentService

    def setup(){


    }


    def "successfully list all open tournament"(){
        given: "a list of open tournaments"
        def result = tournamentService.listOpenTournaments()

        //foreach tournament check if its open and TODO: valid

        expect:
        for (TournamentDto t : result) {
            t.isOpen()
        }

    }


    def "missing open tournament"() {
        given: "a list of all tournaments"
        def allTournaments = tournamentService.listAllTournaments()
        and:
        def openTournaments = tournamentService.listOpenTournaments()

        expect:
        for (TournamentDto t : allTournaments) {
            if(t.isOpen()){
                openTournaments.contains(t)
            }
        }
    }

        @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}
