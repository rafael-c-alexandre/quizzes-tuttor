package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ListSubmissionsService {

    @Autowired
    private SubmissionRepository submissionRepository;


    public List<SubmissionDto> findQuestionsSubmittedByStudent(UserDto user) {
        isStudent(user);
        return submissionRepository.findSubmissionByStudent(user.getId()).stream().map(SubmissionDto::new).collect(Collectors.toList());
    }

    private void isStudent(UserDto user) {
        if (user == null) throw new TutorException(USER_NOT_FOUND);
        if (!user.getRole().toString().equals("STUDENT")) {
            throw new TutorException(NOT_STUDENT_ERROR);
        }
    }
}
