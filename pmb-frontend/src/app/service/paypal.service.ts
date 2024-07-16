import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../classes/user';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PaypalService {
  private readonly http = inject(HttpClient)
  private readonly paypalApi = `${environment.apiUrl}/api/paypal`

  getLinkPaypalAccount(): Observable<any> {
    return this.http.get(`${this.paypalApi}/link-account`);
  }

  linkPaypalAccountCallback(code: string, authUser: User): Observable<any> {
    return this.http.post(`${this.paypalApi}/link-account/code=${code}`, authUser)
  }

  createOrder(amount: number, payeeEmail: string, inviteToken: string): Observable<any> {
    return this.http.post(`${this.paypalApi}/create-order/${inviteToken}`, { amount, payeeEmail })
  }

  captureOrder(paymentId: string, payerId: string, token: String): Observable<any> {
    return this.http.post(`${this.paypalApi}/capture-order`, { paymentId, payerId, token })
  }
}
