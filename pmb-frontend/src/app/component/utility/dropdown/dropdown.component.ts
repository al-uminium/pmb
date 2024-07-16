import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroChevronDown } from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-dropdown',
  standalone: true,
  imports: [NgIconComponent],
  providers: [provideIcons({ heroChevronDown })],
  templateUrl: './dropdown.component.html',
  styleUrl: './dropdown.component.css'
})
export class DropdownComponent{
  @Input()
  value: any = "";

  @Input()
  values!: any[];

  @Output()
  valueChange: EventEmitter<string> = new EventEmitter<string>();

  showDropdown: boolean = false;

  selectVal(value: any){
    this.valueChange.emit(value);
    this.value = value;
    this.showDropdown = false;
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }
}
