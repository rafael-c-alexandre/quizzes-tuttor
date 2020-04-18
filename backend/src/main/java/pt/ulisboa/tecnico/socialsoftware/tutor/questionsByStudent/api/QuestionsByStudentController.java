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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.QuestionsByStudentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.dto.SubmissionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

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


@RestController
public class QuestionsByStudentController {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionsByStudentService questionsByStudentService;

    @Autowired
    private QuestionService questionService;

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

        SubmissionDto result = questionsByStudentService.teacherEvaluatesQuestion(user.getId(),submissionDto.getId(), submissionDto.getTeacherDecision(), submissionDto.getJustification());

        questionService.createQuestion(courseId,generateQuestionDto(result));

        return result;
    }

    @PutMapping("/submissions/{submissionId}/topics")
    @PreAuthorize("hasRole('ROLE_STUDENT')  and hasPermission(#submissionId, 'SUBMISSION.ACCESS')")
    public ResponseEntity updateSubmissionTopics(@PathVariable Integer submissionId, @RequestBody TopicDto[] topics) {

        questionsByStudentService.updateSubmissionTopics(submissionId, topics);

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

    private QuestionDto generateQuestionDto(SubmissionDto submissionDto) {
        QuestionDto questionDto = new QuestionDto();
        questionDto.setTitle(submissionDto.getTitle());
        questionDto.setContent(submissionDto.getContent());
        questionDto.setOptions(submissionDto.getOptions());
        questionDto.setImage(submissionDto.getImage());
        questionDto.setTopics(submissionDto.getTopics());
        questionDto.setStatus("AVAILABLE");
       return questionDto;
    }

    @PutMapping("/submissions/{submissionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#submissionId, 'SUBMISSION.ACCESS')")
    public SubmissionDto updateSubmission(@PathVariable Integer submissionId, @Valid @RequestBody SubmissionDto submission) {
        return this.questionsByStudentService.updateSubmission(submissionId, submission);
    }

}
