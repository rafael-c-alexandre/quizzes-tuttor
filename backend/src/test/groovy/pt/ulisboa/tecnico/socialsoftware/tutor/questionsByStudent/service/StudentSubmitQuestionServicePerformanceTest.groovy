package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
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
    static final String COURSE = "CourseOne"
    static final String NAME = "Rafael"
    static final String USERNAME = "Rafa"
    static final Integer KEY = 10

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
        def course = new Course(COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a user"
        def user = new User(NAME, USERNAME,KEY, User.Role.STUDENT)
        userRepository.save(user)


        and: "a 500  question submissionDto"
        ArrayList<SubmissionDto> submissionDtoList = new ArrayList<SubmissionDto>()
        1.upto(500, {
            def questionDto = new QuestionDto()
            def question = new Question(course, questionDto, Question.Status.PENDING)
            question.setCourse(course)
            question.setKey(KEY + it.intValue())
            questionRepository.save(question)
            def submissionDto = new SubmissionDto()
            submissionDto.setStatus("ONHOLD")
            submissionDto.setCourseId(course.getId())
            submissionDto.setJustification("")
            submissionDto.setQuestionId(question.getId())
            submissionDto.setUser(user.getId())
            submissionDtoList.add(submissionDto)

        })

        when:

        1.upto(500, {
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
