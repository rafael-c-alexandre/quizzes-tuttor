package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.AuthUserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class TournamentDto {

    private Integer id;
    private String title;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private AuthUserDto tournamentCreator;
    private int numberOfSignedUsers;
    private int numberOfTopics;
    private int numberOfQuestions;
    private List<QuestionDto> questions = new ArrayList<>();
    private List<AuthUserDto> signedUsers = new ArrayList<>();
    private List<TopicDto> topics = new ArrayList<>();


    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TournamentDto() {
    }

    @Override
    public String toString() {
        return "TournamentDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", tournamentCreator=" + tournamentCreator +
                ", numberOfSignedUsers=" + numberOfSignedUsers +
                ", numberOfTopics=" + numberOfTopics +
                ", numberOfQuestions=" + numberOfQuestions +
                ", questions=" + questions +
                ", signedUsers=" + signedUsers +
                ", topics=" + topics +
                ", formatter=" + formatter +
                '}';
    }

    public TournamentDto(Tournament tournament) {

        this.id = tournament.getId();
        if(tournament.getAvailableDate() != null)
            this.availableDate = tournament.getAvailableDate().format(formatter);
        if(tournament.getCreationDate() != null)
            this.creationDate = tournament.getCreationDate().format(formatter);
        if(tournament.getConclusionDate() != null)
            this.conclusionDate = tournament.getConclusionDate().format(formatter);
        if(tournament.getSignedUsers() != null) {
            this.numberOfSignedUsers = tournament.getSignedUsers().size();
            this.signedUsers = tournament.getSignedUsers().stream()
                    .map(user -> {
                        AuthUserDto userDto = new AuthUserDto(user);
                        return userDto;
                    })
                    .collect(Collectors.toList());
        }
        if(tournament.getQuestions() != null){
            this.numberOfQuestions = tournament.getQuestions().size();
            this.questions = tournament.getQuestions().stream()
                    .map(question -> {
                        QuestionDto questionDto = new QuestionDto(question);
                        return questionDto;
                    })
                    .collect(Collectors.toList());
        }
        this.title = tournament.getTitle();
        if(tournament.getTopics() != null) {
            this.numberOfTopics = tournament.getTopics().size();
            this.topics = tournament.getTopics().stream()
                    .map(topic -> {
                        TopicDto topicDto = new TopicDto(topic);
                        return topicDto;
                    })
                    .collect(Collectors.toList());
        }
        if(tournament.getTournamentCreator() != null)
            this.tournamentCreator = new AuthUserDto(tournament.getTournamentCreator());




    }

    //Getters

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


    public void addUser(AuthUserDto user) {
        this.signedUsers.add(user);
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public AuthUserDto getTournamentCreator() {
        return tournamentCreator;
    }

    public void setTournamentCreator(AuthUserDto tournamentCreator) {
        this.tournamentCreator = tournamentCreator;
    }

    public List<AuthUserDto> getSignedUsers() {
        return signedUsers;
    }

    public void setSignedUsers(List<AuthUserDto> signedUsers) {
        this.signedUsers = signedUsers;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }
}
