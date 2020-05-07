package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class TournamentStatsDto implements Serializable {
    private Integer totalSignedTournaments = 0;
    private Integer totalCreatedTournaments = 0;
    private Integer attendedTournaments = 0;
    private Integer totalCorrectAnswers = 0;
    private Integer answersInTournaments = 0;

    public Integer getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(Integer totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public Integer getTotalSignedTournaments() {
        return totalSignedTournaments;
    }

    public void setTotalSignedTournaments(Integer totalSignedTournaments) {
        this.totalSignedTournaments = totalSignedTournaments;
    }

    public Integer getTotalCreatedTournaments() {
        return totalCreatedTournaments;
    }

    public void setTotalCreatedTournaments(Integer totalCreatedTournaments) {
        this.totalCreatedTournaments = totalCreatedTournaments;
    }

    public Integer getAttendedTournaments() {
        return attendedTournaments;
    }

    public void setAttendedTournaments(Integer attendedTournaments) {
        this.attendedTournaments = attendedTournaments;
    }

    public Integer getAnswersInTournaments() {
        return answersInTournaments;
    }

    public void setAnswersInTournaments(Integer answersInTournaments) {
        this.answersInTournaments = answersInTournaments;
    }

    @Override
    public String toString() {
        return "TournamentStatsDto{" +
                "totalSignedTournaments=" + totalSignedTournaments +
                ", totalCreatedTournaments=" + totalCreatedTournaments +
                ", attendedTournaments=" + attendedTournaments +
                ", totalCorrectAnswers=" + totalCorrectAnswers +
                ", answersInTournaments=" + answersInTournaments +
                '}';
    }
}
