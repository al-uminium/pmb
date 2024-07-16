import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Expenditure } from '../classes/expenditure';
import { Expense } from '../classes/expenses';
import { User } from '../classes/user';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  private readonly http = inject(HttpClient);
  private readonly postUrl = `${environment.apiUrl}/api/post` 
  private readonly getUrl = `${environment.apiUrl}/api/get` 

  createExpenditure(expenditure: Expenditure): Observable<any> {
    return this.http.post<Expenditure>( `${this.postUrl}/initializeexpenditure`, expenditure)
  }

  createExpense(expense: Expense, path: string): Observable<any> {
    return this.http.post<Expense>(`${this.postUrl}/expense/${path}`, expense)
  }

  createUser(user: User): Observable<User> {
    return this.http.post<User>(`${this.postUrl}/register`, user)
  }

  getExpenditureDetails(inviteToken: string): Observable<Expenditure> {
    return this.http.get<Expenditure>(`${this.getUrl}/expenditure/${inviteToken}`)
  }

  getExpenditureForUser(user: User): Observable<Expenditure[]> {
    return this.http.get<Expenditure[]>(`${this.getUrl}/user/${user.userId}`)
  }

  getExpensesForOwner(inviteToken: string, ownerId: string): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.getUrl}/expenditure/expenses/user/${ownerId}&${inviteToken}`)
  }

  getExpensesWhereUserOwes(inviteToken: string, uid: string): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.getUrl}/expenditure/expenses/user/${uid}&${inviteToken}&owes`)
  }

  getPaymentsToSettle(inviteToken: string): Observable<{ [username: string]: {[name: string] : number } }> {
    return this.http.get<{ [username: string]: {[name: string] : number } }>(`${this.getUrl}/expenditure/${inviteToken}/balance/settlements`)
  }

  getUsersOfExpenditure(inviteToken: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.getUrl}/expenditure/users/${inviteToken}`)
  }

  getBalanceOfExpenditure(inviteToken: string): Observable<User[]> {
    return this.http.get<User[]>(`${this.getUrl}/expenditure/${inviteToken}/balance`)
  }

  updateLinkedUser(loginUser: User, selectedUser: User): Observable<any> {
    return this.http.post(`${this.postUrl}/link-account`, [loginUser, selectedUser])
  }

  postLogin(user: User): Observable<User> {
    return this.http.post<User>(`${this.postUrl}/login`, user);
  }
}
