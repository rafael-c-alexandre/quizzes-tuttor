package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification


@DataJpaTest
class ListTournamentTest extends Specification{

    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Integer ID = 2
    public static final Tournament.TournamentState STATE = Tournament.TournamentState.OPEN
    public static final Integer USER = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    TournamentRepository tournamentRepository

    def t1 = new Tournament()
    def t2 = new Tournament()
    def t3 = new Tournament()
    def t4 = new Tournament()

    def setup(){
        t1.setState(Tournament.TournamentState.OPEN)
        t2.setState(Tournament.TournamentState.OPEN)
        t3.setState(Tournament.TournamentState.OPEN)
        t4.setState(Tournament.TournamentState.CLOSED)

        tournamentRepository.save(t1)
        tournamentRepository.save(t2)
        tournamentRepository.save(t3)
        tournamentRepository.save(t4)
    }


    def "successfully list all open tournament"(){

        given: "a list of open tournaments"
        def result = tournamentService.listTournamentsByState("OPEN")

        expect:
        result.size() == 3
        for (TournamentDto t : result) {
            t.getState() == Tournament.TournamentState.OPEN
        }
    }


    def "missing open tournament"() {

        given: "a list of all tournaments"

        def allTournaments = tournamentService.listTournaments()

        def openTournaments = tournamentService.listTournamentsByState('OPEN')

        expect:
        allTournaments.size() != 0
        openTournaments.size() != 0
        for (TournamentDto t : allTournaments) {
            if(t.getState() == Tournament.TournamentState.OPEN){
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
