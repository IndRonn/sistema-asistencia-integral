import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService } from '@core/services/attendance/attendance.service';
import { AttendanceRecord } from '@core/models/attendance.model';
import { UiBadgeComponent, BadgeVariant } from '@shared/components/ui-badge/ui-badge.component';
import { JustificationModalComponent } from '../../components/justification-modal/justification-modal.component';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, FormsModule, UiBadgeComponent, JustificationModalComponent],
  templateUrl: './history.component.html',
  styleUrls: ['./history.component.css']
})
export class HistoryComponent implements OnInit {
  // Referencia al Modal hijo
  @ViewChild(JustificationModalComponent) modal!: JustificationModalComponent;

  private attendanceService = inject(AttendanceService);


  historial = signal<AttendanceRecord[]>([]);
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

    this.attendanceService.getHistorial(page, 10, this.fechaInicio(), this.fechaFin()).subscribe({
      next: (res) => {

        this.historial.set(res.content || []);

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


  openJustification(row: AttendanceRecord) {
    if (this.modal) {
      this.modal.open(row);
    }
  }


  onJustificationSaved() {
    this.loadHistory(this.currentPage());
  }

  // Navegación
  prevPage() {
    if (!this.isFirstPage()) this.loadHistory(this.currentPage() - 1);
  }

  nextPage() {
    if (!this.isLastPage()) this.loadHistory(this.currentPage() + 1);
  }


  getVariant(texto: string): BadgeVariant {
    if (!texto) return 'neutral';
    const s = texto.toUpperCase();

    // Mapeo según lo que venga de BD ('P', 'T', 'A' o descripción completa)
    if (s === 'P' || s.includes('PUNTUAL')) return 'success';
    if (s === 'T' || s.includes('TARDE') || s.includes('TARDANZA')) return 'warning';
    if (s === 'A' || s.includes('FALTA') || s.includes('AUSENTE')) return 'danger';
    if (s === 'J' || s.includes('JUSTIFICADO')) return 'neutral';

    return 'neutral';
  }


  getJustificationVariant(estado: string | undefined | null): BadgeVariant {
    if (!estado) return 'neutral';
    switch (estado) {
      case 'APROBADO': return 'success';  // Verde
      case 'RECHAZADO': return 'danger';  // Rojo
      case 'PENDIENTE': return 'warning'; // Amarillo
      default: return 'neutral';
    }
  }


  getJustificationIcon(estado: string | undefined | null): string {
    if (!estado) return '';
    switch (estado) {
      case 'APROBADO': return '✓';
      case 'RECHAZADO': return '✕';
      case 'PENDIENTE': return '⏳';
      default: return '';
    }
  }
}
