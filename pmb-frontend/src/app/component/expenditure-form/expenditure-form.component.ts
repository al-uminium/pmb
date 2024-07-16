import { Component, OnInit, inject } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroUserPlus, heroXCircle, heroXMark } from '@ng-icons/heroicons/outline';
import { UtilService } from '../../service/util.service';
import { DropdownComponent } from '../utility/dropdown/dropdown.component';
import { BackendService } from '../../service/backend.service';
import { Expenditure } from '../../classes/expenditure';

@Component({
  selector: 'app-expenditure-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, NgIconComponent, DropdownComponent],
  providers: [provideIcons({ heroUserPlus, heroXCircle, heroXMark }), UtilService],
  templateUrl: './expenditure-form.component.html',
  styleUrl: './expenditure-form.component.css'
})
export class ExpenditureFormComponent implements OnInit{

  private readonly fb = inject(FormBuilder);
  private readonly utilSvc = inject(UtilService);
  private readonly bkendSvc = inject(BackendService);
  private readonly router = inject(Router);

  form!: FormGroup
  defCurrencies: string[] = ["USD", "EUR", "GBP", "SGD"];

  ngOnInit(): void {
    this.form = this.fb.group({
      expenditureName: this.fb.control<string>('', Validators.required),
      usernames: this.fb.array([this.fb.control<string>('', Validators.required)]),
      selectedCurrency: this.fb.control<string>('', Validators.required)
    })
  }

  get usernames() {
    return this.form.get('usernames') as FormArray;
  }

  get expenditureName() {
    return this.form.get('expenditureName') as FormArray;
  }

  get selectedCurrency() {
    return this.form.get('selectedCurrency') as FormArray;
  }

  addUser() {
    this.usernames.push(this.fb.control('', Validators.required));
  }

  deleteUser(index: number) {
    this.usernames.removeAt(index);
  }

  setCurrency(curr: string){
    this.form.controls['selectedCurrency'].setValue(curr);
  }

  onSubmit(): void {
    const expenditureName = this.expenditureName?.value;
    const userArray = this.utilSvc.getUsersFromForm(this.usernames);
    const currency = this.selectedCurrency?.value;
    const inviteToken = this.utilSvc.generateSecureRandomString(25);

    this.bkendSvc.createExpenditure(new Expenditure(expenditureName, userArray, currency, inviteToken)).subscribe({
      next: () => this.router.navigate(['expenditure', inviteToken]),
      complete: () => console.log(inviteToken),
      error: (err) => console.log(err)
    })
    console.log(expenditureName, userArray, currency);
  }
}
