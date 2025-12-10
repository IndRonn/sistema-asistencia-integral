import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { AttendanceRecord } from '@core/models/attendance.model';
import { UiBadgeComponent, BadgeVariant } from '@shared/components/ui-badge/ui-badge.component';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule, UiBadgeComponent],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  private attendanceService = inject(AttendanceService);

  // Estados
  records = signal<AttendanceRecord[]>([]);
  isLoading = signal<boolean>(true);

  // Paginación
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  totalElements = signal<number>(0);
  isLastPage = signal<boolean>(true);
  isFirstPage = signal<boolean>(true);

  // Filtros
  fechaInicio = signal<string>('');
  fechaFin = signal<string>('');

  ngOnInit() {
    this.loadHistory();
  }

  loadHistory(page: number = 0) {
    this.isLoading.set(true);

    // El servicio ya lo teníamos configurado en el paso anterior
    this.attendanceService.getHistorial(page, 10, this.fechaInicio(), this.fechaFin()).subscribe({
      next: (res) => {
        this.records.set(res.content || []);

        // Sincronizar paginación
        this.currentPage.set(res.number);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.isFirstPage.set(res.first);
        this.isLastPage.set(res.last);

        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Error cargando historial:', err);
        this.isLoading.set(false);
      }
    });
  }

  prevPage() {
    if (!this.isFirstPage()) this.loadHistory(this.currentPage() - 1);
  }

  nextPage() {
    if (!this.isLastPage()) this.loadHistory(this.currentPage() + 1);
  }

  getVariant(texto: string): BadgeVariant {
    if (!texto) return 'neutral';
    const s = texto.toUpperCase();
    if (s.includes('PUNTUAL')) return 'success';
    if (s.includes('TARDE') || s.includes('TARDANZA')) return 'warning';
    if (s.includes('FALTA') || s.includes('AUSENTE')) return 'danger';
    return 'neutral';
  }
}
