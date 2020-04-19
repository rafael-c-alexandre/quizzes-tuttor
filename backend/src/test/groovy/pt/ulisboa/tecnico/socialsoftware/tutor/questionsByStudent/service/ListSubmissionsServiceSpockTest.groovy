package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class ListSubmissionsServiceSpockTest extends Specification {
    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE";
    static final String QUESTION_CONTENT = "CONTENT_ONE";
    public static final String OPTION_CONTENT = "optionId content"
    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    QuestionsByStudentService listSubmissionsService

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

    def user
    def course
    def submissionDto
    def optionDto
    def options

    def setup() {
        user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        submissionDto = new SubmissionDto()
        submissionDto.setKey(KEY)
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
        submissionDto.setUser(user.getId())
        submissionDto.setCourseId(course.getId())


    }

    def "list an empty list of submissions"() {

        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user.getId())

        then: "the returned list is empty"
        result.isEmpty()
        and: "list empty"
    }



    def "list a non-empty list of submissions of size 1"() {
        given : "a submission"
        def submission = new Submission(submissionDto, user, course)
        submissionRepository.save(submission)


        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user.getId())

        then: "the returned list has 1 submission"
        result.size() == 1
        result.get(0).userId == user.getId()

    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService listSubmissionsService() {
            return new QuestionsByStudentService()
        }

    }
}
