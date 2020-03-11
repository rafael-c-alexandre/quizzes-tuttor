package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.service

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Specification

class StudentSubmitQuestionServiceSpockTest extends Specification{
    static final String TOPIC_ONE = "TopicOne"
    static final String COURSE_ONE ="CourseOne"
    static final String ACRONYM_ONE = "CO"
    static final String ACADEMIC_TERM = "1st term"
    static final String QUESTION_ONE = "What is the phase after Testing?"
    static final String OPTION1 = "Development"
    static final String OPTION2 = "Verification"
    static final String OPTION3 = "Validation"
    static final String OPTION4 = "Usage"



    def questionByStudentService

    def setup() {
        questionByStudentService = new QuestionsByStudentService()

    }

    def "the topic and course exist and create question"()  {
        given: "a topic"
        def course = new Course(COURSE_ONE, Course.Type.TECNICO)
        and: "a topicDto"
        def tDto = new TopicDto()
        and: "a topic"
        def topic = new Topic(course, tDto)
        and: "a topicDto"
        def topicDto = new TopicDto(topic)
        topicDto.setName(TOPIC_ONE)
        and: "a questionDto"
        def question = new Question()
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)

        when:
        def result = questionByStudentService.studentSubmitQuestion(questionDto)

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

        questionByStudentService.studentSubmitQuestion(questionDto)

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
        questionByStudentService.studentSubmitQuestion(questionDto)

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
        questionByStudentService.studentSubmitQuestion(questionDto)

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
        question.setTitle(QUESTION_ONE)
        def questionDto = new QuestionDto(question)

        when:
        questionByStudentService.studentSubmitQuestion(questionDto)

        then:
        thrown(TutorException)
    }



}