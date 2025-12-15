import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '@env/environment';

import { LoginRequest, LoginResponse, UserProfile } from '@core/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';


  currentUserSignal = signal<UserProfile | null>(null);


  public fullName = computed(() => {
    const user = this.currentUserSignal();
    return user ? `${user.nombres} ${user.apellidos}` : 'Usuario';
  });

  constructor() {
    this.loadSession();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap(response => this.saveSession(response))
    );
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUserSignal.set(null);
    this.router.navigate(['/auth/login']);
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }


  currentUser() {
    return this.currentUserSignal();
  }

  currentRole(): 'ADMIN' | 'EMPLEADO' | null {
    return this.currentUserSignal()?.rol || null;
  }

  private saveSession(response: LoginResponse) {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.usuario));
    this.currentUserSignal.set(response.usuario);
  }

  private loadSession() {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (userStr) {
      try {
        this.currentUserSignal.set(JSON.parse(userStr));
      } catch {
        this.logout();
      }
    }
  }
}
