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

@DataJpaTest
class CancelTournamentTest extends Specification{

    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Tournament.TournamentState STATE = Tournament.TournamentState.OPEN
    public static final User USER = new User("Pedro","Minorca",2, User.Role.STUDENT)
    public static final User USER2 = new User("Afonso","afonsovdm",3, User.Role.STUDENT)


    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def setup(){
        USER.setId(1)
        USER2.setId(2)

    }

    def "cancel existing tournament by creator"(){
        def tournamentDto = new TournamentDto()
        tournamentDto.setState(STATE)
        tournamentDto.setId(1)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setTournamentCreator(USER)

        def tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)

        when:
        tournamentService.cancelTournament(tournament.getId(),USER.getId())

        then:
        tournamentRepository.findById(1).isEmpty() == true

    }

    def "cancel non existing tournamet"(){

        when:
        tournamentService.cancelTournament(1,null)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ID_NOT_EXISTS

    }

    def "cancel tournament with invalid user"(){

        def tournamentDto = new TournamentDto()
        tournamentDto.setState(STATE)
        tournamentDto.setId(1)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setTournamentCreator(USER)


        def tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)

        when:

        tournamentService.cancelTournament(tournamentRepository.findAll().get(0).getId(),30)


        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

    }

    def  "tournament canceled by non creator"(){

        def tournamentDto = new TournamentDto()

        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setTournamentCreator(USER)
        tournamentDto.setId(1)

        userRepository.save(USER)
        userRepository.save(USER2)

        def tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)

        when:
        tournamentService.cancelTournament(tournamentRepository.findAll().get(0).getId(),USER2.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_CANCELER_IS_NOT_CREATOR

    }

    def "cancel non open tournament"(){

        def tournamentDto = new TournamentDto()
        tournamentDto.setState(Tournament.TournamentState.CLOSED)
        tournamentDto.setId(1)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setTournamentCreator(USER)

        def tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)

        when:
        tournamentService.cancelTournament(tournamentRepository.findAll().get(0).getId(),USER.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ALREADY_CLOSED

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }





}