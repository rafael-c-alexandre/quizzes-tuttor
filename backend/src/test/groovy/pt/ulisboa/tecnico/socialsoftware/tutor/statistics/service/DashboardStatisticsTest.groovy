package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service


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
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository


import spock.lang.Specification

import java.time.format.DateTimeFormatter

@DataJpaTest
class DashboardStatisticsTest extends Specification{
    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TNMT_ID = 2
    public static final Integer USER_ID = 2


    @Autowired
    TournamentService tournamentService

    @Autowired
    StatsService statsService

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


    def user1, user2
    def tournament
    def courseExecutionId
    def setup(){
        user1 = new User()
        user2 = new User()
        user1.setRole(User.Role.STUDENT)
        user2.setRole(User.Role.STUDENT)
        user1.setKey(1)
        user2.setKey(2)

        userRepository.save(user1)
        userRepository.save(user2)

        //User 2 creates a Tournament
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseExecutionId = courseExecution.getId()
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        def topic = new Topic()
        topic.setName("TOPICO")
        topicRepository.save(topic)

        def topicList = new ArrayList<TopicDto>()
        topicList.add(new TopicDto((topic)))


        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                TNMT_ID, USER_ID, topicList)

        tournament = tournamentService.createTournament(tournamentDto, user2.getId(), courseExecutionId)
    }

    def "testar calculo de  torneios signed"(){
        when: "an enrollment by user 1"

        System.out.println(tournament.getId())
        tournamentService.enrollInTournament(1, user1.getId())

        then: "1 signed tournaments must be equal to the number retrieved"
        def tournamentStatsDto = statsService.getTournamentStats(user1.getId(), courseExecutionId)
        tournamentStatsDto.getSignedTournaments() == 1
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

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }

}
