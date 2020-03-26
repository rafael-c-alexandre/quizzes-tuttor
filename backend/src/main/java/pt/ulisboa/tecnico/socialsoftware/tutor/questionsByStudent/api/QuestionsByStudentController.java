package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;


import javax.validation.Valid;

import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;



@RestController
public class QuestionsByStudentController {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionsByStudentService questionsByStudentService;



    @GetMapping("/users/{userId}/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<SubmissionDto> getStudentSubmissions(@PathVariable int userId ) {
        return questionsByStudentService.findQuestionsSubmittedByStudent(userId);
    }



    @PostMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SubmissionDto createSubmission( @PathVariable int courseId, @Valid @RequestBody SubmissionDto submissionDto) {
        submissionDto.setStatus(Submission.Status.ONHOLD.name());
        return this.questionsByStudentService.studentSubmitQuestion(submissionDto,submissionDto.getUserId());
    }

    @PutMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SubmissionDto evaluateQuestion(Principal principal, @Valid @RequestBody SubmissionDto submissionDto, @PathVariable int courseId) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return this.questionsByStudentService.teacherEvaluatesQuestion(user.getId(),submissionDto.getId(), submissionDto.getTeacherDecision());

    }
}
