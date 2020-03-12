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
    static final int WRONG_QUESTION_ID = 20

    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 1
    static final String ROLE = "TEACHER"

    static final String OPTION1 = "Development"
    static final String OPTION2 = "Verification"
    static final String OPTION3 = "Validation"
    static final String OPTION4 = "Usage"

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

    //fUNC 2
    def "the user is not a teacher"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user.getId())
        submission.setId(COURSE_ID)
        submissionRepository.save(submission)

        when:

        questionByStudentService.teacherEvaluatesQuestion(user, submission.getId())

        then:
        thrown(TutorException)
    }

    def "the submission does not exist in the repository"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user.getId())
        submission.setId(COURSE_ID)

        when:

        questionByStudentService.teacherEvaluatesQuestion(user, submission.getId())

        then:
        thrown(TutorException)
    }

    def "the professor and submission exist and approves submission"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ONE, COURSE_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> set = new HashSet<CourseExecution>()
        set.add(courseExecution)
        user.setCourseExecutions(set)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user.getId())
        submissionRepository.save(submission)

        when:
        def result = questionByStudentService.teacherEvaluatesQuestion(user, submission.getId())

        then: "the returned data are correct"
        result.getStatus().toString() == "APPROVED"
        and: "submission approved"
    }

    def "the professor and submission exist and rejects submission"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        and: "a course"
        def course = new Course(WRONG_COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, COURSE_ONE, COURSE_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> set = new HashSet<CourseExecution>()
        set.add(courseExecution)
        user.setCourseExecutions(set)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user.getId())
        submissionRepository.save(submission)

        when:
        def result = questionByStudentService.teacherEvaluatesQuestion(user, submission.getId())

        then: "the returned data are correct"
        result.getStatus().toString() == "APPROVED"
        and: "submission approved"
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService questionsByStudentService() {
            return new QuestionsByStudentService()
        }

    }



}