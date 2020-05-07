package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository


import spock.lang.Specification

import java.time.format.DateTimeFormatter

@DataJpaTest
class DashboardStatisticsTest extends Specification{
    public static final String TOURNAMENT_TITLE = "Tournament"
    public static final String CREATION_DATE = "2020-09-22 12:12"
    public static final String AVAILABLE_DATE = "2020-09-23 12:12"
    public static final String CONCLUSION_DATE = "2020-09-24 12:12"
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer TNMT_ID = 2
    public static final Integer USER_ID = 2
    public static final Integer NUM_Q = 3


    @Autowired
    TournamentService tournamentService

    @Autowired
    StatsService statsService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizRepository quizRepository



    def user1, user2
    def tournament
    def courseExecution, courseExecutionId
    def topicList
    def question1,question2,question3,question4,question5

    def setup(){
        //User 2 creates a Tournament with 5 questions
        user1 = new User()
        user2 = new User()
        user1.setRole(User.Role.STUDENT)
        user2.setRole(User.Role.STUDENT)
        user1.setKey(1)
        user2.setKey(2)

        userRepository.save(user1)
        userRepository.save(user2)

        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        courseExecutionId = courseExecution.getId()
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        def topic = new Topic()
        topic.setName("TOPICO")
        topicRepository.save(topic)

        topicList = new ArrayList<TopicDto>()
        topicList.add(new TopicDto((topic)))

        def tournamentDto = getTournamentDto(TOURNAMENT_TITLE,
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE, USER_ID, topicList, NUM_Q)

        tournament = new Tournament(tournamentDto)
        tournament.setTournamentCreator(user2)
        tournamentRepository.save(tournament)




    }

    def "test calculus of signed tournaments total"(){
        when: "an enrollment by user 1 in created tournament"

        tournament.addUser(user1)

        then: "1 signed tournaments must be equal to the number retrieved"
        def tournamentStatsDto = statsService.getTournamentStats(user1.getId(), courseExecutionId)
        tournamentStatsDto.getTotalSignedTournaments() == 1
    }

    def "test calculus of created tournaments total"(){
        when: "an enrollment by user 1 in created tournament"
        def tournamentDto1 = getTournamentDto("Tournament1",
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE, 1, topicList, NUM_Q)
        def tournamentDto2 = getTournamentDto("Tournament2",
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE, 1, topicList, NUM_Q)

        def tournament1 = new Tournament(tournamentDto1)
        def tournament2 = new Tournament(tournamentDto2)
        tournament1.setTournamentCreator(user1)
        tournament2.setTournamentCreator(user1)

        then: "2 created tournaments by user1 and 1 by user2"
        def tournamentStatsDto1 = statsService.getTournamentStats(user1.getId(), courseExecutionId)
        def tournamentStatsDto2 = statsService.getTournamentStats(user2.getId(), courseExecutionId)
        tournamentStatsDto1.getTotalCreatedTournaments() == 2
        tournamentStatsDto2.getTotalCreatedTournaments() == 1
    }

    def "test calculus of correct and total answers"(){
        when: "2 out of 5 answers are correct"

        tournament.addUser(user1)
        attendTournament(user1)

        then: "2 correct answers and 5 total answers"
        def tournamentStatsDto = statsService.getTournamentStats(user1.getId(), courseExecutionId)
        tournamentStatsDto.getTotalCorrectAnswers() == 2
        tournamentStatsDto.getAnswersInTournaments() == 5
    }

    def "test calculus of attended tournaments"() {
        when:"enroll 3 tournaments but only answer to 1"
        def tournamentDto1 = getTournamentDto("Tournament1",
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE, 1, topicList, NUM_Q)
        def tournamentDto2 = getTournamentDto("Tournament2",
                AVAILABLE_DATE, CONCLUSION_DATE, CREATION_DATE, 1, topicList, NUM_Q)

        def tournament1 = new Tournament(tournamentDto1)
        def tournament2 = new Tournament(tournamentDto2)
        tournament1.setTournamentCreator(user2)
        tournament2.setTournamentCreator(user2)

        //enrollment
        tournament1.addUser(user1)
        tournament2.addUser(user1)
        tournament.addUser(user1)

        //answer to tournament
        attendTournament(user1)

        then:"1 attended tournaments and 3 enrolled"
        def tournamentStatsDto = statsService.getTournamentStats(user1.getId(), courseExecutionId)
        tournamentStatsDto.getAttendedTournaments() == 1
        tournamentStatsDto.getTotalSignedTournaments() == 3
    }




