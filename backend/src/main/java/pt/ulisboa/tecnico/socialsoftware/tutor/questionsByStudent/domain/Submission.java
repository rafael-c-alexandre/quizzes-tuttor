package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Entity
@Table(name = "submissions")

public class Submission {
    public enum Status {
        ONHOLD, REJECTED, APPROVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;


    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "submission", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @ManyToMany(mappedBy = "submissions")
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String justification;

    private boolean teacherDecision;

    private boolean madeAvailable;

    private boolean changeTitle;

    private boolean changeContent;

    private boolean changeOptions;

    private boolean changeCorrect;

    @Enumerated(EnumType.STRING)
    private Status submissionStatus = Status.ONHOLD;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public Submission() {

    }

    public Submission(SubmissionDto submissionDto, User user, Course course) {
        setTitle(submissionDto.getTitle());
        setKey(submissionDto.getKey());
        setContent(submissionDto.getContent());
        setCreationDate(DateHandler.toLocalDateTime(submissionDto.getCreationDate()));
        setCourse(course);
        setOptions(submissionDto.getOptions());

        if (submissionDto.getImage() != null)
            setImage(new Image(submissionDto.getImage()));
        setUser(user);
        setJustification("");
        setTeacherDecision(false);
        setMadeAvailable(false);

        checkImproveFields(submissionDto);

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {

        this.user = user;
        user.addSubmission(this);

    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Status getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(Status status) {
        this.submissionStatus = status;
    }

    public boolean getTeacherDecision() {
        return teacherDecision;
    }

    public void setTeacherDecision(boolean teacherDecision) {
        this.teacherDecision = teacherDecision;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public boolean isMadeAvailable() {
        return madeAvailable;
    }

    public void setMadeAvailable(boolean madeAvailable) {
        this.madeAvailable = madeAvailable;
    }

    public void setContent(String content) {
        if (content == null || content.isBlank())
            throw new TutorException(INVALID_CONTENT_FOR_QUESTION);

        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.isBlank())
            throw new TutorException(INVALID_TITLE_FOR_QUESTION);
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setSubmission(this);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        if (this.creationDate == null) {
            this.creationDate = DateHandler.now();
        } else {
            this.creationDate = creationDate;
        }
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDto> options) {

        if (options.stream().filter(OptionDto::getCorrect).count() != 1) {
            throw new TutorException(ONE_CORRECT_OPTION_NEEDED);
        }

        int index = 0;
        for (OptionDto optionDto : options) {
            if (optionDto.getId() == null) {
                optionDto.setSequence(index++);
                new Option(optionDto).setSubmission(this);
            } else {
                Option option = getOptions()
                        .stream()
                        .filter(op -> op.getId().equals(optionDto.getId()))
                        .findFirst()
                        .orElseThrow(() -> new TutorException(OPTION_NOT_FOUND, optionDto.getId()));

                option.setContent(optionDto.getContent());
                option.setCorrect(optionDto.getCorrect());
            }
        }

    }

    public void addOption(Option option) {
        options.add(option);
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }

    public boolean isTeacherDecision() {
        return teacherDecision;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        course.addSubmission(this);
    }

    public void updateTopics(Set<Topic> newTopics) {
        Set<Topic> toRemove = this.topics.stream().filter(topic -> !newTopics.contains(topic)).collect(Collectors.toSet());

        toRemove.forEach(topic -> {
            this.topics.remove(topic);
            topic.getSubmissions().remove(this);
        });

        newTopics.stream().filter(topic -> !this.topics.contains(topic)).forEach(topic -> {
            this.topics.add(topic);
            topic.getSubmissions().add(this);
        });
    }

    private Option getOptionById(Integer id) {
        return getOptions().stream().filter(option -> option.getId().equals(id)).findAny().orElse(null);
    }


    public boolean isChangeTitle() {
        return changeTitle;
    }

    public void setChangeTitle(boolean changeTitle) {
        this.changeTitle = changeTitle;
    }

    public boolean isChangeContent() {
        return changeContent;
    }

    public void setChangeContent(boolean changeContent) {
        this.changeContent = changeContent;
    }

    public boolean isChangeOptions() {
        return changeOptions;
    }

    public void setChangeOptions(boolean changeOptions) {
        this.changeOptions = changeOptions;
    }

    public boolean isChangeCorrect() {
        return changeCorrect;
    }

    public void setChangeCorrect(boolean changeCorrect) {
        this.changeCorrect = changeCorrect;
    }

    public void update(SubmissionDto submissionDto) {

        setTitle(submissionDto.getTitle());
        setContent(submissionDto.getContent());
        setOptions(submissionDto.getOptions());
    }

    public Question convertToQuestion() {
        Question question = new Question();
        question.setTitle(this.getTitle());
        question.setKey(this.getKey());
        question.setContent(this.getContent());
        question.setStatus(Question.Status.AVAILABLE);
        question.setCreationDate(DateHandler.toLocalDateTime(this.getCreationDate().toString()));
        question.setCourse(this.course);

        int index = 0;
        for (Option option : getOptions()) {
            option.setSequence(index++);
            option.setQuestion(question);
        }

        if (this.getImage() != null)
            question.setImage(new Image(new ImageDto(this.getImage())));

        setMadeAvailable(true);
        return question;

    }

    public void checkImproveFields(SubmissionDto submissionDto) {
        if (submissionDto.getFieldsToImprove().contains("title")) this.changeTitle = true;
        if (submissionDto.getFieldsToImprove().contains("content")) this.changeContent = true;
        if (submissionDto.getFieldsToImprove().contains("options")) this.changeOptions = true;
        if (submissionDto.getFieldsToImprove().contains("correct option")) this.changeCorrect = true;
    }

}
