import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
// Importamos las interfaces que acabamos de asegurar en el modelo
import { DashboardStatus, CheckInResponse, HistoryResponse } from '@core/models/attendance.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/asistencia`;

  // Estado reactivo para compartir datos si es necesario
  currentStatus = signal<DashboardStatus | null>(null);

  /**
   * Obtiene el estado actual del empleado (Dashboard Zen)
   */
  getDashboardStatus(): Observable<DashboardStatus> {
    return this.http.get<DashboardStatus>(`${this.API_URL}/estado-actual`).pipe(
      tap(status => this.currentStatus.set(status))
    );
  }

  /**
   * Registra la entrada o salida
   */
  marcar(): Observable<CheckInResponse> {
    return this.http.post<CheckInResponse>(`${this.API_URL}/marcar`, {});
  }

  /**
   * ✅ MÉTODO FALTANTE: Obtiene el historial paginado y filtrado
   */
  getHistorial(page: number = 0, size: number = 10, fechaInicio?: string, fechaFin?: string): Observable<HistoryResponse> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    // Solo agregamos filtros de fecha si tienen valor
    if (fechaInicio) params = params.set('inicio', fechaInicio);
    if (fechaFin) params = params.set('fin', fechaFin);

    // GET /asistencia/historial?page=0&size=10&inicio=...
    return this.http.get<HistoryResponse>(`${this.API_URL}/historial`, { params });
  }
}
