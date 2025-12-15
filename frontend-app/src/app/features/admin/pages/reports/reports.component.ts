import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReportService } from '@core/services/report/report.service';
import { UserService } from '@core/services/user/user.service';
import { UiBadgeComponent, BadgeVariant } from '@shared/components/ui-badge/ui-badge.component';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { ReporteAsistencia } from '@core/models/report.model';
import { Usuario } from '@core/models/user.model';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, UiBadgeComponent],
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css']
})
export class ReportsComponent implements OnInit {
  private reportService = inject(ReportService);
  private userService = inject(UserService);
  private toast = inject(ToastService);

  // Datos
  reportes = signal<ReporteAsistencia[]>([]);
  empleados = signal<Usuario[]>([]); // Lista para el Dropdown

  // Estados de Carga
  isLoading = signal(false);
  isExporting = signal(false);

  // Filtros
  fechaInicio: string = '';
  fechaFin: string = '';
  selectedEmpleado: string = '';

  ngOnInit() {
    this.initDates();
    this.loadEmpleados();
  }


  private initDates() {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);

    // Formato YYYY-MM-DD para el input date
    this.fechaFin = today.toISOString().split('T')[0];
    this.fechaInicio = firstDay.toISOString().split('T')[0];
  }

  // Cargar lista de agentes para el filtro
  private loadEmpleados() {
    this.userService.getAll(0, 100).subscribe({
      next: (res) => this.empleados.set(res.content)
    });
  }

  // Generar Reporte en Pantalla
  generarReporte() {
    this.isLoading.set(true);

    const id = this.selectedEmpleado ? Number(this.selectedEmpleado) : undefined;

    this.reportService.getReportes({
      inicio: this.fechaInicio,
      fin: this.fechaFin,
      idEmpleado: id
    }).subscribe({
      next: (data) => {
        this.reportes.set(data);
        this.isLoading.set(false);

        if (data.length === 0) {
          this.toast.show('No hay actividad registrada en este periodo.', 'info');
        } else {
          this.toast.show(`Inteligencia generada: ${data.length} registros.`, 'success');
        }
      },
      error: (err) => {
        console.error(err);
        this.toast.show('Error al conectar con Oracle.', 'error');
        this.isLoading.set(false);
      }
    });
  }

  // Exportar Evidencia (Excel o PDF)
  exportar(formato: 'EXCEL' | 'PDF') {
    this.isExporting.set(true);
    const id = this.selectedEmpleado ? Number(this.selectedEmpleado) : undefined;

    this.reportService.exportarArchivo({
      inicio: this.fechaInicio,
      fin: this.fechaFin,
      idEmpleado: id
    }, formato).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;


        const ext = formato === 'EXCEL' ? 'xlsx' : 'pdf';
        a.download = `Reporte_Asistencia_${this.fechaInicio}.${ext}`;

        a.click();
        window.URL.revokeObjectURL(url);

        this.toast.show(`📥 ${formato} descargado correctamente`, 'success');
        this.isExporting.set(false);
      },
      error: () => {
        this.toast.show(`Error al generar ${formato}`, 'error');
        this.isExporting.set(false);
      }
    });
  }

  // Helpers Visuales
  getVariant(estado: string): BadgeVariant {
    switch (estado) {
      case 'P': return 'success';  // PUNTUAL (Verde)
      case 'T': return 'warning';  // TARDANZA (Amarillo)
      case 'A': return 'danger';   // AUSENTE (Rojo)
      case 'J': return 'neutral';  // JUSTIFICADO (Gris)
      default: return 'neutral';
    }
  }
}
