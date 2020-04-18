import User from '@/models/user/User';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';

export default class Tournament {
  id!: number;
  title!: string;
  creationDate!: string;
  availableDate!: string;
  conclusionDate!: string;
  numberOfSignedUsers!: number;
  numberOfTopics!: number;
  numberOfQuestions!: number;
  state!: string;
  questions: Question[] = [];
  topics: Topic[] = [];
  signedUsers: number[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.creationDate = jsonObj.creationDate;
      this.availableDate = jsonObj.availableDate;
      this.conclusionDate = jsonObj.conclusionDate;
      this.numberOfSignedUsers = jsonObj.numberOfSignedUsers;
      this.numberOfTopics = jsonObj.numberOfTopics;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.signedUsers = jsonObj.signedUsers;
      if (jsonObj.questions) {
        this.questions = jsonObj.questions.map(
          (question: Question) => new Question(question)
        );
      }
      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }
      let a = new Date(this.availableDate);
      let c = new Date(this.conclusionDate);
      let n = new Date();

      if (n < a) this.state = 'Signable';
      else if (n < c) this.state = 'Open';
      else this.state = 'Closed';
    }
  }
}
