import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
// Importamos los nuevos modelos
import {
  DashboardStatus,
  CheckInResponse,
  HistoryResponse
} from '@core/models/attendance.model';
import {
  JustificationRequest,
  JustificationResponse
} from '@core/models/justification.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/asistencia`;

  currentStatus = signal<DashboardStatus | null>(null);

  getDashboardStatus(): Observable<DashboardStatus> {
    return this.http.get<DashboardStatus>(`${this.API_URL}/estado-actual`).pipe(
      tap(status => this.currentStatus.set(status))
    );
  }

  marcar(): Observable<CheckInResponse> {
    return this.http.post<CheckInResponse>(`${this.API_URL}/marcar`, {});
  }

  getHistorial(page: number = 0, size: number = 10, fechaInicio?: string, fechaFin?: string): Observable<HistoryResponse> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (fechaInicio) params = params.set('inicio', fechaInicio);
    if (fechaFin) params = params.set('fin', fechaFin);

    return this.http.get<HistoryResponse>(`${this.API_URL}/historial`, { params });
  }

  // ✅ NUEVO: Método para enviar la solicitud de justificación
  solicitarJustificacion(request: JustificationRequest): Observable<JustificationResponse> {
    // POST /asistencia/justificaciones
    return this.http.post<JustificationResponse>(`${this.API_URL}/justificaciones`, request);
  }
}
