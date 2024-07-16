import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { User } from '../../../classes/user';
import { BackendService } from '../../../service/backend.service';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProfilePictureComponent } from '../../utility/profile-picture/profile-picture.component';

@Component({
  selector: 'app-select-user-modal',
  standalone: true,
  imports: [CommonModule, ProfilePictureComponent],
  templateUrl: './select-user-modal.component.html',
  styleUrl: './select-user-modal.component.css'
})
export class SelectUserModalComponent{
  @Input()
  text!: string

  @Input()
  users!: User[]

  @Output() 
  selectUserEvent: EventEmitter<User> = new EventEmitter<User>;

  selectUser(user: User):void {
    this.selectUserEvent.emit(user);
  }
}
