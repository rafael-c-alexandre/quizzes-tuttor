package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.lang.reflect.Array

@DataJpaTest
class StudentSubmitQuestionServicePerformanceTest extends Specification{
    static final String COURSE_ONE ="CourseOne"
    static final int COURSE_ID = 14
    static final String QUESTION_TITLE = "QUESTION_ONE";
    static final String QUESTION_CONTENT = "CONTENT_ONE";
    public static final String OPTION_CONTENT = "optionId content"

    static final String NAME = "Rito"
    static final String USERNAME = "Silva"
    static final int KEY = 10

    @Autowired
    QuestionsByStudentService questionsByStudentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    SubmissionRepository submissionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    def "performance testing to create 500 question submissions"() {
        given: "a course"
        def course = new Course(COURSE_ONE,Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a user"
        def user = new User(NAME, USERNAME,KEY, User.Role.STUDENT)
        userRepository.save(user)


        and: "a 2000  question submissionDto"
        ArrayList<SubmissionDto> submissionDtoList = new ArrayList<SubmissionDto>()
        1.upto(100, {
            and: "a submission"
            def submissionDto = new SubmissionDto()
            submissionDto.setKey(KEY+it.intValue())
            submissionDto.setStatus("ONHOLD")
            submissionDto.setCourseId(COURSE_ID)
            submissionDto.setJustification("")
            submissionDto.setTitle(QUESTION_TITLE)
            submissionDto.setContent(QUESTION_CONTENT)
            and: 'a optionId'
            def optionDto = new OptionDto()
            optionDto.setContent(OPTION_CONTENT)
            optionDto.setCorrect(true)
            def options = new ArrayList<OptionDto>()
            options.add(optionDto)
            submissionDto.setOptions(options)
            submissionDto.setUser(user.getId())
            submissionDto.setCourseId(course.getId())
            submissionDtoList.add(submissionDto)

        })

        when:

        1.upto(100, {
            questionsByStudentService.studentSubmitQuestion(submissionDtoList.get(it.intValue()-1),user.getId())})

        then:
        true
    }




    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
         QuestionsByStudentService questionsByStudentService(){
            return new QuestionsByStudentService()
        }

    }
}
