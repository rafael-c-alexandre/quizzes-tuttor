import User from '@/models/user/User';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';

export default class Tournament {
  id!: number;
  title!: string;
  creationDate!: string | undefined;
  availableDate!: string | undefined;
  conclusionDate!: string | undefined;
  tournamentCreator!: User;
  numberOfSignedUsers!: number;
  numberOfTopics!: number;
  numberOfQuestions!: number;

  questions: Question[] = [];
  topics: Topic[] = [];
  signedUsers: User[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.creationDate = jsonObj.creationDate;
      this.availableDate = jsonObj.availableDate;
      this.conclusionDate = jsonObj.conclusionDate;
      this.tournamentCreator = new User(jsonObj.tournamentCreator);
      this.numberOfSignedUsers = jsonObj.numberOfSignedUsers;
      this.numberOfTopics = jsonObj.numberOfTopics;
      this.numberOfQuestions = jsonObj.numberOfQuestions;

      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }
      if (jsonObj.signedUsers) {
        this.signedUsers = jsonObj.signedUsers.map(
          (user: User) => new User(user)
        );
      }
    }
  }
}
