package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class ListSubmissionsService {

    @Autowired
    private SubmissionRepository submissionRepository;

    //PpA - Feature 3
    public List<SubmissionDto> findQuestionsSubmittedByStudent(int userID) {

        return submissionRepository.findSubmissionByStudent(userID).stream().map(SubmissionDto::new).collect(Collectors.toList());
    }
}
