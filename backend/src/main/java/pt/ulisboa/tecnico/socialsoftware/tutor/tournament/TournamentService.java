package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizService quizService;


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listTournaments() {
        return tournamentRepository.findTournaments().stream()
                .map(TournamentDto::new)
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenTournaments(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));


        Set<Integer> studentQuizIds =  user.getQuizAnswers().stream()
                .map(QuizAnswer::getQuiz)
                .map(Quiz::getId)
                .collect(Collectors.toSet());

        return tournamentRepository.findOpenTournaments().stream()
                .map(TournamentDto::new)
                .filter(tournament -> tournament.getSignedUsers().contains(userId)
                        && tournament.getAssociatedQuiz() != null
                        && !studentQuizIds.contains(tournament.getAssociatedQuiz().getId()))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listSignableTournaments(Integer userId) {
        return tournamentRepository.findSignableTournaments().stream()
                .map(TournamentDto::new)
                .filter(tournament -> !tournament.getSignedUsers().contains(userId))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listClosedTournaments() {
        return tournamentRepository.findClosedTournaments().stream()
                .map(TournamentDto::new)
                .collect(Collectors.toList());
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId, Integer creatorId) {
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

        if (tournament == null) {
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        User user = userRepository.findById(creatorId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, creatorId));


        if (tournament.isClosed()) {
            throw new TutorException(TOURNAMENT_ALREADY_CLOSED);
        }
        if (!tournament.getTournamentCreator().equals(user)) {
            throw new TutorException(TOURNAMENT_CANCELER_IS_NOT_CREATOR);
        }
        if(tournament.getAssociatedQuiz() != null)
            quizService.removeQuiz(tournament.getAssociatedQuiz().getId());


        tournament.getTournamentCreator().getCreatedTournaments().remove(tournament);
        tournament.getCourseExecution().getTournaments().remove(tournament);
        tournament.getSignedUsers().forEach(user1 -> user1.getSignedTournaments().remove(tournament));


        tournamentRepository.delete(tournament);


        return new TournamentDto(tournament);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelLastTournament() {
        //get last created tournament
        Tournament tournament = tournamentRepository.findTournamentById(tournamentRepository.getMaxTournamentId());

        if (tournament == null) {
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        if (tournament.isClosed()) {
            throw new TutorException(TOURNAMENT_ALREADY_CLOSED);
        }

        /*
        if(!tournament.getSignedUsers().isEmpty())
            throw new TutorException(TOURNAMENT_ALREADY_HAS_USERS);
        */
        tournament.getTournamentCreator().getCreatedTournaments().remove(tournament);
        tournament.getCourseExecution().getTournaments().remove(tournament);
        tournament.getSignedUsers().forEach(user1 -> user1.getSignedTournaments().remove(tournament));

        tournamentRepository.delete(tournament);

        return new TournamentDto(tournament);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto tournamentDto, Integer creatorId, Integer executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        Integer maxId;
        Random rand = new Random();
        User user = userRepository.findById(creatorId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, creatorId));
        List<Question> questions = questionRepository.findAll();
        List<Tournament> tournaments = tournamentRepository.findAll();


        for (Tournament t : tournaments) {
            if (tournamentDto.getTitle().equals(t.getTitle()))
                throw new TutorException(TOURNAMENT_TITLE_ALREADY_USED, tournamentDto.getTitle());
        }


        Tournament tournament = new Tournament(tournamentDto);
        tournament.setTournamentCreator(user);
        tournament.setCourseExecution(courseExecution);


        tournament.setCreationDate(DateHandler.now().truncatedTo(ChronoUnit.SECONDS));


        for (TopicDto topicDto : tournamentDto.getTopics()) {
            Topic t = topicRepository.findById(topicDto.getId()).orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND, topicDto.getId()));
            tournament.addTopic(t);
        }

        //SET SOME RANDOM QUESTION
        for (int i = 0; i < tournamentDto.getNumberOfQuestions(); i++) {
            while (true) {
                boolean done = false;
                int randomIndex = rand.nextInt(questions.size());
                Question q = questions.get(randomIndex);
                for (Topic t1 : q.getTopics()) {
                    for (Topic t2 : tournament.getTopics()) {
                        if (t1.getId().equals(t2.getId())) {
                            tournament.addQuestion(q);
                            questions.remove(randomIndex);
                            done = true;
                            break;
                        }
                    }
                    if (done)
                        break;
                }
                if (done)
                    break;
            }
        }

        tournamentRepository.save(tournament);

        maxId = tournamentRepository.getMaxTournamentId();
        if (maxId == null) {
            tournamentDto.setId(1);
        } else {
            tournamentDto.setId(maxId + 1);
        }
        return new TournamentDto(tournament);
    }


    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInTournament(Integer tournamentId, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);


        if (tournament == null) {
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        if (user.getRole() == User.Role.STUDENT) {
            if (tournament.openForSignings()) {
                for (User u : tournament.getSignedUsers()) {
                    if (u.getId().equals(userId)) {
                        throw new TutorException(USER_IS_ALREADY_ENROLLED);
                    }
                }
                tournament.addUser(user);
            } else {
                throw new TutorException(TOURNAMENT_IS_NOT_OPEN);
            }
        } else
            throw new TutorException(USER_IS_NOT_STUDENT);

        //Generate new quiz after user reach 2
        if (tournament.getSignedUsers().size() == 2) {
            Quiz quiz = new Quiz();
            quiz.setKey(getMaxQuizKey() + 1);
            quiz.setAssociatedTournament(tournament);
            quiz.generate(new ArrayList<>(tournament.getQuestions()));
            quiz.setTitle(tournament.getTitle() + " Tournament-Quiz");
            quiz.setCourseExecution(tournament.getCourseExecution());
            quizRepository.save(quiz);
            tournament.setAssociatedQuiz(quiz);
        }

        return new TournamentDto(tournament);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInLastTournament(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        //get last created tournament
        Tournament tournament = tournamentRepository.findTournamentById(tournamentRepository.getMaxTournamentId());

        if (tournament == null) {
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        if (user.getRole() == User.Role.STUDENT) {
            if (tournament.openForSignings()) {
                for (User u : tournament.getSignedUsers()) {
                    if (u.getId().equals(userId)) {
                        throw new TutorException(USER_IS_ALREADY_ENROLLED);
                    }
                }
                tournament.addUser(user);
            } else {
                throw new TutorException(TOURNAMENT_IS_NOT_OPEN);
            }
        } else
            throw new TutorException(USER_IS_NOT_STUDENT);

        //Generate new quiz after user reach 2
        if (tournament.getSignedUsers().size() == 2) {
            Quiz quiz = new Quiz();
            quiz.setKey(quizService.getMaxQuizKey() + 1);
            quiz.setAssociatedTournament(tournament);
            quiz.generate(new ArrayList<>(tournament.getQuestions()));
            quiz.setTitle(tournament.getTitle() + " Tournament-Quiz");
            quiz.setCourseExecution(tournament.getCourseExecution());
            quizRepository.save(quiz);
            tournament.setAssociatedQuiz(quiz);
        }

        return new TournamentDto(tournament);
    }

    public Integer getMaxQuizKey() {
        Integer maxQuizKey = quizRepository.getMaxQuizKey();
        return maxQuizKey != null ? maxQuizKey : 0;
    }

}
