import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../classes/user';
import { BackendService } from '../../service/backend.service';
import { Store, StoreModule } from '@ngrx/store';
import { loginUser, selectUser } from '../../state/user.actions';

@Component({
  selector: 'app-signup-form',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, StoreModule],
  templateUrl: './signup-form.component.html',
  styleUrl: './signup-form.component.css'
})
export class SignupFormComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly bkSvc = inject(BackendService);
  private readonly store = inject(Store);
  private readonly router = inject(Router);

  form!: FormGroup

  ngOnInit(): void {
    this.form = this.fb.group({
      username: this.fb.control<string>('', Validators.compose([Validators.required, Validators.pattern('^[a-zA-Z0-9]+$')])),
      password: this.fb.control<string>('', Validators.required),
      email: this.fb.control<string>('', Validators.compose([Validators.required, Validators.email]))
    })
  }

  get username() {
    return this.form.get('username') as FormControl;
  }

  get password() {
    return this.form.get('password') as FormControl;
  }

  get email() {
    return this.form.get('email') as FormControl;
  }

  handleOnRegister() {
    const user = new User(this.username?.value);
    user.pw = this.password?.value;
    user.email = this.email?.value; 
    this.bkSvc.createUser(user).subscribe((user) => {
      this.store.dispatch(loginUser({ user: user }));
      localStorage.setItem('authUser', JSON.stringify({ user }));
      localStorage.setItem('isLoggedIn', JSON.stringify(true));
      this.router.navigate(['user']);
    })
  }
}
