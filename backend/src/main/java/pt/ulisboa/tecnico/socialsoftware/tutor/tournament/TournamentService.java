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

import javax.persistence.EntityManager;
import javax.validation.constraints.Null;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TopicRepository topicRepository;

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
    public TournamentDto createTournament(TournamentDto tournamentDto){

         Integer maxId = tournamentRepository.getMaxTournamentId();

         if(maxId == null) {
             tournamentDto.setId(1);
         }
         else{
             tournamentDto.setId(maxId + 1);
         }


         Tournament tournament = new Tournament(tournamentDto);


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
         if(tournamentDto.getCreationDate() == null){
             tournament.setCreationDate(LocalDateTime.now());
         } else{
             DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
             tournament.setCreationDate(LocalDateTime.parse(tournamentDto.getCreationDate(), formatter));
         }


        tournamentRepository.save(tournament);

        return new TournamentDto(tournament);
    }









}
