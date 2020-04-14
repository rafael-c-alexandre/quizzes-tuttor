package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.util.List;


@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    @Query(value = "SELECT * FROM tournaments t", nativeQuery = true)
    List<Tournament> findTournaments();

    @Query(value = "select * from tournaments where available_date < now() and conclusion_date > now()",nativeQuery = true)
    List<Tournament> findOpenTournaments();

    @Query(value = "SELECT * FROM tournaments t WHERE t.id = :id",nativeQuery = true)
    Tournament findTournamentById(Integer id);

    @Query(value = "SELECT MAX(id) FROM tournaments", nativeQuery = true)
    Integer getMaxTournamentId();

}
