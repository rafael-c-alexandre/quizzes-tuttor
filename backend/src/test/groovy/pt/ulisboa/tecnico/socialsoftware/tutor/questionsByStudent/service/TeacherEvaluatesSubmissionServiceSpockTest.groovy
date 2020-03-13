package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.TeacherEvaluatesSubmissionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_TEACHER_ERROR
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class TeacherEvaluatesSubmissionServiceSpockTest extends Specification{
    static final String COURSE_ONE ="CourseOne"
    static final String WRONG_COURSE = "WrongCourse"
    static final int QUESTION_ID =10
    static final int QUESTION_KEY =14
    static final int SUBMISSION_ID =14

    static final String ACRONYM =14
    static final String ACADEMIC_TERM =14
    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    TeacherEvaluatesSubmissionService teacherEvaluatesSubmissionService

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

    def "the user is not a teacher"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user)
        submission.setId(SUBMISSION_ID)
        submissionRepository.save(submission)

        when:

        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == NOT_TEACHER_ERROR
    }

    def "the submission does not exist in the repository"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user)
        submission.setId(SUBMISSION_ID)

        when:

        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == SUBMISSION_NOT_FOUND
    }

    def "the professor and submission exist and approves submission, question goes to repository"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> set = new HashSet<CourseExecution>()
        set.add(courseExecution)
        user.setCourseExecutions(set)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user)
        submissionRepository.save(submission)

        when:
        def result = teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())

        then: "the returned data are correct"
        result.getStatus().toString() == "APPROVED"
        result.getQuestion() ==  question
        and: "submission approved"
    }

    def "the professor and submission exist and rejects submission"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a wrong course"
        def wrongCourse = new Course(WRONG_COURSE, Course.Type.TECNICO)
        courseRepository.save(wrongCourse)
        and: "a course execution"
        def courseExecution = new CourseExecution(wrongCourse, COURSE_ONE, COURSE_ONE, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> set = new HashSet<CourseExecution>()
        set.add(courseExecution)
        user.setCourseExecutions(set)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_ID)
        question.setCourse(course)
        and: "a submission"
        def submission = new Submission(question, user)
        submissionRepository.save(submission)

        when:
        def result = teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())

        then: "the returned data are correct"
        result.getStatus().toString() == "REJECTED"
        result.getQuestion() ==  question
        and: "submission rejected"
    }

    def "the professor approves the same submission twice"()  {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        UserDto userDto = new UserDto(user)
        and: "a course"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        Set<CourseExecution> set = new HashSet<CourseExecution>()
        set.add(courseExecution)
        user.setCourseExecutions(set)
        and: "a question"
        def question = new Question()
        question.setKey(QUESTION_KEY)
        question.setCourse(course)
        questionRepository.save(question);
        and: "a submission"
        def submission = new Submission(question, user)
        submissionRepository.save(submission)

        when:
        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())
        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(userDto, submission.getId())

        then:
        thrown(TutorException)
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TeacherEvaluatesSubmissionService teacherEvaluatesSubmissionService() {
            return new TeacherEvaluatesSubmissionService()
        }
    }
}