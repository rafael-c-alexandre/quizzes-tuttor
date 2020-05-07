package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NOT_TEACHER_ERROR
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_ALREADY_EVALUATED
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_NOT_FOUND

@DataJpaTest
class TeacherEvaluatesSubmissionServiceSpockTest extends Specification{
    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE";
    static final String QUESTION_CONTENT = "CONTENT_ONE";
    public static final String OPTION_CONTENT = "optionId content"
    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final String NAME2 = "Ritos"
    static final String USERNAME2 = "Silvas"
    static final int KEY = 10

    @Autowired
    QuestionsByStudentService teacherEvaluatesSubmissionService

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

    def course
    def submissionDto
    def optionDto
    def options

    def setup() {
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
        submissionDto.setCourseId(course.getId())


    }

    def "the user is not a teacher"() {
        given: "a student user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        submissionDto.setUser(user.getId())

        when:
        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion( user.getId(), submissionDto, true, "like it")

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == NOT_TEACHER_ERROR
    }

    def "the submission does not exist in the repository"() {
        given: "a teacher user"
        def user = new User(NAME, USERNAME, KEY, User.Role.TEACHER)
        userRepository.save(user)
        submissionDto.setUser(user.getId())

        when:

        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(user.getId(), submissionDto, true,"like it")

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == SUBMISSION_NOT_FOUND
    }

    def "the professor and submission exist and approves submission, question goes to repository"()  {
        given: "users"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        def user2 = new User(NAME2, USERNAME2, KEY+1, User.Role.TEACHER)
        userRepository.save(user2)
        submissionDto.setUser(user.getId())

        and : "a submission"
        def submission = new Submission(submissionDto,user,course)
        submissionRepository.save(submission)


        when:
        def result = teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(user2.getId(), new SubmissionDto(submission), true, "like it")

        then: "the returned data are correct"
        result.getStatus().toString() == "APPROVED"
        and: "submission approved"
    }

    def "the professor and submission exist and rejects submission"()  {
        given: "users"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        def user2 = new User(NAME2, USERNAME2, KEY+1, User.Role.TEACHER)
        userRepository.save(user2)
        submissionDto.setUser(user.getId())

        and : "a submission"
        def submission = new Submission(submissionDto,user,course)
        submissionRepository.save(submission)

        when:
        def result = teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(user2.getId(), new SubmissionDto(submission), false, "not like it")

        then: "the returned data are correct"
        result.getStatus().toString() == "REJECTED"
        and: "submission rejected"
    }

    def "the professor approves the same submission twice"()  {
        given: "users"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        def user2 = new User(NAME2, USERNAME2, KEY+1, User.Role.TEACHER)
        userRepository.save(user2)
        submissionDto.setUser(user.getId())
        and : "a submission"
        def submission = new Submission(submissionDto,user,course)
        submissionRepository.save(submission)

        when:
        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(user2.getId(), new SubmissionDto(submission), true, "like it")
        teacherEvaluatesSubmissionService.teacherEvaluatesQuestion(user2.getId(), new SubmissionDto(submission), true, "Like it")

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == SUBMISSION_ALREADY_EVALUATED
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService teacherEvaluatesSubmissionService() {
            return new QuestionsByStudentService()
        }
    }
}