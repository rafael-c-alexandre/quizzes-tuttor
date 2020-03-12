package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(
        name = "tournaments"
        )
public class Tournament {

    public enum TournamentState {
        OPEN, ONGOING, CLOSED
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;


    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(nullable = false)
    private String title = "Title";

    @Enumerated(EnumType.STRING)
    private Tournament.TournamentState state;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> signedUsers = new HashSet<>();


    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Topic> topics = new HashSet<>();


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User tournamentCreator;

    public Tournament() {}

    public  Tournament(TournamentDto tournamentDto){

        this.id = tournamentDto.getId();
        this.availableDate = tournamentDto.getAvailableDateDate();
        this.conclusionDate = tournamentDto.getConclusionDateDate();
        this.creationDate = tournamentDto.getCreationDateDate();
        setAvailableDate(tournamentDto.getAvailableDateDate());
        setConclusionDate(tournamentDto.getConclusionDateDate());
        this.tournamentCreator = tournamentDto.getTournametCreator();
        setTitle(tournamentDto.getTitle());
        this.state = tournamentDto.getState();


    }



    public void setTitle(String title) {
        checkTitle(title);
        this.title = title;
    }

    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }



    public String getTitle() {
        return title;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        checkConclusionDate(conclusionDate);
        this.conclusionDate = conclusionDate;
    }


    public void setAvailableDate(LocalDateTime availableDate) {
        checkAvailableDate(availableDate);
        this.availableDate = availableDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public TournamentState getState() {
        return state;
    }

    public void setState(TournamentState state) {
        this.state = state;
    }

    public Set<User> getSignedUsers() {
        return signedUsers;
    }


    public void setSignedUsers(Set<User> signedUsers) {
        this.signedUsers = signedUsers;
    }



    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public User getTournamentCreator() {
        return tournamentCreator;
    }



    public void setTournamentCreator(User tournamentCreator) {
        this.tournamentCreator = tournamentCreator;
    }


    public void addTopic(Topic topic){
        this.topics.add(topic);
    }

    public void addUser(User user){
        this.signedUsers.add(user);
    }

    public void closeTournament(){
        if(this.state == TournamentState.CLOSED){
            throw new TutorException(TOURNAMENT_ALREADY_CLOSED);
        }
    }



    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", title='" + title + '\'' +
                ", state=" + state +
                ", creator=" + tournamentCreator +
                '}';
    }


    private void checkAvailableDate(LocalDateTime availableDate) {
        if (availableDate == null) {
            throw new TutorException(TOURNAMENT_EMPTY_DATE, "Available date");
        }
        if(availableDate.isBefore(LocalDateTime.now())){
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
        if (this.conclusionDate != null && conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
    }


    private void checkTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            throw new TutorException(TOURNAMENT_TITLE_IS_EMPTY);
        }
    }

    private void checkConclusionDate(LocalDateTime conclusionDate) {
        if(conclusionDate == null){
            throw new TutorException(TOURNAMENT_EMPTY_DATE);
        }
        if(conclusionDate.isBefore(LocalDateTime.now())){
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
        if (    this.availableDate != null &&
                conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
    }







}
