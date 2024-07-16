import { Injectable } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { User } from '../classes/user';

@Injectable({
  providedIn: 'root'
})
export class UtilService {

  generateSecureRandomString(length: number) {
    let result = '';
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    const charactersLength = characters.length;
    const randomValues = new Uint32Array(length);
    window.crypto.getRandomValues(randomValues);
    for (let i = 0; i < length; i++) {
      result += characters.charAt(randomValues[i] % charactersLength);
    }
    return result;
  }

  getExpenseSplitFromFormGroup(arr: FormGroup[], users: User[]): { [key: string]: number } {
    const expenseSplit = {} as { [key: string]: number };
    for(let i = 0; i < arr.length; i++) {
      const checkBoxVal = arr[i].get('checked')?.value;
      if (checkBoxVal) {
        const user = users[i];
        const costVal = arr[i].get('cost')?.value as number;
        expenseSplit[user.userName] = costVal;
      }
    }
    return expenseSplit;
  }

  checkIfExpenseSplitIsValid(arr: FormGroup[]): boolean {
    let isValid = true;
    arr.forEach(control => {
      const val = control.get('cost')?.value
      if ((typeof val == 'object')){
        isValid = false;
      }      
    })
    return isValid;
  }

  // https://stackoverflow.com/questions/11832914/how-to-round-to-at-most-2-decimal-places-if-necessary
  // decided to pass it to backend to round off instead
  getExpenseSplitIfSplitEven(total: number, arr: FormGroup[], users: User[]) {
    const expenseSplit = {} as { [key: string]: number };
    const usersInvolved = this.getUsersInvolved(arr, users);
    const splitAmt = (total / usersInvolved.length);

    usersInvolved.forEach(user => {
      expenseSplit[user.userName] = splitAmt;
    });
    return expenseSplit;
  }

  getUsersInvolved(arr: FormGroup[], users: User[]): User[] {
    const usersInvolved = new Array<User>;

    for (let i = 0; i < arr.length; i++) {
      const checkBoxVal = arr[i].get('checked')?.value;
      if (checkBoxVal) {
        usersInvolved.push(users[i]);
      }
    }

    return usersInvolved;
  }
  
  getUsersFromForm(arr: FormArray): User[] {
    const userArray = new Array<User>; 

    for (let index = 0; index < arr.length; index++) {
      const val = arr.at(index).value as string
      if (val.length > 0) {
        const newUser = new User(val);
        userArray.push(newUser);
      }
    }
    return userArray
  }  

  getUsersFromBackend(obj: Object) {
    
  }
  
  getUserIdFromLocalStorage(): string {
    const storedUser = localStorage.getItem('selectedUser');
    if (storedUser) {
      const userJson = JSON.parse(storedUser);
      return userJson.user.userId;
    } else {
      return '';
    }
  }

  getAuthUserFromLocalStorage(): User {
    const storedUser = localStorage.getItem('authUser');
    const user = new User('');
    if (storedUser) {
      const userJson = JSON.parse(storedUser);
      user.userName = userJson.user.userName;
      user.userId = userJson.user.userId;
      return user
    }
    return user
  }
}
