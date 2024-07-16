import { Component, inject, Input, OnInit } from '@angular/core';
import { PaypalService } from '../../../service/paypal.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-paypal-pay',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './paypal-pay.component.html',
  styleUrl: './paypal-pay.component.css'
})
export class PaypalPayComponent implements OnInit{
  private readonly paypalSvc = inject(PaypalService)
  // private readonly router = inject(Router)
  private readonly actRoute = inject(ActivatedRoute)

  orderId!: string
  payerId: string = 'A6EDG96W5TGXG';
  
  @Input()
  payeeEmail!: string;

  @Input()
  amt!: number;

  @Input()
  inviteToken!: string

  ngOnInit(): void {
    this.actRoute.queryParams.subscribe(params => {
      this.orderId = params['paymentId'];
    })
  }

  pay() {
    this.paypalSvc.createOrder(this.amt, this.payeeEmail, this.inviteToken).subscribe((response: any) => {
      window.location.href = response.approvalUrl
    })
  }
}
