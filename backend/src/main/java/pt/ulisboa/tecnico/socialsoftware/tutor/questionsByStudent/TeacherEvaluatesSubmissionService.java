package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import java.sql.SQLException;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TeacherEvaluatesSubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void makeSubmissionApproved(SubmissionDto submissionDto, String justification, Submission submission){
        submissionDto.setStatus("APPROVED");
        submissionDto.setJustification(justification);
        submission.setJustification(justification);
        submission.setStatus(Submission.Status.APPROVED);
        Question question = submission.getQuestion();
        questionRepository.save(question);

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
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto teacherEvaluatesQuestion(User user, int submissionId) {
        //due to the lack of information provided, we decided that the approval/rejection
        //of the question by the teacher comes down to whether the teacher belongs to the question's course or not
        isTeacher(user);

        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        Question question = submission.getQuestion();
        Course course = question.getCourse();
        Set cexec = user.getCourseExecutions();
        SubmissionDto submissionDto = new SubmissionDto(submission);
        return makeDecision(course, cexec, submissionDto, submission);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto makeDecision(Course course, Iterable<CourseExecution> cexec, SubmissionDto submissionDto, Submission submission) {
        for (CourseExecution f : cexec) {

            if (f.getCourse().equals(course)) {
                makeSubmissionApproved(submissionDto, "Question well structered and correct", submission);

                return submissionDto;
            }

        }
        makeSubmissionRejected(submissionDto, "Teacher is not assigned to this course", submission);
        return submissionDto;
    }

    private void isTeacher(User user) {
        if (!user.getRole().toString().equals("TEACHER")) {
            throw new TutorException(NOT_TEACHER_ERROR);
        }
    }
}
