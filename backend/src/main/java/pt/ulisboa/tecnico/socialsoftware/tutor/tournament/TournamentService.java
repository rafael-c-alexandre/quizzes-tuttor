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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

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
        Tournament tournament = tournamentRepository.findTournamentById(tournamentId);

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
         Integer maxId;

         //TOURNAMENT HAS NO CREATOR
        if(tournamentDto.getTournamentCreator() == null){
            throw new TutorException(TOURNAMENT_HAS_NO_CREATOR);
        }
        //TOURNAMENT INVALID STATE
        if(tournamentDto.getState() == null || tournamentDto.getState() != Tournament.TournamentState.CREATED){
            throw new TutorException(TOURNAMENT_INVALID_STATE);
        }

         Tournament tournament = new Tournament(tournamentDto);

        //TOURNAMENT CREATOR IS A SIGNED USER
        if (tournamentDto.getTournamentCreator() != null){
            if (!tournamentDto.getSignedUsers().contains(tournamentDto.getTournamentCreator())){
                tournamentDto.addUser(tournamentDto.getTournamentCreator());
            }
        }
         getTopics(tournamentDto, tournament);
         getCreator(tournamentDto, tournament);
         getUsers(tournamentDto,tournament);
         if(tournamentDto.getCreationDate() == null){
             tournament.setCreationDate(LocalDateTime.now());
         } else{
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
             tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
         }


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

    private void getTopics(TournamentDto tournamentDto, Tournament tournament) {
        //tournament topics
        if(tournamentDto.getTopics() != null){
            if(tournamentDto.getTopics().isEmpty()){
                throw new TutorException(TOURNAMENT_NO_TOPICS);
            }
            for (Integer topicId : tournamentDto.getTopics()){
                Topic topic = topicRepository.findById(topicId)
                        .orElseThrow(() -> new TutorException(TOPIC_NOT_FOUND,topicId));
                tournament.addTopic(topic);

            }
        }
        else
            throw new TutorException(TOURNAMENT_NO_TOPICS);
    }

    private void getUsers(TournamentDto tournamentDto, Tournament tournament) {
        //tournament users
        if(tournamentDto.getSignedUsers() != null){
            for (Integer userId : tournamentDto.getSignedUsers()){
                if(userId == null){
                    throw new TutorException(USER_NOT_FOUND,-1);
                }
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
                tournament.addUser(user);
            }
        }
    }

    private void getCreator(TournamentDto tournamentDto, Tournament tournament){
        Integer creatorId = tournamentDto.getTournamentCreator();
        if(creatorId != null){
            User user = userRepository.findById(creatorId)
                    .orElseThrow(() -> new TutorException(USER_NOT_FOUND,creatorId));
            tournament.setTournamentCreator(user);
        }
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
            if (tournament.getState() == Tournament.TournamentState.CREATED) {
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
