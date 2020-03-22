package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;



import java.io.Serializable;


public class SubmissionDto implements Serializable{
    private  Integer id;
    private Integer userId;
    private String status;
    private Integer questionId;
    private String justification;
    private Integer courseId;

    public SubmissionDto() {

    }

    public SubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.userId = submission.getUser().getId();
        this.status = submission.getStatus().name();
        this.questionId = submission.getQuestion().getId();
        this.justification = submission.getJustification();
        this.courseId = submission.getCourse().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUser(Integer user) {
        this.userId = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestion(Integer question) {
        this.questionId = question;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getCourse() {
        return courseId;
    }

    public void setCourse(Integer course) {
        this.courseId = course;
    }

    @Override
    public String toString() {
        return "SubmissionDto{" +
                "id=" + id +
                ", user=" + userId +
                ", status='" + status + '\'' +
                ", question=" + questionId +
                ", justification='" + justification + '\'' +
                ", course=" + courseId +
                '}';
    }
}
