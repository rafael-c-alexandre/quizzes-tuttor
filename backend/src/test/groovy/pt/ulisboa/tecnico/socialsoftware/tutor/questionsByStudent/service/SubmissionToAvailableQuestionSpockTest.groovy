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

import java.util.ArrayList;


@DataJpaTest
class SubmissionToAvailableQuestionSpockTest extends Specification {

    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE"
    static final String QUESTION_CONTENT = "CONTENT_ONE"
    public static final String OPTION_CONTENT = "optionId content"
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
    def submission


    def setup() {
        user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        submissionDto = new SubmissionDto()
        submissionDto.setKey(1)
        submissionDto.setStatus(Submission.Status.ONHOLD.toString())
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
        submissionRepository.save(submission)

    }


    def "make approved submission available to be included in quizzes"() {
        given: "an approved submission"
        submission.setSubmissionStatus(Submission.Status.APPROVED)
        submissionDto.setStatus(Submission.Status.APPROVED.toString())

        when:
        submissionService.makeQuestionAvailable(course.getId(),submission.getId())

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getStatus() == Question.Status.AVAILABLE
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 1
        result.getCourse().getName() == COURSE_ONE
        course.getQuestions().contains(result)
        def resOption = result.getOptions().get(0)
        resOption.getContent() == OPTION_CONTENT

    }

    def "tries to turn on hold submission into available question"() {
        given: "an approved submission"
        submission.setSubmissionStatus(Submission.Status.ONHOLD)
        submissionDto.setStatus(Submission.Status.ONHOLD.toString())

        when:
        submissionService.makeQuestionAvailable(course.getId(),submission.getId())

        then: "throw exception"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_CANNOT_BE_AVAILABLE
    }




    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService submissionService() {
            return new QuestionsByStudentService()
        }
    }
}