    private static getTournamentDto(String title, String availableDate, String conclusionDate, String creationDate,
                                    Integer creator, List<TopicDto> topicList, int numb_questions) {

        TournamentDto tournamentDto = new TournamentDto()
        tournamentDto.setTitle(title)
        tournamentDto.setAvailableDate(availableDate)
        tournamentDto.setConclusionDate(conclusionDate)
        tournamentDto.setCreationDate(creationDate)
        tournamentDto.setTournamentCreator(creator)
        tournamentDto.setTopics(topicList)
        tournamentDto.setNumberOfQuestions(numb_questions)


        return tournamentDto
    }

    private void attendTournament(User user){
        Quiz tQuiz = new Quiz()
        quizRepository.save(tQuiz)
        tQuiz.setCourseExecution(courseExecution)
        tQuiz.setType(Quiz.QuizType.GENERATED.toString())

        // correct answers
        def option1 = new Option()
        option1.setCorrect(true)
        option1.setContent("option 1")
        option1.setSequence(1)
        optionRepository.save(option1)
        //quiz question
        QuizQuestion tQuizQuestion1 = new QuizQuestion()
        tQuizQuestion1.setQuiz(tQuiz)
        //question answer
        QuestionAnswer tQuestionAnswer1 = new QuestionAnswer()
        tQuestionAnswer1.setOption(option1)
        tQuestionAnswer1.setQuizQuestion(tQuizQuestion1)
        quizQuestionRepository.save(tQuizQuestion1)
        questionAnswerRepository.save(tQuestionAnswer1)

        def option2 = new Option()
        option2.setContent("option 2")
        option2.setCorrect(true)
        option2.setSequence(1)
        optionRepository.save(option2)
        //quiz question
        QuizQuestion tQuizQuestion2 = new QuizQuestion()
        tQuizQuestion2.setQuiz(tQuiz)
        //question answer
        QuestionAnswer tQuestionAnswer2 = new QuestionAnswer()
        tQuestionAnswer2.setQuizQuestion(tQuizQuestion2)
        tQuestionAnswer2.setOption(option2)

        quizQuestionRepository.save(tQuizQuestion2)
        questionAnswerRepository.save(tQuestionAnswer2)

        //wrong answers
        def option3 = new Option()
        option3.setContent("option 3")
        option3.setCorrect(false)
        option3.setSequence(1)
        optionRepository.save(option3)

        QuizQuestion tQuizQuestion3 = new QuizQuestion()
        tQuizQuestion3.setQuiz(tQuiz)
        QuestionAnswer tQuestionAnswer3 = new QuestionAnswer()
        tQuestionAnswer3.setQuizQuestion(tQuizQuestion3)
        tQuestionAnswer3.setOption(option3)

        quizQuestionRepository.save(tQuizQuestion3)
        questionAnswerRepository.save(tQuestionAnswer3)

        def option4 = new Option()
        option4.setContent("option 4")
        option4.setCorrect(false)
        option4.setSequence(1)
        optionRepository.save(option4)

        QuizQuestion tQuizQuestion4 = new QuizQuestion()
        tQuizQuestion4.setQuiz(tQuiz)
        QuestionAnswer tQuestionAnswer4 = new QuestionAnswer()
        tQuestionAnswer4.setQuizQuestion(tQuizQuestion4)
        tQuestionAnswer4.setOption(option4)

        quizQuestionRepository.save(tQuizQuestion4)
        questionAnswerRepository.save(tQuestionAnswer4)

        //repeated question
        QuestionAnswer tQuestionAnswer5 = new QuestionAnswer()
        tQuestionAnswer5.setQuizQuestion(tQuizQuestion4)
        tQuestionAnswer5.setOption(option4)
        questionAnswerRepository.save(tQuestionAnswer5)

        List<QuestionAnswer> tQuestionAnswers = new ArrayList<>(5)
        tQuestionAnswers.add(tQuestionAnswer1)
        tQuestionAnswers.add(tQuestionAnswer2)
        tQuestionAnswers.add(tQuestionAnswer3)
        tQuestionAnswers.add(tQuestionAnswer4)
        tQuestionAnswers.add(tQuestionAnswer5)

        QuizAnswer tQuizAnswer = new QuizAnswer()
        tQuizAnswer.setQuestionAnswers(tQuestionAnswers)
        tQuizAnswer.setUser(user)
        tQuizAnswer.setCompleted(true)
        quizAnswerRepository.save(tQuizAnswer)

        tQuiz.addQuizAnswer(tQuizAnswer)
        tQuizAnswer.setQuiz(tQuiz)
        tQuiz.setAssociatedTournament(tournament)
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

        @Bean
        StatsService statsService() {
            return new StatsService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

        @Bean
        AnswersXmlImport xmlImporter() {
            return new AnswersXmlImport()
        }
    }

}
