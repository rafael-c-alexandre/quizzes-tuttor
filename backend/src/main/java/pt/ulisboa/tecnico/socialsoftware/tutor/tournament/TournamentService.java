package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    public List<TournamentDto> listTournamentsByState(String state){
        return tournamentRepository.findTournamentsByState(state).stream()
                .map(tournament -> new TournamentDto(tournament))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto cancelTournament(Integer tournamentId,Integer creatorId){
        System.out.println(tournamentId);
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);
        System.out.println(tournament);

        if(tournament == null){
            throw new TutorException(TOURNAMENT_ID_NOT_EXISTS);
        }

        Optional<User> user = userRepository.findById(creatorId);

        if(tournament.getState() == Tournament.TournamentState.CLOSED){
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

         Integer maxId = tournamentRepository.getMaxTournamentId();

         if(maxId == null) {
             tournamentDto.setId(1);
         }
         else{
             tournamentDto.setId(maxId + 1);
         }
         Tournament tournament = new Tournament(tournamentDto);
         getTopics(tournamentDto, tournament);

         if(tournamentDto.getCreationDate() == null){
             tournament.setCreationDate(LocalDateTime.now());
         } else{
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
             tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
         }

        if (tournament.getTournamentCreator() != null){
            if (!tournament.getSignedUsers().contains(tournament.getTournamentCreator())){
                tournament.addUser(tournament.getTournamentCreator());
            }
        }

        tournamentRepository.save(tournament);

        return  new TournamentDto(tournament);
    }

    private void getTopics(TournamentDto tournamentDto, Tournament tournament) {
        //tournament topics
        if(tournamentDto.getTopics() != null){
            if(tournamentDto.getTopics().isEmpty()){
                throw new TutorException(TOURNAMENT_NO_TOPICS);
            }
            for (TopicDto topicDto : tournamentDto.getTopics()){
                Topic topic = topicRepository.findById(topicDto.getId())
                        .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND,topicDto.getId()));
                tournament.addTopic(topic);

            }
        }
        else
            throw new TutorException(TOURNAMENT_NO_TOPICS);
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInTournament(User user, TournamentDto tournamentDto){
        if(user.getRole() == User.Role.STUDENT) {
            if (tournamentDto.getState() == Tournament.TournamentState.OPEN) {
                if(!(tournamentDto.getSignedUsers().contains(user))){
                    tournamentDto.addUser(user);
                }
                else{
                    throw new TutorException(USER_IS_ALREADY_ENROLLED);
                }
            } else {
                throw new TutorException(TOURNAMENT_IS_NOT_OPEN);
            }
        }
        else
            throw new TutorException(USER_IS_NOT_STUDENT);

        return tournamentDto;
    }
}
