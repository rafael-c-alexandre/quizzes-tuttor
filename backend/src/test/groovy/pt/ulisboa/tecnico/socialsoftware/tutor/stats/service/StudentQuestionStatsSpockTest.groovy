package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class StudentQuestionStatsSpockTest extends Specification{

    static final String COURSE_ONE ="CourseOne"
    static final String QUESTION_TITLE = "QUESTION_ONE"
    static final String QUESTION_TITLE_2 = "QUESTION_TWO"
    static final String QUESTION_TITLE_3 = "QUESTION_THREE"
    static final String QUESTION_CONTENT = "CONTENT_ONE"
    static final String QUESTION_CONTENT_2 = "CONTENT_TWO"
    static final String QUESTION_CONTENT_3 = "CONTENT_THREE"
    public static final String OPTION_CONTENT = "optionId content"
    public static final String OPTION_CONTENT_2 = "optionId content2"
    public static final String OPTION_CONTENT_3 = "optionId content3"
    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final String USERNAME2 = "Alexandre"
    static final int KEY = 10

    @Autowired
    StatsService statsService

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

    @Autowired
    OptionRepository optionRepository


    def user
    def teacher
    def course

    def optionOK
    def optionOK2
    def optionOK3
    def submission
    def submission2
    def submission3



    def setup() {
        given: "create a question"
        submission = new Submission()
        submission.setKey(1)
        submission.setTitle(QUESTION_TITLE)
        submission.setContent(QUESTION_CONTENT)
        and: "a user"
        user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        and: "a course"
        course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)

        and: " a submissionDto"
        submission.setCourse(course)
        submission.setUser(user)
        optionOK = new Option()
        optionOK.setContent(OPTION_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setSubmission(submission)
        optionRepository.save(optionOK)
        submissionRepository.save(submission)

        and: "create a second question"
        submission2 = new Submission()
        submission2.setKey(2)
        submission2.setTitle(QUESTION_TITLE_2)
        submission2.setContent(QUESTION_CONTENT_2)
        submission2.setSubmissionStatus(Submission.Status.REJECTED)

        and: " a option"
        submission2.setCourse(course)
        submission2.setUser(user)
        optionOK2 = new Option()
        optionOK2.setContent(OPTION_CONTENT_2)
        optionOK2.setCorrect(true)
        optionOK2.setSequence(0)
        optionOK2.setSubmission(submission2)
        optionRepository.save(optionOK2)
        submissionRepository.save(submission2)

        and: "create a second question"
        submission3 = new Submission()
        submission3.setKey(3)
        submission3.setTitle(QUESTION_TITLE_3)
        submission3.setContent(QUESTION_CONTENT_3)
        submission3.setSubmissionStatus(Submission.Status.APPROVED)

        and : "an option"
        submission3.setCourse(course)
        submission3.setUser(user)
        optionOK3 = new Option()
        optionOK3.setContent(OPTION_CONTENT_3)
        optionOK3.setCorrect(true)
        optionOK3.setSequence(0)
        optionOK3.setSubmission(submission2)
        optionRepository.save(optionOK3)
        submissionRepository.save(submission3)

    }

    def "check student question stats"() {

        when:

        def result = statsService.getStudentQuestionsStats(user.getId())

        then:
        result.getTotalQuestionsSubmitted() == 3
        result.getTotalQuestionsOnHold() == 1
        result.getTotalQuestionsApproved() == 1
        result.getTotalQuestionsAvailable() == 0

    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
