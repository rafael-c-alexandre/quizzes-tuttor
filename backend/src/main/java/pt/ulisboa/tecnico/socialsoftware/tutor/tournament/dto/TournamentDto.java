package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TournamentDto {

    @Transient
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Integer id;
    private String title;
    private String creationDate = null;
    private String availableDate = null;
    private String conclusionDate = null;
    private int tournamentCreator;
    private int numberOfSignedUsers;
    private int numberOfTopics;
    private int numberOfQuestions;
    private List<QuestionDto> questions = new ArrayList<>();
    private List<Integer> signedUsers = new ArrayList<>();
    private List<TopicDto> topics = new ArrayList<>();
    private QuizDto associatedQuiz;

    public TournamentDto() {
    }

    public TournamentDto(Tournament tournament) {

        this.id = tournament.getId();
        if (tournament.getAvailableDate() != null)
            this.availableDate = tournament.getAvailableDate().format(formatter);
        if (tournament.getCreationDate() != null)
            this.creationDate = tournament.getCreationDate().format(formatter);
        if (tournament.getConclusionDate() != null)
            this.conclusionDate = tournament.getConclusionDate().format(formatter);
        if (tournament.getSignedUsers() != null) {
            this.numberOfSignedUsers = tournament.getSignedUsers().size();
            this.signedUsers = tournament.getSignedUsers().stream()
                    .map(user -> {
                        int id = user.getId();
                        return id;
                    })
                    .collect(Collectors.toList());
        }
        if (tournament.getQuestions() != null) {
            this.numberOfQuestions = tournament.getQuestions().size();
            this.questions = tournament.getQuestions().stream()
                    .map(question -> {
                        QuestionDto questionDto = new QuestionDto(question);
                        return questionDto;
                    })
                    .collect(Collectors.toList());
        }
        this.title = tournament.getTitle();
        if (tournament.getTopics() != null) {
            this.numberOfTopics = tournament.getTopics().size();
            this.topics = tournament.getTopics().stream()
                    .map(topic -> {
                        TopicDto topicDto = new TopicDto(topic);
                        return topicDto;
                    })
                    .collect(Collectors.toList());
        }
        if (tournament.getTournamentCreator() != null)
            this.tournamentCreator = tournament.getTournamentCreator().getId();

        if (tournament.getAssociatedQuiz() != null)
            this.associatedQuiz = new QuizDto(tournament.getAssociatedQuiz(), true);


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

    //Getters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    //Setters

    public int getNumberOfSignedUsers() {
        return numberOfSignedUsers;
    }

    public void setNumberOfSignedUsers(int numberOfSignedUsers) {
        this.numberOfSignedUsers = numberOfSignedUsers;
    }

    public int getNumberOfTopics() {
        return numberOfTopics;
    }

    public void setNumberOfTopics(int numberOfTopics) {
        this.numberOfTopics = numberOfTopics;
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

    public QuizDto getAssociatedQuiz() {
        return associatedQuiz;
    }

    public void setAssociatedQuiz(QuizDto associatedQuiz) {
        this.associatedQuiz = associatedQuiz;
    }

    public void addUser(Integer user) {
        this.signedUsers.add(user);
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public Integer getTournamentCreator() {
        return tournamentCreator;
    }

    public void setTournamentCreator(int tournamentCreator) {
        this.tournamentCreator = tournamentCreator;
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

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
}
