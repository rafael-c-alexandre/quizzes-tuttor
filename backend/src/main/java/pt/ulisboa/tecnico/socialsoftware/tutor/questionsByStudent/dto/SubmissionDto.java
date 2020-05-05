package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;




import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class SubmissionDto implements Serializable{
    private  Integer id;
    private Integer key;
    private Integer userId;
    private String status;
    private String title;
    private String content;
    private String creationDate = null;
    private List<OptionDto> options = new ArrayList<>();
    private ImageDto image;
    private List<TopicDto> topics = new ArrayList<>();
    private String justification;
    private Integer courseId;
    private boolean teacherDecision;
    private boolean madeAvailable;

    public SubmissionDto() {

    }

    public SubmissionDto(Submission submission) {
        this.id = submission.getId();
        this.key = submission.getKey();
        this.userId = submission.getUser().getId();
        this.status = submission.getSubmissionStatus().name();
        this.title = submission.getTitle();
        this.content = submission.getContent();
        this.options = submission.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
        this.topics = submission.getTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());

        if (submission.getImage() != null)
            this.image = new ImageDto(submission.getImage());
        this.creationDate = DateHandler.toISOString(submission.getCreationDate());


        this.justification = submission.getJustification();
        this.courseId = submission.getCourse().getId();
        this.teacherDecision = submission.getTeacherDecision();
        this.madeAvailable = submission.isMadeAvailable();
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

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String questionTitle) {
        this.title = questionTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String questionContent) {
        this.content = questionContent;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public List<OptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {
        this.options = options;
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public boolean isTeacherDecision() {
        return teacherDecision;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public boolean isMadeAvailable() {
        return madeAvailable;
    }

    public void setMadeAvailable(boolean madeAvailable) {
        this.madeAvailable = madeAvailable;
    }

    @Override
    public String toString() {
        return "SubmissionDto{" +
                "id=" + id +
                ", key=" + key +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", options=" + options +
                ", image=" + image +
                ", topics=" + topics +
                ", justification='" + justification + '\'' +
                ", courseId=" + courseId +
                ", teacherDecision=" + teacherDecision +
                ", madeAvailable=" + madeAvailable +
                '}';
    }
}
