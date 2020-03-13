package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;


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


    //PpA - Feature 1
    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public SubmissionDto studentSubmitQuestion(QuestionDto questionDto, User user) {

        isStudent(user);
        Question question = questionRepository.findById(questionDto.getId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND,questionDto.getId()));

        Submission submission = new Submission(question, user);
        SubmissionDto submissionDto = new SubmissionDto(submission);
        submissionRepository.save(submission);

        submissionDto.setStatus("ONHOLD");
        return submissionDto;
    }

    private void isStudent(User user) {
        if (!user.getRole().toString().equals("STUDENT")) {
            throw new TutorException(NOT_STUDENT_ERROR);
        }
    }


}
