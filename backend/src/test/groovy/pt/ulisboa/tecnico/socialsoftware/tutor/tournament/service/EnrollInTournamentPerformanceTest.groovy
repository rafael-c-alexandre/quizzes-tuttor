package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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
class EnrollInTournamentPerformanceTest extends Specification {

    static final Integer KEY = 0
    static final User.Role ROLE = User.Role.STUDENT
    static final String TOURNAMENT_TITLE = "Tournament"
    static final String CREATION_DATE = "2020-09-22 12:12"
    static final String AVAILABLE_DATE = "2020-09-23 12:12"
    static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final Integer CICLES = 50


    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    def "performance testing to enroll 500 users in 1 tournament"() {
        given: "a tournament"

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def topic = new Topic()
        topic.setName("name")
        topicRepository.save(topic)

        def topiclist = new ArrayList<Integer>()
        topiclist.add(1)

        def tournament = new TournamentDto()
        tournament.setTitle(TOURNAMENT_TITLE)
        tournament.setAvailableDate(AVAILABLE_DATE)
        tournament.setConclusionDate(CONCLUSION_DATE)
        tournament.setCreationDate(CREATION_DATE)

        tournament = new Tournament(tournament)
        tournament.setCourseExecution(courseExecution)

        tournamentRepository.save(tournament)


        and: "500 users"

        ArrayList<User> userList = new ArrayList<User>()
        1.upto(CICLES, {

            def user = new User()
            user.setKey(KEY + it.intValue())
            user.setRole(ROLE)
            userList.add(user)
            userRepository.save(user)


        })

        when:
        1.upto(CICLES, {
            println(userList.get(it.intValue() - 1))
            tournamentService.enrollInTournament(1, userList.get(it.intValue() - 1).getId())
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