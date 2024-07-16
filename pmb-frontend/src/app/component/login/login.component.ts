import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { BackendService } from '../../service/backend.service';
import { User } from '../../classes/user';
import { Observable } from 'rxjs';
import { Store, StoreModule } from '@ngrx/store';
import { selectAuthUser } from '../../state/user.selectors';
import { loginUser } from '../../state/user.actions';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, StoreModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent implements OnInit{
  private readonly fb = inject(FormBuilder);
  private readonly bkSvc = inject(BackendService);
  private readonly router = inject(Router);
  private readonly store = inject(Store)

  form!: FormGroup
  isLoginFailed: boolean = false;

  ngOnInit(): void {
    this.form = this.fb.group({
      email: this.fb.control<string>('', Validators.required),
      password: this.fb.control<string>('', Validators.required)
    })  
  }

  get email() {
    return this.form.get('email') as FormControl;
  }

  get password() {
    return this.form.get('password') as FormControl;
  }
  
  handleLogin() {
    const user = new User('');
    user.email = this.email?.value;
    user.pw = this.password?.value;
    this.bkSvc.postLogin(user).subscribe((user) => {
      if (user.userId) {
        this.store.dispatch(loginUser({ user: user }))
        localStorage.setItem('authUser', JSON.stringify({ user }));
        localStorage.setItem('isLoggedIn', JSON.stringify(true));
        this.router.navigate(['user']);
      } else {
        this.isLoginFailed = true; 
      }
    })
  }
}
