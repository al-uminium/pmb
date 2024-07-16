import { Component, Input, OnInit } from '@angular/core';
import { Expense } from '../../../classes/expenses';
import { ProfilePictureComponent } from '../../utility/profile-picture/profile-picture.component';
import { User } from '../../../classes/user';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroChevronDoubleRight } from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-expense-table',
  standalone: true,
  imports: [ProfilePictureComponent, NgIconComponent],
  providers: [provideIcons({ heroChevronDoubleRight })],
  templateUrl: './expense-table.component.html',
  styleUrl: './expense-table.component.css'
})
export class ExpenseTableComponent{
  @Input()
  expenses!: Expense[]; 

  @Input()
  currency!: string;

  @Input()
  user!: User;

  usersInvolvedWoOwner!: User[]

  generateExpenseSummary(exp: Expense): string {
    const usersInvolvedExcludeOwner = [] as User[]
    exp.usersInvolved.forEach(user => {
      if (!(user.userId == exp.expenseOwner.userId)) {
        usersInvolvedExcludeOwner.push(user);
      }
    });
    let text = `${exp.expenseOwner.userName} paid for `
    for (let i = 0; i < usersInvolvedExcludeOwner.length; i++) {
      const userName = usersInvolvedExcludeOwner[i].userName;
      if ((i === 0)) {
        text = text + ` ${userName}`
      } else {
        text = text + `, ${userName}`
      }
    }
    return text;
  }
}
