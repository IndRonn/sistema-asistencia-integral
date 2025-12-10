import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '@env/environment';
import { DashboardStatus } from '@core/models/attendance.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private http = inject(HttpClient);
  // URL exacta según tu indicación
  private readonly API_URL = `${environment.apiUrl}/asistencia`;

  // Signal para almacenar el estado globalmente (útil para actualizar desde otros componentes)
  currentStatus = signal<DashboardStatus | null>(null);

  getDashboardStatus(): Observable<DashboardStatus> {
    return this.http.get<DashboardStatus>(`${this.API_URL}/estado-actual`).pipe(
      tap(status => this.currentStatus.set(status))
    );
  }
}
