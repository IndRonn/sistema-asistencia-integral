import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
import { DashboardStatus, CheckInResponse } from '@core/models/attendance.model';

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

  // NUEVO: Método para Marcar
  marcar(): Observable<CheckInResponse> {
    // El body va vacío porque el usuario se identifica por el Token del Interceptor
    return this.http.post<CheckInResponse>(`${this.API_URL}/marcar`, {});
  }
}
