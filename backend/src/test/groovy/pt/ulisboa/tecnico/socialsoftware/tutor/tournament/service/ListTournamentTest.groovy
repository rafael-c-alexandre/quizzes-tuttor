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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

import javax.persistence.EntityManager

@DataJpaTest
class ListTournamentTest extends Specification{

    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final Integer ID = 2
    public static final Tournament.TournamentState STATE = Tournament.TournamentState.OPEN
    public static final User USER = new User("Pedro","Minorca",2, User.Role.STUDENT)

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository
    


    def setup(){
        topicRepository.deleteAll()
        def topic1 = new Topic()
        def topic2 = new Topic()

        topic1.setName("YO")
        topic2.setName("YA")
        topicRepository.save(topic1)
        topicRepository.save(topic2)


        def topicDto1 = new TopicDto()
        topicDto1.setId(topic1.getId())
        def topicDto2 = new TopicDto()
        topicDto2.setId(topic2.getId())

        topicDto1.setName(topic1.getName())
        topicDto2.setName(topic2.getName())

        def topiclist = new ArrayList<TopicDto>()
        topiclist.add(topicDto1)
        topiclist.add(topicDto2)

        def topic3 = new Topic()
        def topic4 = new Topic()

        topic3.setName("3")
        topic4.setName("4")
        topicRepository.save(topic3)
        topicRepository.save(topic4)


        def topicDto3 = new TopicDto()
        topicDto3.setId(topic3.getId())
        def topicDto4 = new TopicDto()
        topicDto4.setId(topic4.getId())

        topicDto1.setName(topic3.getName())
        topicDto2.setName(topic4.getName())

        def topiclist2 = new ArrayList<TopicDto>()
        topiclist2.add(topicDto3)
        topiclist2.add(topicDto4)

        def tournamentDto = new TournamentDto()

        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setAvailableDate(AVAILABLE_DATE)
        tournamentDto.setConclusionDate(CONCLUSION_DATE)
        tournamentDto.setCreationDate(CREATION_DATE)
        tournamentDto.setId(ID)
        tournamentDto.setState(STATE)
        tournamentDto.setTournametCreator(USER)
        tournamentDto.setTopics(topiclist)

        def tournamentDto2 = new TournamentDto()
        tournamentDto2.setTitle(TOURNAMENT_TITLE)
        tournamentDto2.setAvailableDate(AVAILABLE_DATE)
        tournamentDto2.setConclusionDate(CONCLUSION_DATE)
        tournamentDto2.setCreationDate(CREATION_DATE)
        tournamentDto2.setId(3)
        tournamentDto2.setState(Tournament.TournamentState.CLOSED)
        tournamentDto2.setTopics(topiclist2)

        System.out.println(topicRepository.findAll())

        tournamentService.createTournament(tournamentDto)
        tournamentService.createTournament(tournamentDto2)


    }


    def "successfully list all open tournament"(){
        given: "a list of open tournaments"
        def result = tournamentService.listTournamentsByState("OPEN")


        expect:
        result.size() == 1
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
