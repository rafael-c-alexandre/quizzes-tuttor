package pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsByStudent.Submission;
import java.util.List;

@Repository
@Transactional
public interface SubmissionRepository extends JpaRepository<Submission, Integer> {
    @Query(value = "select * from submissions u where u.user.getId() = :userID", nativeQuery = true)
    List<Submission> findSubmissionByStudent(int userID);

}