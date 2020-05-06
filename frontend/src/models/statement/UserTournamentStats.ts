export default class UserTournamentStats {
  totalSignedTournaments!: number;
  totalCreatedTournaments!: number;
  attendedTournaments!: number;
  uniqueCorrectAnswersInTournaments!: number;
  answersInTournaments!: number;

  constructor(jsonObj?: UserTournamentStats) {
    if (jsonObj) {
      console.log(jsonObj);
      this.totalSignedTournaments = jsonObj.totalSignedTournaments;
      this.totalCreatedTournaments = jsonObj.totalCreatedTournaments;
      this.attendedTournaments = jsonObj.attendedTournaments;
      this.uniqueCorrectAnswersInTournaments =
        jsonObj.uniqueCorrectAnswersInTournaments;
      this.answersInTournaments = jsonObj.answersInTournaments;
      console.log(this);
    }
  }
}
