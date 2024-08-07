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
  defCurrencies: string[] = ["USD", "EUR", "GBP", "SGD", "CAD"];

  ngOnInit(): void {
    this.form = this.fb.group({
      expenditureName: this.fb.control<string>('', Validators.compose([Validators.required, Validators.maxLength(255)])),
      usernames: this.fb.array([this.fb.control<string>('', Validators.compose([Validators.required, Validators.maxLength(255), this.utilSvc.noDotValidator()]))]),
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

    this.bkendSvc.createExpenditure(new Expenditure(expenditureName, userArray, currency)).subscribe(resp => {
      if (resp.inviteToken) {
        this.router.navigate([`expenditure/${resp.inviteToken}`])
      } else {
        console.log("Error occured :(");
      }
    })
    console.log(expenditureName, userArray, currency);
  }
}
