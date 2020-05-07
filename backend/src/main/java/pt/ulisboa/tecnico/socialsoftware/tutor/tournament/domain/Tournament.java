package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "available_date")
    private LocalDateTime availableDate;

    @Column(name = "conclusion_date")
    private LocalDateTime conclusionDate;

    @Column(nullable = false)
    private String title = "Title";

    @ManyToMany
    private Set<User> signedUsers = new HashSet<>();


    @ManyToMany
    private Set<Topic> topics = new HashSet<>();

    @ManyToMany
    private final Set<Question> questions = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User tournamentCreator;

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    private Quiz associatedQuiz;

    public Tournament() {
    }

    public Tournament(TournamentDto tournamentDto) {

        this.availableDate = tournamentDto.getAvailableDateDate();
        this.conclusionDate = tournamentDto.getConclusionDateDate();
        this.creationDate = tournamentDto.getCreationDateDate();
        setAvailableDate(tournamentDto.getAvailableDateDate());
        setConclusionDate(tournamentDto.getConclusionDateDate());
        setTitle(tournamentDto.getTitle());
    }

    //Getters
    public LocalDateTime getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(LocalDateTime availableDate) {
        checkAvailableDate(availableDate);
        this.availableDate = availableDate;
    }

    public LocalDateTime getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(LocalDateTime conclusionDate) {
        checkConclusionDate(conclusionDate);
        this.conclusionDate = conclusionDate;
    }

    public String getTitle() {
        return title;
    }

    //Setters
    public void setTitle(String title) {
        checkTitle(title);
        this.title = title;
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
        tournamentCreator.addCreatedTournaments(this);
        this.tournamentCreator = tournamentCreator;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        courseExecution.addTournament(this);
        this.courseExecution = courseExecution;
    }

    public void setAssociatedQuiz(Quiz associatedQuiz) {
        this.associatedQuiz = associatedQuiz;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
    }

    public void addUser(User user) {
        this.signedUsers.add(user);
        user.addSignedTournament(this);
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    @Override
    public String toString() {
        return "Tournament{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", availableDate=" + availableDate +
                ", conclusionDate=" + conclusionDate +
                ", title='" + title + '\'' +
                ", signedUsers=" + signedUsers +
                ", topics=" + topics +
                ", questions=" + questions +
                ", tournamentCreator=" + tournamentCreator +
                '}';
    }

    private void checkAvailableDate(LocalDateTime availableDate) {
        if (availableDate == null) {
            throw new TutorException(TOURNAMENT_EMPTY_DATE, "Available date");
        }
        if (availableDate.isBefore(LocalDateTime.now())) {
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
        if (conclusionDate == null) {
            throw new TutorException(TOURNAMENT_EMPTY_DATE);
        }
        if (conclusionDate.isBefore(LocalDateTime.now())) {
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
        if (this.availableDate != null &&
                conclusionDate.isBefore(availableDate)) {
            throw new TutorException(TOURNAMENT_INVALID_DATE);
        }
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public Quiz getAssociatedQuiz() {
        return associatedQuiz;
    }

    public boolean openForSignings() {
        return LocalDateTime.now().isBefore(this.availableDate);
    }

    public boolean isOpen() {
        return LocalDateTime.now().isAfter(this.availableDate) && LocalDateTime.now().isBefore(this.conclusionDate);
    }

    public boolean isClosed() {
        return LocalDateTime.now().isAfter(this.conclusionDate);
    }
}
