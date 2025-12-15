import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { ReporteAsistencia, FiltrosReporte } from '@core/models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/admin/reportes`;

  getReportes(filtros: FiltrosReporte): Observable<ReporteAsistencia[]> {
    let params = new HttpParams()
      .set('inicio', filtros.inicio)
      .set('fin', filtros.fin);

    if (filtros.idEmpleado) {
      params = params.set('idEmpleado', filtros.idEmpleado);
    }

    return this.http.get<ReporteAsistencia[]>(this.API_URL, { params });
  }

  // ✅ ACTUALIZADO: Soporta 'EXCEL' o 'PDF'
  exportarArchivo(filtros: FiltrosReporte, formato: 'EXCEL' | 'PDF'): Observable<Blob> {
    let params = new HttpParams()
      .set('inicio', filtros.inicio)
      .set('fin', filtros.fin)
      .set('formato', formato); // <--- Nuevo parámetro para el Backend

    if (filtros.idEmpleado) {
      params = params.set('idEmpleado', filtros.idEmpleado);
    }

    return this.http.get(`${this.API_URL}/exportar`, {
      params,
      responseType: 'blob'
    });
  }
}
