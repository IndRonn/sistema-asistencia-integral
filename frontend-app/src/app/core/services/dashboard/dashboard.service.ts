import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { HistoricoSemana, KpiResumen, MonitorVivo} from '@core/models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private http = inject(HttpClient);
  private readonly API_URL = `${environment.apiUrl}/admin/dashboard`;

  getKpis(): Observable<KpiResumen> {
    return this.http.get<KpiResumen>(`${this.API_URL}/kpis`);
  }

  getMonitorVivo(): Observable<MonitorVivo[]> {
    return this.http.get<MonitorVivo[]>(`${this.API_URL}/live`);
  }

  getHistoricoSemanal(): Observable<HistoricoSemana[]> {
    return this.http.get<HistoricoSemana[]>(`${this.API_URL}/semanal`);
  }
}
