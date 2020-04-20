package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class QuestionsByStudentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ImageRepository imageRepository;



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto studentSubmitQuestion(SubmissionDto submissionDto, int userId, int courseId) {

        isStudent(userId);
        User student = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND,courseId));

        if (submissionDto.getKey() == null) {
            int maxQuestionNumber = submissionRepository.getMaxSubmissionNumber() != null ?
                    submissionRepository.getMaxSubmissionNumber() : 0;
            submissionDto.setKey(maxQuestionNumber + 1);
        }

        Submission submission = new Submission(submissionDto, student, course );
        submission.setCreationDate(LocalDateTime.now());
        student.addSubmission(submission);
        course.addSubmission(submission);

        submissionRepository.save(submission);
        return new SubmissionDto(submission);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SubmissionDto> findQuestionsSubmittedByStudent(Integer userId) {
        isStudent(userId);
        return submissionRepository.findSubmissionByStudent(userId).stream().map(SubmissionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<SubmissionDto> findCourseSubmissions(Integer courseId) {
        return submissionRepository.findSubmissionsByCourse(courseId).stream().map(SubmissionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void makeSubmissionApproved(SubmissionDto submissionDto, String justification, Submission submission){
        submissionDto.setStatus("APPROVED");
        submissionDto.setJustification(justification);
        submission.setJustification(justification);
        submission.setSubmissionStatus(Submission.Status.APPROVED);

    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void makeSubmissionRejected(SubmissionDto submissionDto,  String justification, Submission submission){
        submissionDto.setStatus("REJECTED");
        submissionDto.setJustification(justification);
        submission.setJustification(justification);
        submission.setSubmissionStatus(Submission.Status.REJECTED);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto makeDecision( boolean isApproved, SubmissionDto submissionDto, Submission submission, String justification) {
        if (isApproved) {
            makeSubmissionApproved(submissionDto, justification, submission);
            return submissionDto;
        }

        else {
            makeSubmissionRejected(submissionDto, justification, submission);
            return submissionDto; }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto teacherEvaluatesQuestion(int userId, int submissionId, boolean isApproved, String justification) {

        isTeacher(userId);

        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));

        isSubmitionOnHold(submission);

        SubmissionDto submissionDto = new SubmissionDto(submission);
        submissionDto.setId(submission.getId());
        submissionDto.setTeacherDecision(isApproved);
        submission.setTeacherDecision(isApproved);

        return makeDecision( isApproved, submissionDto, submission, justification);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto findSubmissionById(Integer submissionId) {
        return submissionRepository.findById(submissionId).map(SubmissionDto::new)
                .orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findSubmissionCourse(Integer submissionId) {
        return submissionRepository.findById(submissionId)
                .map(Submission::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateSubmissionTopics(Integer submissionId, TopicDto[] topics) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));

        if (!submission.getSubmissionStatus().toString().equals("ONHOLD")) throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);

        submission.updateTopics(Arrays.stream(topics).map(topicDto -> topicRepository.findTopicByName(submission.getCourse().getId(), topicDto.getName())).collect(Collectors.toSet()));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void uploadImage(Integer submissionId, String type) {

        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));

        if (!submission.getSubmissionStatus().toString().equals("ONHOLD")) throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);

        Image image = submission.getImage();

        if (image == null) {
            image = new Image();

            submission.setImage(image);
            imageRepository.save(image);
        }

        submission.getImage().setUrl(submission.getKey() + "." + type);

    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto updateSubmission(Integer submissionId, SubmissionDto submissionDto) {

        if (!submissionDto.getStatus().equals("ONHOLD")) throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        submission.update(submissionDto);
        return new SubmissionDto(submission);
    }


    private void isSubmitionOnHold(Submission submission) {
        if(!submission.getSubmissionStatus().toString().equals("ONHOLD")){
            throw new TutorException(SUBMISSION_ALREADY_EVALUATED, submission.getId());
        }
    }



    private void isTeacher(Integer userId) {
        User teacher = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
        if (!teacher.getRole().toString().equals("TEACHER")) {
            throw new TutorException(NOT_TEACHER_ERROR);
        }
    }

    private void isStudent(Integer userId) {
        User teacher = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
        if (!teacher.getRole().toString().equals("STUDENT")) {
            throw new TutorException(NOT_STUDENT_ERROR);
        }
    }

}
