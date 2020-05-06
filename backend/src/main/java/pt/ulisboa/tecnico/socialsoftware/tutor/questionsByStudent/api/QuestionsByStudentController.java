package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SUBMISSION_NOT_FOUND;


@RestController
public class QuestionsByStudentController {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionsByStudentService questionsByStudentService;

    @Autowired
    private UserService userService;

    @Value("${figures.dir}")
    private String figuresDir;



    @GetMapping("/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<SubmissionDto> getStudentSubmissions(Principal principal ) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return questionsByStudentService.findQuestionsSubmittedByStudent(user.getId());
    }

    @GetMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<SubmissionDto> getCourseSubmissions(@PathVariable int courseId,Principal principal ) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return questionsByStudentService.findCourseSubmissions(courseId);
    }

    @PutMapping("/courses/{courseId}/submissions/{submissionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionDto makeQuestionAvailable(Principal principal, @PathVariable int courseId, @PathVariable int submissionId ) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return questionsByStudentService.makeQuestionAvailable(courseId, submissionId);
    }





    @PostMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SubmissionDto createSubmission( Principal principal, @PathVariable int courseId, @Valid @RequestBody SubmissionDto submissionDto) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        submissionDto.setStatus(Submission.Status.ONHOLD.name());

        return this.questionsByStudentService.studentSubmitQuestion(submissionDto,user.getId(),courseId);
    }

    @PutMapping("/courses/{courseId}/submissions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public SubmissionDto evaluateQuestion(Principal principal, @Valid @RequestBody SubmissionDto submissionDto, @PathVariable int courseId) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return questionsByStudentService.teacherEvaluatesQuestion(user.getId(),submissionDto, submissionDto.getTeacherDecision(), submissionDto.getJustification());

    }

    @PutMapping("/submissions/{submissionId}/topics")
    @PreAuthorize("hasRole('ROLE_STUDENT')  and hasPermission(#submissionId, 'SUBMISSION.ACCESS') or hasRole('ROLE_TEACHER')")
    public ResponseEntity updateSubmissionTopics(Principal principal, @PathVariable Integer submissionId, @RequestBody TopicDto[] topics) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        questionsByStudentService.updateSubmissionTopics(submissionId, topics, user);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/submissions/{submissionId}/image")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#submissionId, 'SUBMISSION.ACCESS')")
    public String uploadImage(@PathVariable Integer submissionId, @RequestParam("file") MultipartFile file) throws IOException {


        SubmissionDto submissionDto = questionsByStudentService.findSubmissionById(submissionId);
        String url = submissionDto.getImage() != null ? submissionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        questionsByStudentService.uploadImage(submissionId, type);

        url = questionsByStudentService.findSubmissionById(submissionId).getImage().getUrl();

        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }


    @PutMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') ")
    public SubmissionDto updateSubmission(Principal principal, @PathVariable Integer submissionId, @Valid @RequestBody SubmissionDto submission) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return this.questionsByStudentService.updateSubmission(submissionId, submission, user);
    }

    @PutMapping("/submissions/rejected/{submissionId}")
    @PreAuthorize("(hasRole('ROLE_STUDENT') and hasPermission(#submissionId, 'SUBMISSION.ACCESS'))")
    public SubmissionDto reSubmitSubmission(Principal principal, @PathVariable Integer submissionId, @Valid @RequestBody SubmissionDto submission) {

        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return this.questionsByStudentService.reSubmitSubmission(submissionId, submission, user);
    }

    @GetMapping("/users/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<UserDto> getPublicDashboardUsers(Principal principal){
        User user = (User)((Authentication) principal).getPrincipal();

        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);
        return userService.getPublicDashboardUsers();
    }

    @PutMapping("users/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity changeUserDashboardPrivacy(Principal principal){

        User user = (User)((Authentication) principal).getPrincipal();


        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        userService.changeUserDashboardPrivacy(user.getId());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public UserDto getUserPrivacyStatus(Principal principal){

        User user = (User)((Authentication) principal).getPrincipal();


        if(user == null) throw new TutorException(AUTHENTICATION_ERROR);

        return userService.getPrivacyStatus(user.getId());


    }

}
