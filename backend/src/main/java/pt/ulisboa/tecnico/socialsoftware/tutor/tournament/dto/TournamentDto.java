package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TournamentDto {

    private Integer id;
    private String title;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private Tournament.TournamentState state;
    private List<Integer> topics = new ArrayList<>();
    private Integer tournamentCreator;
    private int numberOfSignedUsers;
    private int numberOfTopics;
    private List<Integer> signedUsers = new ArrayList<>();


    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {

        this.id = tournament.getId();
        this.availableDate = tournament.getAvailableDate().format(formatter);
        this.creationDate = tournament.getCreationDate().format(formatter);
        this.conclusionDate = tournament.getConclusionDate().format(formatter);
        if(this.getSignedUsers() != null)
            this.numberOfSignedUsers = tournament.getSignedUsers().size();
        this.state = tournament.getState();
        this.title = tournament.getTitle();
        if(tournament.getTopics() != null)
            this.numberOfTopics = tournament.getTopics().size();
        if(tournament.getTournamentCreator() != null)
            this.tournamentCreator = tournament.getTournamentCreator().getId();

    }

    //Getters

    public Tournament.TournamentState getState() {
        return state;
    }
    public Integer getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getCreationDate() {
        return creationDate;
    }
    public String getAvailableDate() {
        return availableDate;
    }
    public String getConclusionDate() {
        return conclusionDate;
    }
    public int getNumberOfSignedUsers() {
        return numberOfSignedUsers;
    }
    public int getNumberOfTopics() {
        return numberOfTopics;
    }
    public LocalDateTime getCreationDateDate() {
        if (getCreationDate() == null || getCreationDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getCreationDate(), formatter);
    }
    public LocalDateTime getAvailableDateDate() {
        if (getAvailableDate() == null || getAvailableDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getAvailableDate(), formatter);
    }
    public LocalDateTime getConclusionDateDate() {
        if (getConclusionDate() == null || getConclusionDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getConclusionDate(), formatter);

    }

    //Setters




    public void setState(Tournament.TournamentState state) {
        this.state = state;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }
    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }
    public void setNumberOfSignedUsers(int numberOfSignedUsers) {
        this.numberOfSignedUsers = numberOfSignedUsers;
    }
    public void setNumberOfTopics(int numberOfTopics) {
        this.numberOfTopics = numberOfTopics;
    }


    public void addUser(int id) {
        this.signedUsers.add(id);
    }

    public List<Integer> getTopics() {
        return topics;
    }

    public void setTopics(List<Integer> topics) {
        this.topics = topics;
    }

    public Integer getTournamentCreator() {
        return tournamentCreator;
    }

    public void setTournamentCreator(Integer tournamentCreator) {
        this.tournamentCreator = tournamentCreator;
    }

    public List<Integer> getSignedUsers() {
        return signedUsers;
    }

    public void setSignedUsers(List<Integer> signedUsers) {
        this.signedUsers = signedUsers;
    }
}
