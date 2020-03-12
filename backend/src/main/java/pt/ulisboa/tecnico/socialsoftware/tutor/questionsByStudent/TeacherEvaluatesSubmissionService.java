package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TeacherEvaluatesSubmissionService {

    @Autowired
    private SubmissionRepository submissionRepository;

    //PpA - Feature 2
    public void makeSubmissionApproved(SubmissionDto submission, String justification){
        submission.setStatus("APPROVED");
        submission.setJustification(justification);
    }
    public void makeSubmissionRejected(SubmissionDto submission,  String justification){
        submission.setStatus("REJECTED");
        submission.setJustification(justification);
    }


    public SubmissionDto teacherEvaluatesQuestion(User user, int submissionId) {
        //user é prof?
        isTeacher(user);
        //avalia uma submissao consoante
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new TutorException(SUBMISSION_NOT_FOUND, submissionId));
        Question question = submission.getQuestion();
        Course course = question.getCourse();
        Set cexec = user.getCourseExecutions();
        SubmissionDto submissionDto = new SubmissionDto(submission);
        return makeDecision(course, cexec, submissionDto);
    }

    private SubmissionDto makeDecision(Course course, Iterable<CourseExecution> cexec, SubmissionDto submissionDto) {
        for (CourseExecution f : cexec) {

            if (f.getCourse().equals(course)) {
                makeSubmissionApproved(submissionDto, "Question well structered and correct");

                return submissionDto;
            }

        }
        makeSubmissionRejected(submissionDto, "Teacher is not assigned to this course");
        return submissionDto;
    }

    private void isTeacher(User user) {
        if (!user.getRole().toString().equals("TEACHER")) {
            throw new TutorException(NOT_TEACHER_ERROR);
        }
    }
}
