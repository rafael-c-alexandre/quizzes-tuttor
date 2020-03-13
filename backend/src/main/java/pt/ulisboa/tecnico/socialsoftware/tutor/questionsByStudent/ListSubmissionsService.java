package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ListSubmissionsService {

    @Autowired
    private SubmissionRepository submissionRepository;


    public List<SubmissionDto> findQuestionsSubmittedByStudent(User user) {
        isStudent(user);
        return submissionRepository.findSubmissionByStudent(user.getId()).stream().map(SubmissionDto::new).collect(Collectors.toList());
    }

    private void isStudent(User user) {
        if (!user.getRole().toString().equals("STUDENT")) {
            throw new TutorException(NOT_STUDENT_ERROR);
        }
    }
}
