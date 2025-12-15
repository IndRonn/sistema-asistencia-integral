import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Usuario, UsuarioRequest, UserResponse } from '@core/models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private readonly API = `${environment.apiUrl}/usuarios`;


  getAll(page: number = 0, size: number = 10, search: string = ''): Observable<UserResponse> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (search) params = params.set('search', search);

    return this.http.get<UserResponse>(this.API, { params });
  }

  // Crear
  create(user: UsuarioRequest): Observable<Usuario> {
    return this.http.post<Usuario>(this.API, user);
  }

  // Cambiar Estado
  toggleStatus(id: number, nuevoEstado: 'A' | 'I'): Observable<void> {
    return this.http.patch<void>(`${this.API}/${id}/estado`, { estado: nuevoEstado });
  }
}
