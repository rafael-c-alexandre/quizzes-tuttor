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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification




@DataJpaTest
class UpdateSubmissionTopicsSpockTest extends Specification {

    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE"
    static final String QUESTION_CONTENT = "CONTENT_ONE"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String TOPIC_ONE = 'nameOne'
    public static final String TOPIC_TWO = 'nameTwo'
    public static final String TOPIC_THREE = 'nameThree'
    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    QuestionsByStudentService submissionService

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
    def options
    def optionDto
    def topicDtoOne
    def topicDtoTwo
    def topicDtoThree
    def topicOne
    def topicTwo
    def topicThree
    def submission


    def setup() {
        user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        submissionDto = new SubmissionDto()
        submissionDto.setId(1)
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
        submissionDto.setUser(user.getId())
        submissionDto.setCourseId(course.getId())
        submission = new Submission(submissionDto,user, course)

        topicDtoOne = new TopicDto()
        topicDtoOne.setName(TOPIC_ONE)
        topicDtoTwo = new TopicDto()
        topicDtoTwo.setName(TOPIC_TWO)
        topicDtoThree = new TopicDto()
        topicDtoThree.setName(TOPIC_THREE)

        topicOne = new Topic(course, topicDtoOne)
        topicTwo = new Topic(course, topicDtoTwo)
        submission.getTopics().add(topicOne)
        topicOne.getSubmissions().add(submission)
        submission.getTopics().add(topicTwo)
        topicTwo.getSubmissions().add(submission)
        submissionRepository.save(submission)
        topicRepository.save(topicOne)
        topicRepository.save(topicTwo)

        topicThree = new Topic(course, topicDtoThree)
        topicRepository.save(topicThree)

    }


    def "update topics of onHold submission"() {
        given: 'topic list'
        TopicDto[] topics = [topicDtoOne, topicDtoTwo, topicDtoThree]


        when:
        submissionService.updateSubmissionTopics(submission.getId(), topics)

        then:
        submission.getTopics().size() == 3
        submission.getTopics().contains(topicOne)
        submission.getTopics().contains(topicTwo)
        submission.getTopics().contains(topicThree)
        topicOne.getSubmissions().size() == 1
        topicTwo.getSubmissions().size() == 1
        topicThree.getSubmissions().size() == 1
    }

    def "remove one topic of onHold submission"() {
        given: 'topic list'
        TopicDto[] topics = [topicDtoOne]


        when:
        submissionService.updateSubmissionTopics(submission.getId(), topics)

        then:
        submission.getTopics().size() == 1
        submission.getTopics().contains(topicOne)
        topicOne.getSubmissions().size() == 1
    }

    def "tries to update topics of non-pending submission"() {
        given: "a submissionDto"
        submissionDto.setStatus("APPROVED")
        submissionDto.setKey(KEY)
        and: "another submission"
        def submission2 = new Submission(submissionDto, user, course)
        submission2.setSubmissionStatus(Submission.Status.APPROVED)
        submissionRepository.save(submission2)

        and: "a topics list"
        TopicDto[] topics = [topicDtoOne, topicDtoTwo, topicDtoThree]

        when:
        submissionService.updateSubmissionTopics(submission2.getId(), topics)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.SUBMISSION_CANNOT_BE_EDITED
    }




    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService submissionService() {
            return new QuestionsByStudentService()
        }
    }
}
