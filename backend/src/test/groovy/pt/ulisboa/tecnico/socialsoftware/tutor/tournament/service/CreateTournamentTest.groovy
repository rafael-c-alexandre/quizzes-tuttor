package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.apache.tomcat.jni.Local
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament.TournamentState
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.logging.Handler


@DataJpaTest
class CreateTournamentTest extends Specification{
    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Integer ID = 2
    public static final TournamentState STATE = TournamentState.OPEN
    public static final User USER = new User("Pedro","Minorca",2, User.Role.STUDENT)







    def setup(){
        adminService = new AdministrationService()

    }

    def "successfully create a tournament"(){
  /*      given: "a tournament"
        def tournament = new Tournament()
        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate()




*/
        expect: false
    }

    def "empty tournament title"(){
        /*given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(null)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        adminService.createTournamentTest(tournamentDto)

        then:
        thrown(TutorException)*/
        expect: false

    }

    def "invalid tournament date"(){

        expect: false
    }

    def "create tournament with no topics"(){

        expect: false
    }

    def "empty creation date"(){

        expect: false
    }

    def "empty available date"(){

        expect: false
    }

    def "empty conclusion date"(){

        expect: false
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}