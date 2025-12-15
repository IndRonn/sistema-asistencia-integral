import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ui-switch',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ui-switch.component.html',
  styleUrls: ['./ui-switch.component.css']
})
export class UiSwitchComponent {
  @Input() checked: boolean = false;
  @Output() changed = new EventEmitter<boolean>();

  toggle() {
    this.changed.emit(!this.checked); // Emite el valor contrario
  }
}
