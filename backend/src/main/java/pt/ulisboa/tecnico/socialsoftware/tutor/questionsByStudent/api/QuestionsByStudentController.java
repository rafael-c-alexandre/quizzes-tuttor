package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;


@RestController
public class QuestionsByStudentController {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionsByStudentService questionsByStudentService;



    @PostMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SubmissionDto createSubmission(@PathVariable int courseId, @Valid @RequestBody SubmissionDto submissionDto) {
        submissionDto.setStatus(Submission.Status.ONHOLD.name());

        return this.questionsByStudentService.studentSubmitQuestion(submissionDto,submissionDto.getUserId());
    }

}
