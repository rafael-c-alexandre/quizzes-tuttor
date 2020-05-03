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

        def user
        def teacher
        def course
        def submissionDto
        def submissionDtoTeacher
        def optionDto
        def options
        def submission
        def submissionTeacher


        def setup() {
            given: "a user"
            user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
            userRepository.save(user)
            and: "a course"
            course = new Course(COURSE_ONE, Course.Type.TECNICO)
            courseRepository.save(course)
            and: " a optionDto"
            optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setCorrect(true)
            options = new ArrayList<OptionDto>()
            options.add(optionDto)
            and: " an ONHOLD submissionDto"
            submissionDto = new SubmissionDto()
            submissionDto.setId(1)
            submissionDto.setKey(1)
            submissionDto.setStatus("ONHOLD")
            submissionDto.setCourseId(COURSE_ID)
            submissionDto.setJustification("")
            submissionDto.setTitle(QUESTION_TITLE)
            submissionDto.setContent(QUESTION_CONTENT)
            submissionDto.setOptions(options)
            submissionDto.setUser(user.getId())
            submissionDto.setCourseId(course.getId())
            submission = new Submission(submissionDto,user, course)
            submissionRepository.save(submission)
            and: " an APPROVED submissionDto"
            submissionDtoTeacher = new SubmissionDto()
            submissionDtoTeacher.setId(2)
            submissionDtoTeacher.setKey(2)
            submissionDtoTeacher.setStatus("APPROVED")
            submissionDtoTeacher.setCourseId(COURSE_ID)
            submissionDtoTeacher.setJustification("")
            submissionDtoTeacher.setTitle(QUESTION_TITLE)
            submissionDtoTeacher.setContent(QUESTION_CONTENT)
            submissionDtoTeacher.setOptions(options)
            submissionDtoTeacher.setUser(user.getId())
            submissionDtoTeacher.setCourseId(course.getId())
            submissionTeacher = new Submission(submissionDtoTeacher,user, course)
            submissionRepository.save(submissionTeacher)
        }

    def "update topics of APPROVED submission by a teacher"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("APPROVED")
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
        and: "a user"
        teacher = new User(NAME, USERNAME2, KEY + 1, User.Role.TEACHER)
        userRepository.save(teacher)
        submissionDto2.setOptions(options2)
        submissionDto2.setUser(teacher.getId())
        submissionDto2.setCourseId(course.getId())

        when:
        def result = submissionService.updateSubmission(submissionDtoTeacher.getId(), submissionDto2, teacher)

        then:
        result.getTitle() == QUESTION_TITLE_2
        result.getContent() == QUESTION_CONTENT2
        result.getOptions().size() == 1
        result.getOptions().get(0).getContent() == OPTION_CONTENT2
    }


    def "update topics of onHold submission by student"() {
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

        when:
        def result = submissionService.updateSubmission(submissionDto.getId(), submissionDto2, user.getId())

        then:
        result.getTitle() == QUESTION_TITLE_2
        result.getContent() == QUESTION_CONTENT2
        result.getOptions().size() == 1
        result.getOptions().get(0).getContent() == OPTION_CONTENT2
    }

    def "tries to update non-pending submission"() {
        given: 'another submissionDto'
        def submissionDto2 = new SubmissionDto()
        submissionDto2.setId(1)
        submissionDto2.setKey(1)
        submissionDto2.setStatus("APPROVED")
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

        when:
        submissionService.updateSubmission(submissionDto.getId(), submissionDto2, user.getId())

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.SUBMISSION_CANNOT_BE_EDITED
    }




    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService submissionService() {
            return new QuestionsByStudentService()
        }
    }
}
