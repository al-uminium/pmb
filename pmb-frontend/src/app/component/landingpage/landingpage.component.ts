import { Component, inject } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-landingpage',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './landingpage.component.html',
  styleUrl: './landingpage.component.css'
})
export class LandingpageComponent {
  private readonly router = inject(Router);

  routeToCreateExpedition(): void {
    this.router.navigate(["/expenditure/create"]);
  }

  routeToLogin(): void {
    this.router.navigate(["/login"]);
  }

  routeToRegister(): void {
    this.router.navigate(["/register"])
  }
}
