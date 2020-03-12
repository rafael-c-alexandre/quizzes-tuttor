package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.ListSubmissionsService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ListSubmissionServiceSpockTest extends Specification{
    static final String WRONG_COURSE = "WrongCourse"
    static final int QUESTION_ID =10

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
    //F3
    def "list an empty list of submissions"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)

        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user.getId())

        then: "the returned list is empty"
        result.isEmpty()
        and: "list empty"
    }

    def "list a non-empty list of submissions"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)

        and: "a course"
        def course = new Course(WRONG_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user.getId())
        submissionRepository.save(submission)

        when:
        def result = listSubmissionsService.findQuestionsSubmittedByStudent(user.getId())

        then: "the returned list has 1 submission"
        result.size() == 1
        and: "list empty"
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ListSubmissionsService listSubmissionsService() {
            return new ListSubmissionsService()
        }

    }



}