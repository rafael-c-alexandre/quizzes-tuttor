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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateTournamentPerformanceTest extends Specification {

    static final String TOURNAMENT_TITLE = "Tournament"
    static final String CREATION_DATE = "2020-09-22 12:12"
    static final String AVAILABLE_DATE = "2020-09-23 12:12"
    static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    static final Integer ID = 2
    static final Integer USER = 1
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
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def "performance testing to create 500 tournaments"() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        def topic = new Topic()
        topic.setName("name")
        topicRepository.save(topic)
        def user = new User()
        user.setKey(1)
        userRepository.save(user)
        def s = 0

        given: "500 tournaments created"
        ArrayList<TournamentDto> tournamentDtoList = new ArrayList<TournamentDto>()
        1.upto(CICLES, {
            s++

            def topiclist = new ArrayList<TopicDto>()
            topiclist.add(new TopicDto(topic))

            def tournamentDto = new TournamentDto()
            tournamentDto.setId(ID)
            tournamentDto.setTitle(TOURNAMENT_TITLE + s)
            tournamentDto.setCreationDate(CREATION_DATE)
            tournamentDto.setAvailableDate(AVAILABLE_DATE)
            tournamentDto.setConclusionDate(CONCLUSION_DATE)
            tournamentDto.setTournamentCreator(USER)
            tournamentDto.setTopics(topiclist)
            tournamentDtoList.add(tournamentDto)

        })

        when:
        1.upto(CICLES, {
            tournamentService.createTournament(tournamentDtoList.get(it.intValue() - 1), user.getId(), courseExecution.getId())
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