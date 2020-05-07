package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateTournamentTest extends Specification {
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

    def formatter
    def courseExecution
    def user

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        user = new User()
        user.setKey(1)
        userRepository.save(user)
    }

    def "successfully create a tournament"() {
        given: "a tournamentDto"

        def topic = new Topic()
        topic.setName("TOPICO")
        topicRepository.save(topic)

        def topicList = new ArrayList<TopicDto>()
        topicList.add(new TopicDto((topic)))


        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                ID, USER, topicList)

        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then: "The correct tournament is inside the repository"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findTournaments().get(0)
        result.getId() != null
        result.getCreationDate() != null
        result.getAvailableDate().format(formatter) == AVAILABLE_DATE
        result.getConclusionDate().format(formatter) == CONCLUSION_DATE
        result.getTournamentCreator().getId() == USER
        result.getTitle() == TOURNAMENT_TITLE
        result.getTopics().size() == 1


    }

    def "empty tournament title"() {
        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(null,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)
        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_TITLE_IS_EMPTY

    }

    def "tournament with Invalid courseExecution "() {
        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)
        when:
        tournamentService.createTournament(tournamentDto, user.getId(), 1000)
        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

    }

    def "available date before present date"() {
        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                "2003-09-22 12:12", CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)

        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "conclusion date before present date"() {
        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                "2003-09-22 12:12", CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)
        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "conclusion date before available date"() {
        given: "a tournamentDto"
        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE, CONCLUSION_DATE, AVAILABLE_DATE, CREATION_DATE,
                ID, USER, null)

        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_INVALID_DATE

    }

    def "empty available date"() {

        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE, null, CONCLUSION_DATE, CREATION_DATE,
                ID, USER, null)

        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_EMPTY_DATE


    }

    def "empty conclusion date"() {

        given: "a tournamentDto"

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE, AVAILABLE_DATE, null, CREATION_DATE,
                ID, USER, null)

        when:
        tournamentService.createTournament(tournamentDto, user.getId(), courseExecution.getId())

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TOURNAMENT_EMPTY_DATE

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
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }

}