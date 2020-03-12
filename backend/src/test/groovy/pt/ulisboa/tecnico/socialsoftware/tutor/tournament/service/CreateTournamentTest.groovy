package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament.TournamentState
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.*
import spock.lang.Specification

import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification{
    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Integer ID = 2
    public static final TournamentState STATE = TournamentState.OPEN
    public static final User USER = new User("Pedro","Minorca",2, User.Role.STUDENT)


    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository





    def formatter



    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        USER.setId(1)

    }

    def "successfully create a tournament"(){
        given: "a tournamentDto"



        def topic = new Topic()
        topic.setId(1)
        topicRepository.save(topic)


        def topicDto = new TopicDto()
        topicDto.setId(topic.getId())

        def topiclist = new ArrayList<TopicDto>()
        topiclist.add(topicDto)


        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)
        tournamentDto.setTopics(topiclist)

        when:
        tournamentService.createTournament(tournamentDto)

        then: "The correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findTournaments().get(0)
        result.getId() != null
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == AVAILABLE_DATE
        result.getConclusionDate().format(formatter) == CONCLUSION_DATE
        result.getState() == STATE
        result.getTournamentCreator().getId() == USER.getId()
        result.getTitle() == TOURNAMENT_TITLE
        result.getTopics().size() == 1


    }

    def "empty tournament title"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(null)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_TITLE_IS_EMPTY

    }

    def "available date before present date"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate("2003-09-22 12:12")
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "conclusion date before present date"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate("2003-09-22 12:12")
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "conclusion date before available date"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(CONCLUSION_DATE)
        tournamentDto.setConclusionDate(AVAILABLE_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "create tournament with no topics"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_NO_TOPICS
    }

    def "empty available date"(){

        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(null)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_EMPTY_DATE


    }

    def "empty conclusion date"(){

        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(null)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)

        when:
        tournamentService.createTournament(tournamentDto)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_EMPTY_DATE

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}