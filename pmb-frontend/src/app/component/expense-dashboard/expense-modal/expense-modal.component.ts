import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroCheck, heroUserPlus, heroXMark } from '@ng-icons/heroicons/outline';
import { User } from '../../../classes/user';
import { UtilService } from '../../../service/util.service';
import { Expense } from '../../../classes/expenses';
import { DropdownComponent } from '../../utility/dropdown/dropdown.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-expense-modal',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, NgIconComponent, DropdownComponent, CommonModule],
  providers: [provideIcons({heroXMark, heroUserPlus, heroCheck})],
  templateUrl: './expense-modal.component.html',
  styleUrl: './expense-modal.component.css'
})
export class ExpenseModalComponent implements OnInit{
  private readonly fb = inject(FormBuilder);
  private readonly utilSvc = inject(UtilService);

  form!: FormGroup;

  @Input()
  expense!: Expense
  
  @Input()
  usersList!: User[]

  @Input()
  selectedUser!: User

  @Input()
  currency!: string;

  @Output()
  createExpenseEvent: EventEmitter<Expense> = new EventEmitter<Expense>

  isSplitEven: boolean = true;
  isCostIncurredValid: boolean = true;

  ngOnInit(): void {
    this.form = this.fb.group({
      expenseName: this.fb.control<string>('', Validators.required),
      totalCost: this.fb.control<number>(0, Validators.compose([Validators.required, Validators.min(0)])),
      users: this.fb.control<User>(this.usersList.at(0) as User, Validators.required),
      costIncurred: this.fb.array([])
    })
    this.populateCostIncurredArray()
  }

  get costIncurred () {
    const formArray = this.form.get('costIncurred') as FormArray;
    return formArray.controls as FormGroup[]
  }

  toggleIsSplitEven(): void {
    this.isSplitEven = !this.isSplitEven;
  }

  toggle(group: FormGroup) {
    if (!group.get('checked')?.value) {
      group.get('cost')?.enable()
    } else {
      group.get('cost')?.disable()
      group.get('cost')?.setValue('');
    }

  }

  populateCostIncurredArray(): void {
    this.usersList.forEach(() => {
      this.costIncurred.push(this.fb.group({
        checked: this.fb.control<boolean>(true),
        cost: this.fb.control<number>(0, Validators.min(0)),
      }))
    });
  }

  handleSubmit(): void {
    const expenseName = this.form.get('expenseName')?.value;
    const totalCost = this.form.get('totalCost')?.value;
    const usersInvolved = this.utilSvc.getUsersInvolved(this.costIncurred, this.usersList);
    this.isCostIncurredValid = this.utilSvc.checkIfExpenseSplitIsValid(this.costIncurred);
    if (this.isCostIncurredValid) {
      if (this.isSplitEven) {
        const expenseSplit = this.utilSvc.getExpenseSplitIfSplitEven(totalCost, this.costIncurred, this.usersList);
        const expense = new Expense(expenseName, this.selectedUser, totalCost, expenseSplit, usersInvolved, "", "");
        this.createExpenseEvent.emit(expense);
      } else {
        const expenseSplit = this.utilSvc.getExpenseSplitFromFormGroup(this.costIncurred, this.usersList);
        const expense = new Expense(expenseName, this.selectedUser, totalCost, expenseSplit, usersInvolved, "", "");
        this.createExpenseEvent.emit(expense);
      }
    }

  }
}
