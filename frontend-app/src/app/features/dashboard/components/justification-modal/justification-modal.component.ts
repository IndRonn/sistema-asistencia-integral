import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { JustificationRequest, JustificationType } from '@core/models/justification.model';
import { AttendanceRecord } from '@core/models/attendance.model';

@Component({
  selector: 'app-justification-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './justification-modal.component.html',
  styleUrls: [] // Tailwind
})
export class JustificationModalComponent {
  private fb = inject(FormBuilder);
  private attendanceService = inject(AttendanceService);
  private toast = inject(ToastService);

  // Estados de Visibilidad y Carga
  isOpen = signal<boolean>(false);
  isSubmitting = signal<boolean>(false);

  // Datos del registro seleccionado
  selectedRecord = signal<AttendanceRecord | null>(null);

  // Formulario Reactivo
  form: FormGroup = this.fb.group({
    fecha: [{ value: '', disabled: true }],
    tipo: ['', [Validators.required]],
    motivo: ['', [Validators.required, Validators.minLength(10)]]
  });

  // Opciones para el Select
  types: JustificationType[] = ['SALUD', 'PERSONAL', 'TRABAJO'];


  open(record: AttendanceRecord) {
    this.selectedRecord.set(record);
    this.isOpen.set(true);

    // Resetear y pre-cargar formulario
    this.form.reset({
      fecha: record.fecha,
      tipo: '',
      motivo: ''
    });
  }

  close() {
    this.isOpen.set(false);
    this.selectedRecord.set(null);
  }

  submit() {
    if (this.form.invalid || !this.selectedRecord()) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);

    // Preparar Payload
    const payload: JustificationRequest = {
      idAsistencia: this.selectedRecord()!.idAsistencia,
      fecha: this.selectedRecord()!.fecha, // Enviamos la fecha original del registro
      tipo: this.form.get('tipo')?.value,
      motivo: this.form.get('motivo')?.value
    };

    this.attendanceService.solicitarJustificacion(payload).subscribe({
      next: (res) => {
        this.toast.show(res.mensaje || 'Solicitud enviada correctamente', 'success');
        this.isSubmitting.set(false);
        this.close();

      },
      error: (err) => {
        this.isSubmitting.set(false);
        const code = err.error?.code;
        const msg = err.error?.message || 'Error al procesar solicitud';


        if (code === 'JUST-001') {
          this.toast.show('⚠️ Ya existe una solicitud pendiente para esta fecha.', 'warning');
        } else if (code === 'JUST-002') {
          this.toast.show('⏳ No puedes justificar fechas futuras.', 'error');
        } else {
          this.toast.show(msg, 'error');
        }
      }
    });
  }
}
