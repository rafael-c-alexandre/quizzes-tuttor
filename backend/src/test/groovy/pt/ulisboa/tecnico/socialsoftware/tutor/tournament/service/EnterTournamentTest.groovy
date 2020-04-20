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
class EnterTournamentTest extends Specification{
    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Integer ID = 2
    public static final Integer USER = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def setup(){

    }


    def "Teacher enters in an open tournament"(){

        given: "a tournament"
        def tournament = new Tournament()
        tournamentRepository.save(tournament)


        and: "a teacher"
        def teacher = new User('name', "username", 32, User.Role.TEACHER)
        userRepository.save(teacher)

        when:
        tournamentService.enrollInTournament(1, 1)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_NOT_STUDENT
    }


    def "Admin enters in an open tournament"(){
        given: "a tournament"
        def tournament = new Tournament()
        tournamentRepository.save(tournament)


        and: "an admin"
        def admin = new User('name', "username", 32, User.Role.ADMIN)
        userRepository.save(admin)

        when:
        tournamentService.enrollInTournament(2, 2)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_NOT_STUDENT

    }

    def "Demo admin enters in an open tournament"(){
        given: "a tournament"
        def tournament = new Tournament()
        tournamentRepository.save(tournament)

        and: "a demo"
        def demo = new User('name', "username", 32, User.Role.DEMO_ADMIN)
        userRepository.save(demo)

        when:
        tournamentService.enrollInTournament(3, 3)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_NOT_STUDENT


    }

    def "Reentering an already enrolled tournament"() {
        given: "a tournament"
        def tournament = new TournamentDto()
        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setAvailableDate(AVAILABLE_DATE)
        tournament.setCreationDate(CREATION_DATE)
        tournament.setConclusionDate(CONCLUSION_DATE)
        tournamentRepository.save(new Tournament(tournament))

        and: "a student"
        def student = new User('name', "username", 32, User.Role.STUDENT)
        userRepository.save(student)

        tournamentService.enrollInTournament(4, 4)

        when:
        tournamentService.enrollInTournament(4, 4)


        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_IS_ALREADY_ENROLLED

    }

        private TournamentDto getTournamentDto() {
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setTournamentCreator(USER)
        return tournamentDto
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}