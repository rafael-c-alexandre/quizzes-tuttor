package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*
import spock.lang.Specification

@DataJpaTest
class ListSubmissionServicePerformanceTest extends Specification {
    static final String COURSE = "CourseOne"
    static final Integer QUESTION_KEY = 12
    static final Integer STUDENT_ID = 676
    static final String NAME = "Rafael"
    static final String USERNAME = "Rafa"
    static final Integer KEY = 10

    @Autowired
    QuestionsByStudentService questionsByStudentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    SubmissionRepository submissionRepository

    @Autowired
    QuestionRepository questionRepository

    def "performance testing to get 2000 lists of student submissions"() {
        given: "a user"
        def user = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(user)
        and: "a course"
        def course = new Course(COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        
        and: "a 2000 question submissions"
        1.upto(2000, {
            and: "a question"
            def questionDto = new QuestionDto()
            def question = new Question(course, questionDto, Question.Status.PENDING)
            question.setKey(QUESTION_KEY+it.intValue())
            question.setCourse(course)
            questionRepository.save(question)
            submissionRepository.save(new Submission(question, user))
        })

        when:
        1.upto(2000, { questionsByStudentService.findQuestionsSubmittedByStudent(user.getId())})

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsByStudentService questionsByStudentService() {
            return new QuestionsByStudentService()
        }

    }
}
