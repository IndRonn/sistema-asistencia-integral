import { Component, OnInit, OnDestroy, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { DashboardStatus, UIStateConfig } from '@core/models/attendance.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, OnDestroy {
  private attendanceService = inject(AttendanceService);
  private toast = inject(ToastService);

  // Estados
  isLoading = signal<boolean>(true);
  isProcessingMarca = signal<boolean>(false); // Bloqueo de botón
  statusData = signal<DashboardStatus | null>(null);

  // Timer en vivo
  liveTime = signal<string>('--:--:--');
  private timerInterval: any;

  // Lógica de Diseño Computada
  uiState = computed<UIStateConfig>(() => {
    const estado = this.statusData()?.estado;
    switch (estado) {
      case 'EN_JORNADA':
        return {
          color: 'text-sys-primary', borderColor: 'border-sys-primary',
          label: 'EN CURSO', actionLabel: 'TERMINAR JORNADA'
        };
      case 'FINALIZADO':
        return {
          color: 'text-blue-500', borderColor: 'border-blue-500',
          label: 'FINALIZADO', actionLabel: 'JORNADA CERRADA'
        };
      default:
        return {
          color: 'text-sys-silver', borderColor: 'border-sys-dim',
          label: 'PENDIENTE', actionLabel: 'INICIAR JORNADA'
        };
    }
  });

  ngOnInit(): void {
    this.loadStatus();
  }

  ngOnDestroy(): void {
    this.stopTimer();
  }

  loadStatus() {
    this.isLoading.set(true);
    this.attendanceService.getDashboardStatus().subscribe({
      next: (data) => {
        this.statusData.set(data);
        this.isLoading.set(false);

        // Iniciar cronómetro si está trabajando
        if (data.estado === 'EN_JORNADA' && data.horaEntrada) {
          this.startTimer(data.horaEntrada);
        } else {
          this.stopTimer();
          // Mostrar hora estática si no está corriendo el tiempo
          this.liveTime.set(data.horaEntrada || '--:--:--');
        }
      },
      error: () => this.isLoading.set(false)
    });
  }

  // --- ACCIÓN DE MARCAR ---
  onMarcar() {
    if (this.isProcessingMarca()) return;

    this.isProcessingMarca.set(true);

    this.attendanceService.marcar().subscribe({
      next: (res) => {
        // 1. Feedback Exitoso
        this.toast.show(res.mensaje, 'success');

        // 2. Recargar estado para sincronizar vista
        this.loadStatus();

        this.isProcessingMarca.set(false);
      },
      error: (err) => {
        // 3. Manejo de Error (409 Conflict)
        const msg = err.error?.message || 'Error al procesar la marca';
        this.toast.show(msg, 'error');
        this.isProcessingMarca.set(false);
      }
    });
  }

  // --- LÓGICA DEL RELOJ ---
  private startTimer(horaEntradaStr: string) {
    this.stopTimer();

    // Crear fecha de referencia (Hoy + horaEntrada)
    const [h, m, s] = horaEntradaStr.split(':').map(Number);
    const entrada = new Date();
    entrada.setHours(h, m, s);

    const update = () => {
      const ahora = new Date();
      const diff = Math.abs(ahora.getTime() - entrada.getTime());

      const seconds = Math.floor((diff / 1000) % 60);
      const minutes = Math.floor((diff / (1000 * 60)) % 60);
      const hours = Math.floor(diff / (1000 * 60 * 60));

      const pad = (n: number) => n.toString().padStart(2, '0');
      this.liveTime.set(`${pad(hours)}:${pad(minutes)}:${pad(seconds)}`);
    };

    update();
    this.timerInterval = setInterval(update, 1000);
  }

  private stopTimer() {
    if (this.timerInterval) clearInterval(this.timerInterval);
  }
}
