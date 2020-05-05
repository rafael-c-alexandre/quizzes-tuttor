import Image from '@/models/management/Image';
import Option from '@/models/management/Option';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export default class Submission {
  id: number | null = null;
  userId!: number;
  status: string = 'ONHOLD';
  justification: string = '';
  courseId!: number;
  teacherDecision!: boolean | null;
  madeAvailable!: boolean | null;
  isAvailable: string = '';
  title: string = '';
  content: string = '';
  creationDate!: string | null;
  image: Image | null = null;
  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  topics: Topic[] = [];
  topicNames: string[] = [];
  imageUrl: string = '';

  constructor(jsonObj?: Submission) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userId = jsonObj.userId;
      this.status = jsonObj.status;
      this.justification = jsonObj.justification;
      this.courseId = jsonObj.courseId;
      this.teacherDecision = jsonObj.teacherDecision;
      this.title = jsonObj.title;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.creationDate = ISOtoString(jsonObj.creationDate);
      this.madeAvailable = jsonObj.madeAvailable

      if (jsonObj.madeAvailable)
        this.isAvailable = 'Yes';
      else this.isAvailable = 'No';

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );
      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));

      for (let i = 0; i < this.topics.length; i++)
        this.topicNames[i] = ' ' + this.topics[i].name;

      if (jsonObj.image != null) {
        this.image = new Image(jsonObj.image);
        this.imageUrl = this.image.url;
      }
    }
  }
}
