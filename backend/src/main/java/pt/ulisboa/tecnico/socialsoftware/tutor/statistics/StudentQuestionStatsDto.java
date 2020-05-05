package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StudentQuestionStatsDto implements Serializable {
    private Integer totalQuestionsSubmitted = 0;
    private Integer totalQuestionsApproved = 0;
    private Integer totalQuestionOnHold = 0;
    private Integer totalQuestionRejected = 0;
    private Integer totalQuestionsAvailable = 0;
    private float percentageQuestionsApproved = 0;
    private float percentageQuestionsRejected = 0;

    public Integer getTotalQuestionsSubmitted() {
        return totalQuestionsSubmitted;
    }

    public void setTotalQuestionsSubmitted(Integer totalQuestionsSubmitted) {
        this.totalQuestionsSubmitted = totalQuestionsSubmitted;
    }

    public Integer getTotalQuestionsApproved() {
        return totalQuestionsApproved;
    }

    public void setTotalQuestionsApproved(Integer totalQuestionsApproved) {
        this.totalQuestionsApproved = totalQuestionsApproved;
    }

    public Integer getTotalQuestionOnHold() {
        return totalQuestionOnHold;
    }

    public void setTotalQuestionOnHold(Integer totalQuestionOnHold) {
        this.totalQuestionOnHold = totalQuestionOnHold;
    }

    public Integer getTotalQuestionRejected() {
        return totalQuestionRejected;
    }

    public void setTotalQuestionRejected(Integer totalQuestionRejected) {
        this.totalQuestionRejected = totalQuestionRejected;
    }

    public Integer getTotalQuestionsAvailable() {
        return totalQuestionsAvailable;
    }

    public void setTotalQuestionsAvailable(Integer totalQuestionsAvailable) {
        this.totalQuestionsAvailable = totalQuestionsAvailable;
    }

    public float getPercentageQuestionsApproved() {
        return percentageQuestionsApproved;
    }

    public void setPercentageQuestionsApproved(float percentageQuestionsApproved) {
        this.percentageQuestionsApproved = percentageQuestionsApproved;
    }

    public float getPercentageQuestionsRejected() {
        return percentageQuestionsRejected;
    }

    public void setPercentageQuestionsRejected(float percentageQuestionsRejected) {
        this.percentageQuestionsRejected = percentageQuestionsRejected;
    }

    @Override
    public String toString() {
        return "StudentQuestionStatsDto{" +
                "totalQuestionsSubmitted=" + totalQuestionsSubmitted +
                ", totalQuestionsApproved=" + totalQuestionsApproved +
                ", totalQuestionOnHold=" + totalQuestionOnHold +
                ", totalQuestionRejected=" + totalQuestionRejected +
                ", totalQuestionsAvailable=" + totalQuestionsAvailable +
                ", percentageQuestionsApproved=" + percentageQuestionsApproved +
                ", percentageQuestionsRejected=" + percentageQuestionsRejected +
                '}';
    }
}



