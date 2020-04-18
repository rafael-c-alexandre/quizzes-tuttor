import User from '@/models/user/User';
import Question from '@/models/management/Question';
import Topic from '@/models/management/Topic';

export default class Tournament {
  id!: number;
  title!: string;
  creationDate!: string | undefined;
  availableDate!: string | undefined;
  conclusionDate!: string | undefined;
  numberOfSignedUsers!: number;
  numberOfTopics!: number;
  numberOfQuestions!: number;

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
    }
  }
}
