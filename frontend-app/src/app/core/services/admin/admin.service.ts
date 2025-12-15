import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { JustificacionPendiente, Resolucion, ResolucionResponse } from '@core/models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/admin`;


  getPendientes(): Observable<JustificacionPendiente[]> {
    return this.http.get<JustificacionPendiente[]>(`${this.API_URL}/justificaciones`);
  }

  resolver(id: number, resolucion: Resolucion): Observable<ResolucionResponse> {
    return this.http.put<ResolucionResponse>(`${this.API_URL}/justificaciones/${id}/resolucion`, resolucion);
  }
}
