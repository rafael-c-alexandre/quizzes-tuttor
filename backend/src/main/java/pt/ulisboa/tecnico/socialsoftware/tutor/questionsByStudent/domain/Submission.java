package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
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

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL,  mappedBy = "submission", orphanRemoval=true)
    private List<Option> options = new ArrayList<>();

    @ManyToMany(mappedBy = "submissions")
    private Set<Topic> topics = new HashSet<>();

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    private String justification;

    private boolean teacherDecision;

    @Enumerated(EnumType.STRING)
    private Status submissionStatus = Status.ONHOLD;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


    public Submission(){

    }

    public Submission(SubmissionDto submissionDto, User user, Course course) {
        checkConsistentSubmission(submissionDto);
        this.title = submissionDto.getTitle();
        this.key = submissionDto.getKey();
        this.content = submissionDto.getContent();

        this.course = course;
        course.addSubmission(this);

        if (submissionDto.getImage() != null) {
            Image img = new Image(submissionDto.getImage());
            setImage(img);

        }

        int index = 0;
        for (OptionDto optionDto : submissionDto.getOptions()) {
            optionDto.setSequence(index++);
            Option option = new Option(optionDto);
            this.options.add(option);
            option.setSubmission(this);

        }
        this.user = user;
        this.justification = "";
        this.teacherDecision = false;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
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
    }

    private void checkConsistentSubmission(SubmissionDto submissionDto) {
        if (submissionDto.getTitle().trim().length() == 0 ||
                submissionDto.getContent().trim().length() == 0 ||
                submissionDto.getOptions().stream().anyMatch(optionDto -> optionDto.getContent().trim().length() == 0)) {
           // throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (submissionDto.getOptions().stream().filter(OptionDto::getCorrect).count() != 1) {
            //throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }
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
    public void update(SubmissionDto submissionDto) {
        checkConsistentSubmission(submissionDto);

        setTitle(submissionDto.getTitle());
        setContent(submissionDto.getContent());

        submissionDto.getOptions().forEach(optionDto -> {
            Option option = getOptionById(optionDto.getId());
            if (option == null) {
                throw new TutorException(OPTION_NOT_FOUND, optionDto.getId());
            }
            option.setContent(optionDto.getContent());
            option.setCorrect(optionDto.getCorrect());
        });
    }
}
