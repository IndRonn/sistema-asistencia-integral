import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-ui-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ui-toast.component.html',
  styleUrls: ['./ui-toast.component.css']
})
export class UiToastComponent {
  // Inyectamos el servicio para acceder a la señal 'toasts'
  protected toastService = inject(ToastService);
}
