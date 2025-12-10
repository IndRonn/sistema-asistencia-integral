import { Component, OnInit, inject, signal, computed, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { DashboardStatus, UIStateConfig } from '@core/models/attendance.model';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  private attendanceService = inject(AttendanceService);

  isLoading = signal<boolean>(true);
  statusData = signal<DashboardStatus | null>(null);

  // 🧠 SELECTOR INTELIGENTE: Transforma datos crudos en Diseño
  uiState = computed<UIStateConfig>(() => {
    const estado = this.statusData()?.estado;

    switch (estado) {
      case 'EN_JORNADA':
        return {
          color: 'text-sys-primary',        // Verde
          borderColor: 'border-sys-primary',
          label: 'EN CURSO',
          actionLabel: 'TERMINAR JORNADA'
        };
      case 'FINALIZADO':
        return {
          color: 'text-blue-500',           // Azul (Jornada terminada)
          borderColor: 'border-blue-500',
          label: 'FINALIZADO',
          actionLabel: 'VER RESUMEN'
        };
      case 'SIN_MARCAR':
      default:
        return {
          color: 'text-sys-silver',         // Gris Plata
          borderColor: 'border-sys-dim',
          label: 'PENDIENTE',
          actionLabel: 'INICIAR JORNADA'
        };
    }
  });

  ngOnInit(): void {
    this.loadStatus();
  }

  loadStatus() {
    this.isLoading.set(true);
    this.attendanceService.getDashboardStatus().subscribe({
      next: (data) => {
        console.log('Estado recibido:', data); // Debug
        this.statusData.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error dashboard:', err);
        this.isLoading.set(false);
      }
    });
  }
}
