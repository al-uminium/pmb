import { Component, inject } from '@angular/core';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroEllipsisVertical } from '@ng-icons/heroicons/outline';
import { Store, StoreModule } from '@ngrx/store';
import { logoutUser } from '../../../state/user.actions';

@Component({
  selector: 'app-navbar-dropdown',
  standalone: true,
  imports: [NgIconComponent, StoreModule],
  providers: [provideIcons({ heroEllipsisVertical })],
  templateUrl: './navbar-dropdown.component.html',
  styleUrl: './navbar-dropdown.component.css'
})
export class NavbarDropdownComponent {
  private readonly store = inject(Store)
  showDropdown: boolean = false;

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  handleLogOut(): void {
    localStorage.removeItem("authUser");
    this.store.dispatch(logoutUser());
  }
}
