package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository.SubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;


import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class QuestionsByStudentService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Autowired
    private UserRepository userRepository;



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SubmissionDto studentSubmitQuestion(SubmissionDto submissionDto, int userId) {

        isStudent(userId);
        Question question = questionRepository.findById(submissionDto.getQuestionId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND,submissionDto.getQuestionId()));
        User student = userRepository.findById(submissionDto.getUserId()).orElseThrow(() -> new TutorException(USER_NOT_FOUND,submissionDto.getUserId()));

        Submission submission = new Submission(question, student);
        student.addSubmission(submission);
        question.getCourse().addSubmission(submission);
        submissionRepository.save(submission);
        SubmissionDto submissionResult = new SubmissionDto(submission);

        return submissionResult;
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
    public void makeSubmissionApproved(SubmissionDto submissionDto, String justification, Submission submission){
        submissionDto.setStatus("APPROVED");
        submissionDto.setJustification(justification);
        submission.setJustification(justification);
        submission.setStatus(Submission.Status.APPROVED);
        submission.getQuestion().setStatus(Question.Status.DISABLED); //becomes disabled before teacher makes it available to the people

    }
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void makeSubmissionRejected(SubmissionDto submissionDto,  String justification, Submission submission){
        submissionDto.setStatus("REJECTED");
        submissionDto.setJustification(justification);
        submission.setJustification(justification);
        submission.setStatus(Submission.Status.REJECTED);
        submission.getQuestion().setStatus(Question.Status.REMOVED);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto makeDecision( boolean isApproved, SubmissionDto submissionDto, Submission submission) {
        if (isApproved) {
            makeSubmissionApproved(submissionDto, "Question well structured and correct", submission);
            return submissionDto;
        }

        else {
            makeSubmissionRejected(submissionDto, "Question is not correct", submission);
            return submissionDto; }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)

    public SubmissionDto teacherEvaluatesQuestion(int userId, int submissionId, boolean isApproved) {

        isTeacher(userId);

        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));

        isSubmitionOnHold(submission);


        SubmissionDto submissionDto = new SubmissionDto(submission);
        submissionDto.setId(submission.getId());
        submissionDto.setTeacherDecision(isApproved);
        submission.setTeacherDecision(isApproved);

        return makeDecision( isApproved, submissionDto, submission);
    }

    private void isSubmitionOnHold(Submission submission) {
        if(!submission.getStatus().toString().equals("ONHOLD")){
            throw new TutorException(SUBMITION_ALREADY_EVALUATED, submission.getId());
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
