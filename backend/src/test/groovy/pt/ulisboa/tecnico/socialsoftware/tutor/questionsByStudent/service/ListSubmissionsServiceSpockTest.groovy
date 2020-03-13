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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.ListSubmissionsService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ListSubmissionsServiceSpockTest extends Specification {
    static final String WRONG_COURSE = "WrongCourse"
    static final int QUESTION_KEY =10

    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    ListSubmissionsService listSubmissionsService

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

    def setup() {

    }

    def "list an empty list of submissions"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)

        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user)

        then: "the returned list is empty"
        result.isEmpty()
        and: "list empty"
    }



    def "list a non-empty list of submissions of size 1"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)

        and: "a course"
        def course = new Course(WRONG_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user)
        submissionRepository.save(submission)


        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user)

        then: "the returned list has 1 submission"
        result.size() == 1
        result.get(0).question == question
        result.get(0).user == user

    }

    def "user is not a student"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.ADMIN)
        userRepository.save(user)

        and: "a course"
        def course = new Course(WRONG_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user)
        submissionRepository.save(submission)


        when:
        listSubmissionsService.findQuestionsSubmittedByStudent(user)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.NOT_STUDENT_ERROR


    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ListSubmissionsService listSubmissionsService() {
            return new ListSubmissionsService()
        }

    }
}
