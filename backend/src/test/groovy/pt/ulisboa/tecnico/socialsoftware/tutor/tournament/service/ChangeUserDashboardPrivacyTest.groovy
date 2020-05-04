package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ChangeUserDashboardPrivacyTest extends Specification{

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentService tournamentService

    User user
    def setup(){
        user = new User()
        user.setKey(0)

        userRepository.save(user)

    }

    def "Change user dashboard privacy"(){
        when:
        tournamentService.changeUserDashboardPrivacy(user.getId());
        then:
        !user.getPublicTournamentDashboard()
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

    }

}