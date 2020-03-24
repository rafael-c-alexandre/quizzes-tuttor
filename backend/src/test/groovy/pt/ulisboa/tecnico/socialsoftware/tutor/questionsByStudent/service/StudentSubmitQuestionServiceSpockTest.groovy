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
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Unroll
import spock.lang.Specification


@DataJpaTest
class StudentSubmitQuestionServiceSpockTest extends Specification{
    static final String TOPIC_ONE = "TopicOne"
    static final String COURSE_ONE ="CourseOne"
    static final String WRONG_COURSE = "WrongCourse"
    static final String ACRONYM_ONE = "CO"
    static final String ACADEMIC_TERM = "1st term"
    static final String QUESTION_ONE = "What is the phase after Testing?"
    static final int COURSE_ID = 14
    static final int QUESTION_ID =10

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

    def setup() {

    }

    def "the user is not a student"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a questionDto"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        questionRepository.save(question)
        QuestionDto questionDto = new QuestionDto(question)

        when:
        questionByStudentService.studentSubmitQuestion(questionDto,userDto)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.NOT_STUDENT_ERROR
    }

    def "question exists and it is correctly submitted"(){
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a questionDto"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        questionRepository.save(question)
        QuestionDto questionDto = new QuestionDto(question)

        when:

        def result = questionByStudentService.studentSubmitQuestion(questionDto,userDto)

        then:
        result.status == "ONHOLD"
        result.justification == ""
        result.getUser().getId() == user.getId()
        result.getQuestion() == question

    }

    def "student submit an empty question"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a questionDto"
        def question = new Question()
        question.setId(QUESTION_ID)
        QuestionDto questionDto = new QuestionDto(question)


        when:
        questionByStudentService.studentSubmitQuestion(questionDto, userDto)

        then: "throw exception"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_NOT_FOUND

    }
    

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService questionsByStudentService() {
            return new QuestionsByStudentService()
        }
    }

}