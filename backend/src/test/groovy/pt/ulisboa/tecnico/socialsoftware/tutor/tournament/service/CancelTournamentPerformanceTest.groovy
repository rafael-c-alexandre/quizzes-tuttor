package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CancelTournamentPerformanceTest extends Specification {

    static final String TOURNAMENT_TITLE = "Tournament"
    static final String CREATION_DATE = "2020-09-22 12:12"
    static final String AVAILABLE_DATE = "2020-09-23 12:12"
    static final String CONCLUSION_DATE = "2020-09-24 12:12"

    static final Integer USER = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to cancel 500 tournaments"() {
        def topic = new Topic()
        topicRepository.save(topic)
        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        given: "500 tournaments created"
        ArrayList<TournamentDto> tournamentDtoList = new ArrayList<TournamentDto>()
        1.upto(500, {


            def topiclist = new ArrayList<Integer>()
            topiclist.add(1)

            def tournamentDto = new TournamentDto()
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
        1.upto(500, {
            println(tournamentDtoList.get(it.intValue() - 1))
            TournamentDto t = tournamentService.createTournament(tournamentDtoList.get(it.intValue() - 1))
            tournamentService.cancelTournament(t.getId(), tournamentDtoList.get(it.intValue() - 1).getTournamentCreator())
        })

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