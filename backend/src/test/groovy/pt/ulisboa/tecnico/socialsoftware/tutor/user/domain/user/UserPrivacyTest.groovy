package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import spock.lang.Specification

@DataJpaTest
class UserPrivacyTest extends Specification {

    @Autowired
    UserService userService

    @Autowired
    UserRepository userRepository

    User user
    def setup(){
       user = new User()
       user.setKey(1)
       userRepository.save(user)
    }

    def "Change user privacy"(){
        when: "Changed privacy"
        userService.changeUserDashboardPrivacy(user.getId())
        then: "User has private info"
        !user.getPublicDashboards()
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        UserService userService() {
            return new UserService()
        }
    }

}
