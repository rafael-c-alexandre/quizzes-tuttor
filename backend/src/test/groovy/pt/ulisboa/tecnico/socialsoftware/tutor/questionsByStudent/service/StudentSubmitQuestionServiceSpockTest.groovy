package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.TutorApplication
import pt.ulisboa.tecnico.socialsoftware.tutor.administration.AdministrationService
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

    def "the topic and course exist and create question"()  {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a topicDto"
        def tDto = new TopicDto()
        and: "a topic"
        def topic = new Topic(course, tDto)
        topicRepository .save(topic)

        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        topicDto.setName(TOPIC_ONE)
        and: "a questionDto"
        def question = new Question()
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)

        when:
        def result = questionByStudentService.studentSubmitQuestion(COURSE_ID, questionDto)

        then: "the returned data are correct"
        result.name == TOPIC_ONE
        and: "question is created"
    }

    def "the topic and course exist and question exists"()  {
        given: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a topicDto"
        def tDto = new TopicDto()
        and: "a topic"
        def topic = new Topic(course, tDto)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        topicDto.setName(TOPIC_ONE)
        def question = new Question()
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)

        when:

        questionByStudentService.studentSubmitQuestion(COURSE_ID,questionDto)

        then:
        thrown(TutorException)
    }

    def "course name is empty"() {
        given: "a course Dto"

        def courseDto = new CourseDto()
        courseDto.setName(null)
        courseDto.setAcronym(ACRONYM_ONE)
        courseDto.setAcademicTerm(ACADEMIC_TERM)
        and: "a topicDto"
        def tDto = new TopicDto()
        and: "a topic"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        def topic = new Topic(course, tDto)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        topicDto.setName(TOPIC_ONE)
        def question = new Question()
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)

        when:
        questionByStudentService.studentSubmitQuestion(COURSE_ID, questionDto)

        then:
        thrown(TutorException)

    }

    def "topic name is empty"() {
        given: "a topic Dto"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        def tDto = new TopicDto()
        def topic = new Topic(course, tDto)
        def topicDto = new TopicDto(topic)
        topicDto.setName(null)
        def question = new Question()
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)



        when:
        questionByStudentService.studentSubmitQuestion(COURSE_ID, questionDto)

        then:
        thrown(TutorException)

    }

    def "the question name is empty"()  {
        given: "a topic Dto"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        def tDto = new TopicDto()
        def topic = new Topic(course, tDto)
        def topicDto = new TopicDto(topic)
        topicDto.setName(TOPIC_ONE)
        def question = new Question()
        question.setTitle(null)
        def questionDto = new QuestionDto(question)

        when:
        questionByStudentService.studentSubmitQuestion(COURSE_ID, questionDto)

        then:
        thrown(TutorException)
    }
}