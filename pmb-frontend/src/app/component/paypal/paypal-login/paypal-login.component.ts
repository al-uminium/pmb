import { Component, inject } from '@angular/core';
import { PaypalService } from '../../../service/paypal.service';


@Component({
  selector: 'app-paypal-login',
  standalone: true,
  imports: [],
  templateUrl: './paypal-login.component.html',
  styleUrl: './paypal-login.component.css'
})
export class PaypalLoginComponent {
  private readonly paypalSvc = inject(PaypalService)

  linkPaypal() {
    this.paypalSvc.getLinkPaypalAccount().subscribe((resp: any) => {
      window.location.href = resp.approvalUrl
    })
  }
}
