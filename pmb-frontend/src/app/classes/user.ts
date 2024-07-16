export class User {
  userName!: string;
  userId!: string; 
  balance!: number;
  pw!: string;
  email!: string;
  paypalEmail!: string; 

  constructor(userName: string) {
    this.userName = userName;
  }
  
  // set userId(userid : string) {
  //   this.userId = userid;
  // }

  // set balance(number: number) {
  //   this.balance = number;
  // }
  
}