import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '@core/services/user/user.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { UsuarioRequest } from '@core/models/user.model';

@Component({
  selector: 'app-user-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-modal.component.html',
  styles: []
})
export class UserModalComponent {
  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private toast = inject(ToastService);

  @Output() saved = new EventEmitter<void>();

  isOpen = signal(false);
  isSubmitting = signal(false);

  form: FormGroup = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    nombres: ['', [Validators.required]],
    apellidos: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    rol: ['EMPLEADO', [Validators.required]]
  });

  open() {
    this.form.reset({ rol: 'EMPLEADO' }); // Valor por defecto
    this.isOpen.set(true);
  }

  close() {
    this.isOpen.set(false);
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    const payload: UsuarioRequest = this.form.value;

    this.userService.create(payload).subscribe({
      next: (newUser) => {

        this.toast.show(`Personal reclutado: ${newUser.username}`, 'success');
        this.saved.emit();
        this.close();
        this.isSubmitting.set(false);
      },
      error: (err) => {

        this.isSubmitting.set(false);


        if (err.status === 409) {

          const msg = err.error?.message || 'Conflicto: El usuario o email ya está registrado.';
          this.toast.show(`⚠️ ${msg}`, 'warning');
        }


        else if (err.status === 400) {
          this.toast.show('Datos inválidos. Verifique el formulario.', 'error');
        }

        else {
          console.error(err);
          this.toast.show('Error crítico en el servidor.', 'error');
        }
      }
    });
  }
}

