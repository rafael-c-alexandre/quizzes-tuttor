package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class TeacherEvaluatesQuestionServicePerformanceTest extends Specification{
    static final String COURSE = "CourseOne"
    static final String NAME = "Francisco"
    static final String USERNAME = "Cecilio"
    static final Integer KEY = 10
    static final String ACRONYM =14
    static final String ACADEMIC_TERM =14

    @Autowired
    QuestionsByStudentService questionsByStudentService

    @Autowired
    CourseExecutionRepository courseExecutionRepository

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
        def user = new User(NAME, USERNAME,KEY, User.Role.TEACHER)
        userRepository.save(user)


        and: "a 500 submissions array"
        ArrayList<Submission> submissionList = new ArrayList<Submission>()
        1.upto(500, {
            def question = new Question()
            question.setCourse(course)
            question.setKey(KEY + it.intValue())
            questionRepository.save(question)
            def submission = new Submission(question, user)
            submissionRepository.save(submission)
            submissionList.add(submission)

        })

        when:

        1.upto(500, {
            questionsByStudentService.teacherEvaluatesQuestion(user.getId(), submissionList.get(it.intValue()-1).getId(),true)})

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
