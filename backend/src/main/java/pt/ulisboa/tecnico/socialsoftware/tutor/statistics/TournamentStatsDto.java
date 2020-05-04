package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class TournamentStatsDto implements Serializable {
    private Integer totalSignedTournaments = 0;
    private Integer totalCreatedTournaments = 0;
    private Integer attendededTournaments = 0;
    private float averageScore = 0;
    private Integer uniqueCorrectAnswersInTournaments = 0;
    private Integer answersInTournaments = 0;

    public Integer getSignedTournaments() {
        return totalSignedTournaments;
    }
    public void setTotalSignedTournaments(Integer tournamentCount) {
        this.totalSignedTournaments = tournamentCount;
    }

    public Integer getCreatedTournaments() {
        return totalCreatedTournaments;
    }
    public void setTotalCreatedTournaments(Integer tournamentCount) {
        this.totalCreatedTournaments = tournamentCount;
    }

    public Integer getAttendededTournaments() {
        return attendededTournaments;
    }
    public void setAttendededTournaments(Integer tournamentCount) {
        this.attendededTournaments = tournamentCount;
    }

    public float getAverageScore() { return attendededTournaments; }
    public void setAverageScore(float averageScore) {
        this.averageScore = averageScore;
    }

    public Integer getUniqueCorrectAnswersInTournaments() {
        return uniqueCorrectAnswersInTournaments;
    }
    public void setUniqueCorrectAnswers(Integer answersCount) {
        this.uniqueCorrectAnswersInTournaments = answersCount;
    }

    public Integer getAnswersInTournaments() {
        return answersInTournaments;
    }
    public void setAnswersInTournaments(Integer answersCount) {
        this.answersInTournaments = answersCount;
    }

    public String toString() {
        return "TournamentStatsDto{" +
                "totalSignedTournaments=" + totalSignedTournaments +
                ", totalCreatedTournaments=" + totalCreatedTournaments +
                ", attendededTournaments=" + attendededTournaments +
                ", percentageWonTournaments=" + averageScore +
                ", uniqueCorrectAnswersInTournaments=" + uniqueCorrectAnswersInTournaments +
                ", answersInTournaments=" + answersInTournaments +
                '}';
    }
}
