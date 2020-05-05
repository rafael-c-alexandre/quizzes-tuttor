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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
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
class UpdateSubmissionSpockTest extends Specification{

        static final String COURSE_ONE ="CourseOne"
        static final int COURSE_ID = 14
        static final String QUESTION_TITLE = "QUESTION_ONE"
        static final String QUESTION_TITLE_2 = "QUESTION_TWO"
        static final String QUESTION_CONTENT = "CONTENT_ONE"
        static final String QUESTION_CONTENT2 = "CONTENT_TWO"
        public static final String OPTION_CONTENT = "optionId content"
        public static final String OPTION_CONTENT2 = "optionId content2"
        static final String NAME = "Rito"
        static final String USERNAME = "Silva"
        static final String USERNAME2 = "Alexandre"
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

       @Autowired
       OptionRepository optionRepository


        def user
        def teacher
        def course

        def optionOK
        def submission
        def submissionTeacher
        def submissionDtoTeacher2
        def submissionTeacher2


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

        }
    //tests for F1
    def "update options of onHold submission"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("ONHOLD")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)

        and: 'a option'
        def options = new ArrayList<OptionDto>()
        def optionDto2 = new OptionDto(optionOK)

        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        submissionDto2.setOptions(options)

        when:

        def result = submissionService.updateSubmission(submission.getId(), submissionDto2, user)

        then:
        result.getTitle() == QUESTION_TITLE_2
        result.getContent() == QUESTION_CONTENT2
        result.getStatus() == "ONHOLD"
        result.getOptions().size() == 1
        result.getOptions().get(0).getContent() == OPTION_CONTENT2
    }

    def "tries to update non-pending submission"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("ONHOLD")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)
        and: 'a optionId'
        def optionDto2 = new OptionDto()
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        def options2 = new ArrayList<OptionDto>()
        options2.add(optionDto2)
        submissionDto2.setOptions(options2)
        submissionDto2.setUser(user.getId())
        submissionDto2.setCourseId(course.getId())
        and: "a teacher"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)
        and: 'a submission acception'
        submissionService.teacherEvaluatesQuestion(teacher.getId(), submission.getId(), true,"like it")


        when:
        submissionService.updateSubmission(submission.getId(), submissionDto2, user)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.SUBMISSION_CANNOT_BE_EDITED
    }


    //tests for F4
    def "update options of APPROVED submission by a teacher"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("APPROVED")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)
        and: 'a option'
        def options = new ArrayList<OptionDto>()
        def optionDto2 = new OptionDto(optionOK)
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        submissionDto2.setOptions(options)
        and: "a teacher"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)
        and: 'a submission acception'
        submissionService.teacherEvaluatesQuestion(teacher.getId(), submission.getId(), true,"like it")


        when:
        def result = submissionService.updateSubmission(submission.getId(), submissionDto2, teacher)

        then:
        result.getTitle() == QUESTION_TITLE_2
        result.getContent() == QUESTION_CONTENT2
        result.getOptions().size() == 1
        result.getOptions().get(0).getContent() == OPTION_CONTENT2
    }

    def "try to update topics of ONHOLD submission by a teacher"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("ONHOLD")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)
        and: 'a option'
        def options = new ArrayList<OptionDto>()
        def optionDto2 = new OptionDto(optionOK)
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        submissionDto2.setOptions(options)
        and: "a user"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)

        when:
        submissionService.updateSubmission(submission.getId(), submissionDto2, teacher)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.SUBMISSION_CANNOT_BE_EDITED
    }

    //tests for F6
    def "student updates and resubmits rejected submission"() {
        given: "a teacher"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)
        and: 'a submission rejection'
        submissionService.teacherEvaluatesQuestion(teacher.getId(), submission.getId(), false,"don't like it")

        and: 'anthoer  submisionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("ONHOLD")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)
        and: 'a option'
        def options = new ArrayList<OptionDto>()
        def optionDto2 = new OptionDto(optionOK)
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        submissionDto2.setOptions(options)

        when:

        def result = submissionService.reSubmitSubmission(submission.getId(), submissionDto2, user)

        then:
        result.getTitle() == QUESTION_TITLE_2
        result.getContent() == QUESTION_CONTENT2
        result.getStatus() == "ONHOLD"
        result.getOptions().size() == 1
        result.getOptions().get(0).getContent() == OPTION_CONTENT2
    }

    def "student tries to resubmit approved submission"() {
        given: "a teacher"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)
        and: 'a submission acception'
        submissionService.teacherEvaluatesQuestion(teacher.getId(), submission.getId(), true,"like it")

        and: 'anthoer  submisionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("ONHOLD")
        submissionDto2.setCourseId(COURSE_ID)
        submissionDto2.setJustification("")
        submissionDto2.setTitle(QUESTION_TITLE_2)
        submissionDto2.setContent(QUESTION_CONTENT2)
        and: 'a option'
        def options = new ArrayList<OptionDto>()
        def optionDto2 = new OptionDto(optionOK)
        optionDto2.setContent(OPTION_CONTENT2)
        optionDto2.setCorrect(true)
        options.add(optionDto2)
        submissionDto2.setOptions(options)

        when:

        submissionService.reSubmitSubmission(submission.getId(), submissionDto2, user)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.SUBMISSION_CANNOT_BE_RESUBMITED
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService submissionService() {
            return new QuestionsByStudentService()
        }
    }
}
