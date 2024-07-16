import { Expense } from "./expenses";
import { User } from "./user";

export class Expenditure {
  expenditureName!: string;
  users!: User[];
  defaultCurrency!: string;
  inviteToken!: string;
  expenses!: Expense[]

  constructor(expName: string, users: User[], currency: string) {
    this.expenditureName = expName;
    this.users = users;
    this.defaultCurrency = currency;
  }

  
  public set setUsers(v : User[]) {
    this.users = v;
  }
  
}
