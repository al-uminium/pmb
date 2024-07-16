import { Component, OnInit, inject } from '@angular/core';
import { BackendService } from '../../../service/backend.service';
import { User } from '../../../classes/user';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Expense } from '../../../classes/expenses';
import { Observable } from 'rxjs';
import { Expenditure } from '../../../classes/expenditure';
import { CommonModule } from '@angular/common';
import { select, Store, StoreModule } from '@ngrx/store';
import { selectSelectedUser } from '../../../state/user.selectors';
import { ProfilePictureComponent } from '../../utility/profile-picture/profile-picture.component';
import { PaypalPayComponent } from '../../paypal/paypal-pay/paypal-pay.component';
import { PaypalService } from '../../../service/paypal.service';

@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [RouterModule, CommonModule, StoreModule, ProfilePictureComponent, PaypalPayComponent],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.css',
})
export class BalanceComponent implements OnInit {
  private readonly bkSvc = inject(BackendService);
  private readonly actRoute = inject(ActivatedRoute);
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  private readonly paypalSvc = inject(PaypalService)

  selectedUser$!: Observable<User | null>;
  expenditure$!: Observable<Expenditure>;
  usersBalance$!: Observable<User[]>;
  settlements$!: Observable<{ [username: string]: { [name: string]: number } }>;

  inviteToken!: string;
  paymentsToSettle!: { [username: string]: { [name: string]: number } };
  hasSettlements!: boolean;

  paymentId!: string;
  token!: string;
  payerId!: string;

  ngOnInit(): void {
    this.actRoute.paramMap.subscribe((param) => {
      this.inviteToken = param.get('id') as string;
      this.expenditure$ = this.bkSvc.getExpenditureDetails(this.inviteToken);
      this.settlements$ = this.bkSvc.getPaymentsToSettle(this.inviteToken);
      this.selectedUser$ = this.store.pipe(select(selectSelectedUser));
    });

    this.actRoute.queryParams.subscribe((query) => {
      this.paymentId = query['paymentId'] as string;
      this.token = query['token'] as string;
      this.payerId = query['PayerID'] as string;
      if (this.paymentId && this.payerId && this.token) {
        this.paypalSvc.captureOrder(this.paymentId, this.payerId, this.token).subscribe((response: any) => {
          if (response.status === 'approved') {
            alert(`Payment Successful!`);
          } else {
            alert('Payment failed :(');
          }
        })
      }
    })
  }

  handleRepay(selectedUser: User, user: User, amount: number) {
    const expenseName = `Repayment: ${selectedUser.userName} to ${user.userName} for ${amount}`;
    const expenseSplit = {} as { [key: string]: number };
    expenseSplit[selectedUser.userName] = amount;
    expenseSplit[user.userName] = amount;
    const usersInvolved = [user];
    const expense = new Expense(
      expenseName,
      selectedUser,
      amount,
      expenseSplit,
      usersInvolved,
      '',
      ''
    );
    console.log(expense);
    this.bkSvc.createExpense(expense, this.inviteToken).subscribe(() => {
      this.expenditure$ = this.bkSvc.getExpenditureDetails(this.inviteToken);
      this.settlements$ = this.bkSvc.getPaymentsToSettle(this.inviteToken);
    });
  }

  handleRouteToExpense() {
    this.router.navigate([`/expenditure/${this.inviteToken}`]);
  }

  checkIfAllBalanceNeutral(users: User[]): boolean {
    let totalBalance = 0;
    users.forEach((user) => {;
      totalBalance += Math.abs(user.balance);
    });
    // to deal with 0.01 edge cases 
    totalBalance = Math.round(totalBalance);
    return totalBalance == 0;
  }
}
