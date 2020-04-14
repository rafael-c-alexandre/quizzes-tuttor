package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listTournaments(){
        return tournamentRepository.findTournaments().stream()
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> listOpenTournaments(){
        return tournamentRepository.findOpenTournaments().stream()
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId,Integer creatorId){
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

        if(tournament == null){
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        Optional<User> user = userRepository.findById(creatorId);

        if(tournament.isClosed()){
            throw new TutorException(TOURNAMENT_ALREADY_CLOSED);
        }
        if(!user.isPresent()){
            throw new TutorException(USER_NOT_FOUND,creatorId);
        }
        if(!tournament.getTournamentCreator().equals(user.get())){
            throw new TutorException(TOURNAMENT_CANCELER_IS_NOT_CREATOR);
        }

        entityManager.remove(tournament);

        return new TournamentDto(tournament);
    }





    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto tournamentDto){
         Integer maxId;
         Random rand = new Random();
         List<Topic> topics = topicRepository.findAll();
         List<Question> questions = questionRepository.findAll();



         //TOURNAMENT HAS NO CREATOR
        if(tournamentDto.getTournamentCreator() == null){
            throw new TutorException(TOURNAMENT_HAS_NO_CREATOR);
        }
        User user = userRepository.findByUsername(tournamentDto.getTournamentCreator().getUsername());
        if(user == null)
            throw new TutorException(USER_NOT_FOUND,tournamentDto.getTournamentCreator().getUsername());

        Tournament tournament = new Tournament(tournamentDto);
        tournament.setTournamentCreator(user);

        tournament.addUser(user);

        tournament.setCreationDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));


         for(TopicDto topicDto: tournamentDto.getTopics()){
             for(Topic topic : topics){
                 if(topicDto.getName().equals(topic.getName()))
                     tournament.addTopic(topic);
             }
         }

         //SET SOME RANDOM QUESTION
        if((Integer) tournamentDto.getNumberOfQuestions() != null){
            for(int i = 0; i < tournamentDto.getNumberOfQuestions(); i++){
                while(true) {
                    boolean done = false;
                    int randomIndex = rand.nextInt(questions.size());
                    Question q = questions.get(randomIndex);
                    for(Topic t1 : q.getTopics()){
                        for(Topic t2 : tournament.getTopics()){
                            if(t1.getId() == t2.getId()) {
                                tournament.addQuestion(q);
                                questions.remove(randomIndex);
                                done = true;
                                break;
                            }
                        }
                        if(done)
                            break;
                    }
                    if(done)
                        break;
                }
            }
        }

        System.out.println(new TournamentDto(tournament));
        tournamentRepository.save(tournament);

        maxId = tournamentRepository.getMaxTournamentId();
        if(maxId == null) {
            tournamentDto.setId(1);
        }
        else{
            tournamentDto.setId(maxId + 1);
        }
        return  new TournamentDto(tournament);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInTournament(Integer tournamentId, Integer userId){
        User user = userRepository.findById(userId).get();
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

        if(user == null){
            throw new TutorException(USER_NOT_FOUND,userId);
        }
        if(tournament == null){
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }


        if(user.getRole() == User.Role.STUDENT) {
            if (tournament.openForSignings()) {
                for(User u : tournament.getSignedUsers()){
                    if(u.getId() == userId) {
                        throw new TutorException(USER_IS_ALREADY_ENROLLED);
                    }
                }
                tournament.addUser(user);

            } else {
                throw new TutorException(TOURNAMENT_IS_NOT_OPEN);
            }
        }
        else
            throw new TutorException(USER_IS_NOT_STUDENT);

        return new TournamentDto(tournament);
    }
}
