import { User } from './user';

export class Expense {
  expenseName!: string;
  expenseOwner!: User;
  expenseOwnerId!: string;
  totalCost!: number;
  expenseSplit!: { [key: string]: number };
  usersInvolved!: User[];
  exid!: string;
  eid!: string;

  constructor(
    expanseName: string,
    owner: User,
    totalCost: number,
    expenseSplit: { 
      [key: string]: number 
    },
    usersInvolved: User[],
    exid: string,
    eid: string
  ) {
    this.expenseName = expanseName;
    this.expenseOwner = owner;
    this.totalCost = totalCost;
    this.expenseSplit = expenseSplit;
    this.usersInvolved = usersInvolved;
    this.exid = exid;
    this.eid = eid;
  }
}
