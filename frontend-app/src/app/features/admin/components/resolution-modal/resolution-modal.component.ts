import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '@core/services/admin/admin.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { Resolucion } from '@core/models/admin.model';

export type ResolutionAction = 'APROBADO' | 'RECHAZADO' | null;

@Component({
  selector: 'app-resolution-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './resolution-modal.component.html',
  styles: []
})
export class ResolutionModalComponent {
  private adminService = inject(AdminService);
  private toast = inject(ToastService);

  @Output() resolveEvent = new EventEmitter<number>();

  // Estados
  isOpen = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);
  currentId = signal<number | null>(null);
  currentAction = signal<ResolutionAction>(null);

  // Campo del formulario
  comentario: string = '';

  open(id: number, action: ResolutionAction) {
    this.currentId.set(id);
    this.currentAction.set(action);
    this.comentario = '';
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
    this.currentId.set(null);
    this.currentAction.set(null);
  }

  submit() {
    if (!this.currentId() || !this.currentAction()) return;

    if (this.currentAction() === 'RECHAZADO' && this.comentario.length < 5) {
      this.toast.show('⚠️ Escribe un motivo para rechazar.', 'warning');
      return;
    }

    this.isSubmitting.set(true);

    const payload: Resolucion = {
      estado: this.currentAction()!,
      comentario: this.comentario || (this.currentAction() === 'APROBADO' ? 'Solicitud Aprobada.' : 'Solicitud Rechazada.')
    };

    this.adminService.resolver(this.currentId()!, payload).subscribe({
      next: (res) => {
        this.toast.show(res.mensaje, 'success');
        this.resolveEvent.emit(this.currentId()!);
        this.close();
        this.isSubmitting.set(false);
      },
      error: (err) => {
        console.error(err);
        this.toast.show(err.error?.message || 'Error al procesar', 'error');
        this.isSubmitting.set(false);
      }
    });
  }
}
