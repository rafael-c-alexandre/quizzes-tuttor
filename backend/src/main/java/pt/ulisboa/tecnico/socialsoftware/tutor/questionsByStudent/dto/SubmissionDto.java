package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;




import java.io.Serializable;


public class SubmissionDto implements Serializable{
    private  Integer id;
    private Integer userId;
    private String status;
    private Integer questionId;
    private String justification;
    private Integer courseId;
    private boolean teacherDecision;

    public SubmissionDto() {

    }

    public SubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.userId = submission.getUser().getId();
        this.status = submission.getStatus().name();
        this.questionId = submission.getQuestion().getId();
        this.justification = submission.getJustification();
        this.courseId = submission.getCourse().getId();
        this.teacherDecision = submission.getTeacherDecision();
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

    public void setUser(Integer userId) {
        this.userId = userId;
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

    public void setQuestionId(int question) {
        this.questionId = question;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public boolean getTeacherDecision() {
        return teacherDecision;
    }

    public void setTeacherDecision(boolean teacherDecision) {
        this.teacherDecision = teacherDecision;
    }

    @Override
    public String toString() {
        return "SubmissionDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", questionId=" + questionId +
                ", justification='" + justification + '\'' +
                ", courseId=" + courseId +
                ", teacherDecision=" + teacherDecision +
                '}';
    }
}
