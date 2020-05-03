package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class TournamentGeneratesQuizTest extends Specification {

    static final User.Role ROLE = User.Role.STUDENT
    static final String TOURNAMENT_TITLE = "Tournament"
    static final String CREATION_DATE = "2020-09-22 12:12"
    static final String AVAILABLE_DATE = "2020-09-23 12:12"
    static final String CONCLUSION_DATE = "2020-09-24 12:12"


    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    UserRepository userRepository

    Tournament tournament
    User u1
    User u2
    User u3

    def setup() {
        Question q1 = new Question()
        q1.setTitle("1")
        Question q2 = new Question()
        q2.setTitle("2")
        Question q3 = new Question()
        q3.setTitle("3")
        Question q4 = new Question()
        q4.setTitle("4")

        questionRepository.save(q1)
        questionRepository.save(q2)
        questionRepository.save(q3)
        questionRepository.save(q4)


        u1 = new User()
        u2 = new User()
        u3 = new User()

        u1.setRole(ROLE)
        u2.setRole(ROLE)
        u3.setRole(ROLE)

        u1.setKey(0)
        u2.setKey(1)
        u3.setKey(3)

        userRepository.save(u1)
        userRepository.save(u2)
        userRepository.save(u3)


        TournamentDto dto = new TournamentDto()
        dto.setTitle(TOURNAMENT_TITLE)
        dto.setAvailableDate(AVAILABLE_DATE)
        dto.setConclusionDate(CONCLUSION_DATE)
        dto.setCreationDate(CREATION_DATE)

        tournament = new Tournament(dto)

        tournament.addQuestion(q1)
        tournament.addQuestion(q2)
        tournament.addQuestion(q3)
        tournament.addQuestion(q4)


        tournamentRepository.save(tournament)

    }

    def "One student signs in one tournament"() {

        when: "User enrolls in tournament"
        TournamentDto dto = tournamentService.enrollInTournament(tournament.getId(), u1.getId())

        then:
        dto.getAssociatedQuiz() == null

    }

    def "Two students sign in one tournament"() {
        when: "2 Users enrolls in tournament"
        TournamentDto dto1 = tournamentService.enrollInTournament(tournament.getId(), u1.getId())
        TournamentDto dto2 = tournamentService.enrollInTournament(tournament.getId(), u2.getId())

        then:
        dto1.getAssociatedQuiz() == null
        dto2.getAssociatedQuiz() != null
        dto2.getAssociatedQuiz().numberOfQuestions == 4
        dto2.getAssociatedQuiz().getTitle() == (tournament.getTitle() + " Tournament-Quiz")

    }

    def "Three students sign in one tournament"() {
        when: "3 Users enrolls in tournament"
        TournamentDto dto1 = tournamentService.enrollInTournament(tournament.getId(), u1.getId())
        TournamentDto dto2 = tournamentService.enrollInTournament(tournament.getId(), u2.getId())
        TournamentDto dto3 = tournamentService.enrollInTournament(tournament.getId(), u3.getId())

        then:
        dto1.getAssociatedQuiz() == null
        dto2.getAssociatedQuiz() != null
        dto3.getAssociatedQuiz() != null
        dto2.getAssociatedQuiz().numberOfQuestions == 4
        dto2.getAssociatedQuiz().getTitle() == (tournament.getTitle() + " Tournament-Quiz")
        dto3.getAssociatedQuiz().numberOfQuestions == 4
        dto3.getAssociatedQuiz().getTitle() == (tournament.getTitle() + " Tournament-Quiz")

    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

    }

}