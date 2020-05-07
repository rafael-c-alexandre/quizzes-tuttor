package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification


@DataJpaTest
class StudentSubmitQuestionServiceSpockTest extends Specification{
    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE";
    static final String QUESTION_CONTENT = "CONTENT_ONE";
    public static final String OPTION_CONTENT = "optionId content"

    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    QuestionsByStudentService questionByStudentService

    @Autowired
     CourseRepository courseRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    SubmissionRepository submissionRepository

    @Autowired
     QuestionRepository questionRepository;

    @Autowired
     TopicRepository topicRepository;

    @Autowired
     UserRepository userRepository;

    def course
    def submissionDto
    def optionDto
    def options

    def setup() {
        course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        submissionDto = new SubmissionDto()
        submissionDto.setKey(1)
        submissionDto.setStatus("ONHOLD")
        submissionDto.setCourseId(COURSE_ID)
        submissionDto.setJustification("")
        submissionDto.setTitle(QUESTION_TITLE)
        submissionDto.setContent(QUESTION_CONTENT)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_CONTENT)
        optionDto.setCorrect(true)
        options = new ArrayList<OptionDto>()
        options.add(optionDto)
        submissionDto.setOptions(options)
        submissionDto.setCourseId(course.getId())

    }

    def "the user is not a student"() {
        given: "a teacher user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        submissionDto.setUser(user.getId())

        when:
        questionByStudentService.studentSubmitQuestion(submissionDto,user.getId(),course.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.NOT_STUDENT_ERROR
    }

    def "question is correctly submitted"(){
        given: "a student user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        submissionDto.setUser(user.getId())

        when:
        def result = questionByStudentService.studentSubmitQuestion(submissionDto,user.getId(),course.getId())
        then: "the correct question is inside the repository"
        submissionRepository.count() == 1L
        result.getId() != null
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT
        resOption.getCorrect()
        result.status == "ONHOLD"
        result.justification == ""
        result.getUserId() == user.getId()

    }

    def "student submit question with empty title"()  {
        given: "a student user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        submissionDto.setUser(user.getId())
        submissionDto.setTitle("")

        when:
        questionByStudentService.studentSubmitQuestion(submissionDto, user.getId(),course.getId())

        then: "throw exception"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.INVALID_TITLE_FOR_QUESTION

    }
    

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService questionsByStudentService() {
            return new QuestionsByStudentService()
        }
    }

}