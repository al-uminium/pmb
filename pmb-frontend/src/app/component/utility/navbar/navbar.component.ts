import { Component, inject, OnInit } from '@angular/core';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroHome } from '@ng-icons/heroicons/outline';
import { select, Store, StoreModule } from '@ngrx/store';
import { Observable } from 'rxjs';
import { User } from '../../../classes/user';
import { selectAuthUser } from '../../../state/user.selectors';
import { CommonModule } from '@angular/common';
import { NavbarDropdownComponent } from '../navbar-dropdown/navbar-dropdown.component';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [NgIconComponent, StoreModule, CommonModule, NavbarDropdownComponent],
  providers: [provideIcons({ heroHome })],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  private readonly store = inject(Store);

  authUser$!: Observable<User | null>;
  
  ngOnInit(): void {
    this.authUser$ = this.store.pipe(select(selectAuthUser));
  }
}
