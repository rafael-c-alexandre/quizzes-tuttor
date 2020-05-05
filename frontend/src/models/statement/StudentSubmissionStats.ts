export default class StudentSubmissionStats {
  totalQuestionSubmitted!: number;
  totalQuestionsOnHold!: number;
  totalQuestionsApproved!: number;
  totalQuestionsRejected!: number;
  totalQuestionsAvailable!: number;

  percentageQuestionsApproved!: number;
  percentageQuestionsRejected!: number;

  constructor(jsonObj?: StudentSubmissionStats) {
    if (jsonObj) {
      this.totalQuestionSubmitted = jsonObj.totalQuestionSubmitted;
      this.totalQuestionsApproved = jsonObj.totalQuestionsApproved;
      this.totalQuestionsRejected = jsonObj.totalQuestionsRejected;
      this.totalQuestionsAvailable = jsonObj.totalQuestionsAvailable;
      this.totalQuestionsOnHold = jsonObj.totalQuestionsOnHold;
      this.percentageQuestionsApproved = jsonObj.percentageQuestionsApproved;
      this.percentageQuestionsRejected = jsonObj.percentageQuestionsRejected;
    }
  }
}
