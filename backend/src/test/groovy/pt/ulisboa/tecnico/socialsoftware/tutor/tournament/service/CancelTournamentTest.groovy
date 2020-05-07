package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CancelTournamentTest extends Specification {

    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer ID = 2
    public static final Integer USER = 1


    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TournamentService tournamentService

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    def setup() {

    }

    def "cancel existing tournament by creator"() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def topicList = new ArrayList<TopicDto>()

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                1, USER, topicList)

        def user = new User()
        user.setKey(1)
        userRepository.save(user)

        tournamentService.createTournament(tournamentDto, USER, courseExecution.getId())

        when:
        tournamentService.cancelTournament(1, USER)

        then:
        tournamentRepository.findById(1).isEmpty()

    }

    def "cancel non existing tournamet"() {

        when:
        tournamentService.cancelTournament(1, null)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_ID_NOT_EXISTS

    }

    def "cancel tournament with invalid user"() {

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)


        def tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)

        when:

        tournamentService.cancelTournament(tournamentRepository.findAll().get(0).getId(), 30)


        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

    }


    private static getTournamentDto(String title, String availableDate, String conclusionDate, String creationDate,
                                    Integer id, Integer creator, List<TopicDto> topicList) {

        TournamentDto tournamentDto = new TournamentDto()
        tournamentDto.setTitle(title)
        tournamentDto.setAvailableDate(availableDate)
        tournamentDto.setConclusionDate(conclusionDate)
        tournamentDto.setCreationDate(creationDate)
        tournamentDto.setId(id)
        tournamentDto.setTournamentCreator(creator)
        tournamentDto.setTopics(topicList)

        return tournamentDto
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }
    }
}