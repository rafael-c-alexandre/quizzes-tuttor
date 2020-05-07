export default class StudentSubmissionStats {
  totalQuestionsSubmitted!: number;
  totalQuestionsOnHold!: number;
  totalQuestionsApproved!: number;
  totalQuestionsRejected!: number;
  totalQuestionsAvailable!: number;

  percentageQuestionsApproved!: number;
  percentageQuestionsRejected!: number;

  constructor(jsonObj?: StudentSubmissionStats) {
    if (jsonObj) {
      this.totalQuestionsSubmitted = jsonObj.totalQuestionsSubmitted;
      this.totalQuestionsApproved = jsonObj.totalQuestionsApproved;
      this.totalQuestionsRejected = jsonObj.totalQuestionsRejected;
      this.totalQuestionsAvailable = jsonObj.totalQuestionsAvailable;
      this.totalQuestionsOnHold = jsonObj.totalQuestionsOnHold;
      this.percentageQuestionsApproved = jsonObj.percentageQuestionsApproved;
      this.percentageQuestionsRejected = jsonObj.percentageQuestionsRejected;
    }
  }
}
