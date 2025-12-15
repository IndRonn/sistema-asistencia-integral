import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { AuthService } from '@core/services/auth/auth.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  private attendanceService = inject(AttendanceService);
  public authService = inject(AuthService);
  private toast = inject(ToastService);
  private router = inject(Router);

  // Reloj
  currentTime = signal<string>('');
  currentDate = signal<Date>(new Date()); // ✅ Señal para la fecha
  private clockSubscription!: Subscription;

  // Estado de Asistencia
  statusData = this.attendanceService.currentStatus;
  isProcessingMarca = signal<boolean>(false);

  // Lógica UI del Botón (Igual que antes)
  uiState = computed(() => {
    const status = this.statusData();
    if (!status) return {
      label: 'CARGANDO...',
      actionLabel: 'ESPERE',
      color: 'border-gray-500 text-gray-500',
      disabled: true
    };

    switch (status.estado) {
      case 'SIN_MARCAR':
        return {
          label: 'JORNADA PENDIENTE',
          actionLabel: 'INICIAR JORNADA',
          color: 'border-sys-primary text-sys-primary hover:bg-sys-primary hover:text-sys-black',
          disabled: false
        };
      case 'EN_JORNADA':
        return {
          label: 'EN CURSO',
          actionLabel: 'TERMINAR JORNADA',
          color: 'border-sys-warning text-sys-warning hover:bg-sys-warning hover:text-sys-black',
          disabled: false
        };
      case 'FINALIZADO':
        return {
          label: 'JORNADA COMPLETADA',
          actionLabel: 'HASTA MAÑANA',
          color: 'border-sys-dim text-sys-dim',
          disabled: true
        };
      default:
        return { label: 'ERROR', actionLabel: 'ERROR', color: 'border-red-500', disabled: true };
    }
  });

  ngOnInit() {
    this.startClock();
    this.attendanceService.getDashboardStatus().subscribe();
  }

  ngOnDestroy() {
    if (this.clockSubscription) this.clockSubscription.unsubscribe();
  }

  startClock() {
    this.updateTime();
    this.clockSubscription = interval(1000).subscribe(() => this.updateTime());
  }

  updateTime() {
    const now = new Date();
    this.currentDate.set(now); // Actualizamos fecha
    this.currentTime.set(now.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit', second: '2-digit' }));
  }

  onMarcar() {
    if (this.isProcessingMarca()) return;
    this.isProcessingMarca.set(true);

    this.attendanceService.marcar().subscribe({
      next: (res) => {
        this.toast.show(res.mensaje, 'success');
        this.attendanceService.getDashboardStatus().subscribe();
        this.isProcessingMarca.set(false);
      },
      error: (err) => {
        const msg = err.error?.message || 'Error al marcar asistencia';
        this.toast.show(msg, 'error');
        this.isProcessingMarca.set(false);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
