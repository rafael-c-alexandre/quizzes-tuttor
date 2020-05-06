export default class UserTournamentStats {
  numberOfSignedTournaments!: number;
  numberOfCreatedTournaments!: number;
  numberOfAtendedTournaments!: number;
  averageScore!: number;
  numberOfTotalAnswers!: number;
  numberOfUniqueCorrectAnswers!: number;

  constructor(jsonObj?: UserTournamentStats) {
    if (jsonObj) {
      this.numberOfSignedTournaments = jsonObj.numberOfSignedTournaments;
      this.numberOfCreatedTournaments = jsonObj.numberOfCreatedTournaments;
      this.numberOfAtendedTournaments = jsonObj.numberOfAtendedTournaments;
      this.averageScore = jsonObj.averageScore;
      this.numberOfTotalAnswers = jsonObj.numberOfTotalAnswers;
      this.numberOfUniqueCorrectAnswers = jsonObj.numberOfUniqueCorrectAnswers;
    }
  }
}
