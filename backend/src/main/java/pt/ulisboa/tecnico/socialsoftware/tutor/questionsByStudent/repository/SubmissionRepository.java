package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.domain.Submission;
import java.util.*;

@Repository
@Transactional
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    @Query(value = "select * from submissions s where s.user_id = :userID", nativeQuery = true)
    List<Submission> findSubmissionByStudent(int userID);

    @Query(value = "SELECT MAX(key) FROM submissions", nativeQuery = true)
    Integer getMaxSubmissionNumber();


}