package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StudentQuestionStatsDto implements Serializable {
    private Integer totalQuestionsSubmitted = 0;
    private Integer totalQuestionsApproved = 0;
    private Integer totalQuestionsOnHold = 0;
    private Integer totalQuestionsRejected = 0;
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

    public Integer getTotalQuestionsOnHold() {
        return totalQuestionsOnHold;
    }

    public void setTotalQuestionsOnHold(Integer totalQuestionsOnHold) {
        this.totalQuestionsOnHold = totalQuestionsOnHold;
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

    public Integer getTotalQuestionsRejected() {
        return totalQuestionsRejected;
    }

    public void setTotalQuestionsRejected(Integer totalQuestionsRejected) {
        this.totalQuestionsRejected = totalQuestionsRejected;
    }

    @Override
    public String toString() {
        return "StudentQuestionStatsDto{" +
                "totalQuestionsSubmitted=" + totalQuestionsSubmitted +
                ", totalQuestionsApproved=" + totalQuestionsApproved +
                ", totalQuestionsOnHold=" + totalQuestionsOnHold +
                ", totalQuestionsRejected=" + totalQuestionsRejected +
                ", totalQuestionsAvailable=" + totalQuestionsAvailable +
                ", percentageQuestionsApproved=" + percentageQuestionsApproved +
                ", percentageQuestionsRejected=" + percentageQuestionsRejected +
                '}';
    }
}



