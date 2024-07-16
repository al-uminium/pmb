import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BackendService } from '../../../service/backend.service';
import { Expenditure } from '../../../classes/expenditure';
import { User } from '../../../classes/user';
import { ExpenseTableComponent } from '../expense-table/expense-table.component';
import { SelectUserModalComponent } from '../select-user-modal/select-user-modal.component';
import { Expense } from '../../../classes/expenses';
import { ExpenseModalComponent } from '../expense-modal/expense-modal.component';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroLink, heroPlus, heroShare, heroUsers } from '@ng-icons/heroicons/outline';
import { select, Store, StoreModule } from '@ngrx/store';
import { loginUser, selectUser } from '../../../state/user.actions';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { selectAuthUser, selectSelectedUser } from '../../../state/user.selectors';
import { UtilService } from '../../../service/util.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, 
    ExpenseTableComponent, 
    SelectUserModalComponent, 
    ExpenseModalComponent, 
    NgIconComponent,
    StoreModule
  ],
  providers: [
    provideIcons({ heroUsers, heroPlus, heroLink })
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly actRoute = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly bkSvc = inject(BackendService);
  private readonly store = inject(Store);
  private readonly util = inject(UtilService)

  expenditure$!: Observable<Expenditure>;
  selectedUser$!: Observable<User | null>;
  authUser$!: Observable<User | null>;

  inviteToken!: string;
  showUserModal: boolean = true;
  showExpenseModal: boolean = false;
  showLinkModal: boolean = false;
  selectedUserExpense!: Expense[]
  selectedUserOwedExpense!: Expense[]

  @Input()
  createdExpense!: Expense

  ngOnInit(): void {
    this.actRoute.paramMap.subscribe(params => {
      this.inviteToken = params.get('id') as string
      this.expenditure$ = this.bkSvc.getExpenditureDetails(this.inviteToken);
      this.expenditure$.subscribe(expenditure => {
        this.onPageLoad(expenditure);
        this.selectedUser$ = this.store.pipe(select(selectSelectedUser));
        this.authUser$ = this.store.pipe(select(selectAuthUser));
      })
    });
  }

  toggleUserModal(): void {
    this.showUserModal = !this.showUserModal;
  }

  toggleExpenseModal(): void {
    this.showExpenseModal = !this.showExpenseModal;
  }

  toggleLinkModal(): void {
    this.showLinkModal = !this.showLinkModal;
  }

  generateLinkModalText(user: User) : string {
    return `You are currently logged in as ${user?.userName}. Who do you want to link as?`
  }

  selectUser(user: User): void {
    this.store.dispatch(selectUser({ user: user }));
    localStorage.setItem('selectedUser', JSON.stringify({ user }));
    this.toggleUserModal();
  }

  linkUser(selectedUser: User, loginUser: User): void {
    this.bkSvc.updateLinkedUser(loginUser, selectedUser).subscribe(() => {
      console.log("Linking user...");
    });
    this.toggleLinkModal()
  }

  createExpense(expense: Expense) {
    this.bkSvc.createExpense(expense, this.inviteToken).subscribe({
      next: () => {
        this.expenditure$ = this.bkSvc.getExpenditureDetails(this.inviteToken)
        this.expenditure$.subscribe((exp) => console.log(exp))
        this.toggleExpenseModal()
      }
    })
  }

  handleRouteToBalance() {
    this.router.navigate([`/expenditure/${this.inviteToken}/balance`])
  }

  onPageLoad(expenditure: Expenditure) {
    const storedUserId = this.util.getUserIdFromLocalStorage();
    const authUser = this.util.getAuthUserFromLocalStorage();
    if (authUser) {
      this.store.dispatch(loginUser({ user: authUser }))
    }

    expenditure.users.forEach(expenditureUser => {
      const user = expenditureUser as User;
      if (storedUserId == expenditureUser.userId) {
        this.selectUser(user)
      } 
    })
  }
}
