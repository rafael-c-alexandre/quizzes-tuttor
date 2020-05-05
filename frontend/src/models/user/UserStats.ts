export default class UserStats {
  id!: number;
  name!: string;
  username!: string;
  role!: string;
  coursesNumber: number = 0;

  constructor(jsonObj?: UserStats) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.name = jsonObj.name;
      this.username = jsonObj.username;
      this.role = jsonObj.role;
    }
  }
}
