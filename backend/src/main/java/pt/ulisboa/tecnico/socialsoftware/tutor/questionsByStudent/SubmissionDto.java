package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;


import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;



import java.io.Serializable;


public class SubmissionDto implements Serializable{
    private  Integer id;
    private User user;
    private String status;
    private Question question;
    private String justification;

    public SubmissionDto() {

    }

    public SubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.user = submission.getUser();
        this.status = submission.getStatus().name();
        this.question = submission.getQuestion();
        this.justification = submission.getJustification();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "SubmissionDto{" +
                "id=" + id +
                ", user=" + user.getId() +
                ", status='" + status + '\'' +
                ", question=" + question +
                ", justification='" + justification + '\'' +
                '}';
    }
}
