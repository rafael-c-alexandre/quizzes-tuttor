package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import javax.persistence.*;


@Entity
@Table(name = "submissions")

public class Submission {
    public enum Status {
        ONHOLD, REJECTED, APPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    private Question question;

    private String justification;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ONHOLD;

    public Submission(){

    }

    public Submission(Question question, int userID) {
        this.question = question;
        this.user = user;
        this.justification = "";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
