import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Configuracion, ConfigChange } from '@core/models/config.model';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private http = inject(HttpClient);
  private readonly API = `${environment.apiUrl}/admin/configuracion`;

  getConfigs(): Observable<Configuracion[]> {
    return this.http.get<Configuracion[]>(this.API);
  }

  updateConfigs(cambios: ConfigChange[]): Observable<void> {
    return this.http.put<void>(this.API, cambios);
  }
}
