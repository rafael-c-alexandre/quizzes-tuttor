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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class EnrollInTournamentPerformanceTest extends Specification {

    static final String NAME = "Name"
    static final String USERNAME = "Username"
    static final String KEY = 1
    static final User.Role ROLE = User.Role.STUDENT


    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to enroll 500 users in 1 tournament"() {
        given: "a tournament"

        def topic = new Topic()
        topicRepository.save(topic)

        def topiclist = new ArrayList<Integer>()
        topiclist.add(1)

        def tournament = new Tournament()
        tournamentRepository.save(tournament)


        and: "500 users"

        ArrayList<UserDto> userDtoList = new ArrayList<UserDto>()
        1.upto(500, {

            def user = new User()
            user.setName(NAME)
            user.setUsername(USERNAME)
            user.setKey(KEY+it.intValue())
            user.setRole(ROLE)
            userDtoList.add(user)


        })

        when:
        1.upto(500,{
            println(userDtoList.get(it.intValue()-1))
            tournamentService.createTournament()
            tournamentService.enrollInTournament(1,userDtoList.get(it.intValue()-1).getId())})

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