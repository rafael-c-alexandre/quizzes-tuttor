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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
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

    @Autowired
    private QuestionRepository questionRepository;



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
    public QuestionDto makeQuestionAvailable(Integer courseId, Integer submissionId) {

        courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND,courseId));

        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND,submissionId));

        if (!submission.getSubmissionStatus().name().equals("APPROVED")) throw new TutorException(QUESTION_CANNOT_BE_AVAILABLE);

        Question question = submission.convertToQuestion();
        questionRepository.save(question);



        return new QuestionDto(question);
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
            System.out.println("Prof rejeitou");
            System.out.println("Prof rejeitou");
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
    public void updateSubmissionTopics(Integer submissionId, TopicDto[] topics, User user) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        if(hasPermissionToEdit(submission, user)) {
            submission.updateTopics(Arrays.stream(topics).map(topicDto -> topicRepository.findTopicByName(submission.getCourse().getId(), topicDto.getName())).collect(Collectors.toSet()));
        } else{
            throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);
        }
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
    public SubmissionDto reSubmitSubmission(Integer submissionId, SubmissionDto submissionDto, User user) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        if(user.getRole().equals(User.Role.STUDENT) && (submission.getSubmissionStatus().name().equals("REJECTED"))){
            submission.update(submissionDto);
            submission.setSubmissionStatus(Submission.Status.ONHOLD);
            return new SubmissionDto(submission);
        } else{
            throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);
        }
    }
//fazer testes -> aluno cria submissao, prof rejeita, aluno resubmete
    //jmeter vai ser igual ao

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto updateSubmission(Integer submissionId, SubmissionDto submissionDto, User user) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        if(hasPermissionToEdit(submission, user)) {
            submission.update(submissionDto);
            return new SubmissionDto(submission);
        } else{
            throw new TutorException(SUBMISSION_CANNOT_BE_EDITED);
        }
    }

    private boolean hasPermissionToEdit(Submission submission,  User user){
        if((user.getRole().equals(User.Role.TEACHER) && (submission.getSubmissionStatus().name().equals("APPROVED"))))
            return true;
        else if(user.getRole().equals(User.Role.STUDENT) && (submission.getSubmissionStatus().name().equals("ONHOLD")))
            return true;
        else{
            return false;
        }
    }

    private void isSubmitionOnHold(Submission submission) {
        if(!submission.getSubmissionStatus().toString().equals("ONHOLD")){
            throw new TutorException(SUBMISSION_ALREADY_EVALUATED, submission.getId());
        }
    }



    private boolean isTeacher(Integer userId) {
        User teacher = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
        if (!teacher.getRole().toString().equals("TEACHER")) {
            throw new TutorException(NOT_TEACHER_ERROR);
        }
        return true;
    }

    private boolean isStudent(Integer userId) {
        User teacher = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND,userId));
        if (!teacher.getRole().toString().equals("STUDENT")) {
            throw new TutorException(NOT_STUDENT_ERROR);
        }
        return true;
    }

    private QuestionDto generateQuestionDto(SubmissionDto submissionDto) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(submissionDto.getTitle());
        questionDto.setContent(submissionDto.getContent());
        questionDto.setOptions(submissionDto.getOptions());
        questionDto.setImage(submissionDto.getImage());
        questionDto.setTopics(submissionDto.getTopics());
        questionDto.setStatus("AVAILABLE");;
        return questionDto;
    }

}
