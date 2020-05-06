export default class UserTournamentStats {
  totalSignedTournaments!: number;
  totalCreatedTournaments!: number;
  attendedTournaments!: number;
  totalCorrectAnswers!: number;
  answersInTournaments!: number;

  constructor(jsonObj?: UserTournamentStats) {
    if (jsonObj) {
      console.log(jsonObj);
      this.totalSignedTournaments = jsonObj.totalSignedTournaments;
      this.totalCreatedTournaments = jsonObj.totalCreatedTournaments;
      this.attendedTournaments = jsonObj.attendedTournaments;
      this.totalCorrectAnswers = jsonObj.totalCorrectAnswers;
      this.answersInTournaments = jsonObj.answersInTournaments;
      console.log(this);
    }
  }
}
