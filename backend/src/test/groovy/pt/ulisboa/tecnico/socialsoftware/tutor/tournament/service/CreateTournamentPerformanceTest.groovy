package pt.ulisboa.tecnico.socialsoftware.tutor.administration.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament.TournamentState
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.*
import spock.lang.Specification
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {
    static final String NAME = "Pedro"
    static final String USERNAME = "Skr"
    static final Integer KEY = 10

    static final String TOURNAMENT_TITLE = "Tournament"
    static final String CREATION_DATE = "2020-09-22 12:12"
    static final String AVAILABLE_DATE = "2020-09-23 12:12"
    static final String CONCLUSION_DATE = "2020-09-24 12:12"
    static final Integer ID = 2
    static final Tournament.TournamentState STATE = Tournament.TournamentState.OPEN
    static final User USER = new User("Pedro","Minorca",2, User.Role.STUDENT)

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to create 1000 tournaments"() {

        given: "500 tournaments created"
        ArrayList<TournamentDto> tournamentDtoList = new ArrayList<TournamentDto>()
        1.upto(500, {
            def topic = new Topic()
            topic.setId(1 + it.intValue())
            topicRepository.save(topic)

            def topicDto = new TopicDto()
            topicDto.setId(topic.getId())

            def topiclist = new ArrayList<TopicDto>()
            topiclist.add(topicDto)

            def tournamentDto = new TournamentDto()
            tournamentDto.setId(ID)
            tournamentDto.setTitle(TOURNAMENT_TITLE)
            tournamentDto.setCreationDate(CREATION_DATE)
            tournamentDto.setAvailableDate(AVAILABLE_DATE)
            tournamentDto.setConclusionDate(CONCLUSION_DATE)
            tournamentDto.setState(STATE);
            tournamentDto.setTournamentCreator(USER);
            tournamentDto.setTopics(topiclist);
            tournamentDtoList.add(tournamentDto)

        })

        when:
        1.upto(500,{
            println(tournamentDtoList.get(it.intValue()-1))
            tournamentService.createTournament(tournamentDtoList.get(it.intValue()-1),USER.getId())})

            then:
            true
        }



    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

    }
}