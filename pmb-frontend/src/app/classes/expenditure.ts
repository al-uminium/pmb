import { Expense } from "./expenses";
import { User } from "./user";

export class Expenditure {
  expenditureName!: string;
  users!: User[];
  defaultCurrency!: string;
  inviteToken!: string;
  expenses!: Expense[]

  constructor(expName: string, users: User[], currency: string, invToken: string) {
    this.expenditureName = expName;
    this.users = users;
    this.defaultCurrency = currency;
    this.inviteToken = invToken;
  }

  
  public set setUsers(v : User[]) {
    this.users = v;
  }
  
}
