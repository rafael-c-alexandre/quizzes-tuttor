package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class OpenTournamentTest extends Specification{
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Tournament.TournamentState STATE = Tournament.TournamentState.CREATED
    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    UserRepository userRepository

    def formatter
    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    }


    def "Successfully open tournament"(){
        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        given: "a tournament"
        def tournament = new Tournament();
        tournament.setState(STATE)
        tournament.setAvailableDate(LocalDateTime.parse(AVAILABLE_DATE,formatter))
        tournament.setConclusionDate(LocalDateTime.parse(CONCLUSION_DATE,formatter))
        tournament.setCreationDate(LocalDateTime.parse(CREATION_DATE,formatter))
        tournament.setTournamentCreator(user)

        tournamentRepository.save(tournament)

        tournamentService.openTournament(1,1)

        expect:
        def tourList = tournamentRepository.findTournamentsByState("OPEN")
        tourList.size() == 1
    }

    def "Open an already open tournament"(){

        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        given: "a tournament"
        def tournament = new Tournament();
        tournament.setState(Tournament.TournamentState.OPEN)
        tournament.setAvailableDate(LocalDateTime.parse(AVAILABLE_DATE,formatter))
        tournament.setConclusionDate(LocalDateTime.parse(CONCLUSION_DATE,formatter))
        tournament.setCreationDate(LocalDateTime.parse(CREATION_DATE,formatter))
        tournament.setTournamentCreator(user)

        tournamentRepository.save(tournament)

        when:
        tournamentService.openTournament(2,2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ALREADY_OPEN

    }

    def "Open Closed Tournament"(){

        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        given: "a tournament"
        def tournament = new Tournament();
        tournament.setState(Tournament.TournamentState.CLOSED)
        tournament.setAvailableDate(LocalDateTime.parse(AVAILABLE_DATE,formatter))
        tournament.setConclusionDate(LocalDateTime.parse(CONCLUSION_DATE,formatter))
        tournament.setCreationDate(LocalDateTime.parse(CREATION_DATE,formatter))
        tournament.setTournamentCreator(user)

        tournamentRepository.save(tournament)

        when:
        tournamentService.openTournament(3,3)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ALREADY_CLOSED
    }


    def "Non creator opening tournament"(){

        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        def user1 = new User()
        user1.setKey(2)
        userRepository.save(user1)

        given: "a tournament"
        def tournament = new Tournament();
        tournament.setState(STATE)
        tournament.setAvailableDate(LocalDateTime.parse(AVAILABLE_DATE,formatter))
        tournament.setConclusionDate(LocalDateTime.parse(CONCLUSION_DATE,formatter))
        tournament.setCreationDate(LocalDateTime.parse(CREATION_DATE,formatter))
        tournament.setTournamentCreator(user)

        tournamentRepository.save(tournament)

        when:
        tournamentService.openTournament(4,5)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_CANCELER_IS_NOT_CREATOR

    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }

}
