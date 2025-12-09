import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

// Usamos los alias que acabamos de configurar
import { environment } from '@env/environment';
import { LoginRequest, AuthResponse, UserProfile } from '@core/models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Inyección de dependencias moderna (sin constructor verbose)
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly API_URL = `${environment.apiUrl}/auth`;
  private readonly TOKEN_KEY = 'sys_token';
  private readonly USER_KEY = 'sys_user';

  // ⚡ STATE (Signals)
  // Inicializamos el signal leyendo del storage para persistencia al recargar (F5)
  private currentUserSignal = signal<UserProfile | null>(this.getUserFromStorage());

  // ⚡ COMPUTED VALUES (Selectores)
  // Estas señales derivadas se actualizan automáticamente cuando currentUserSignal cambia
  public currentUser = this.currentUserSignal.asReadonly();
  public isAuthenticated = computed(() => !!this.currentUserSignal());
  public currentRole = computed(() => this.currentUserSignal()?.rol);
  public fullName = computed(() => this.currentUserSignal()?.nombreCompleto);

  /**
   * Realiza la petición de login y guarda la sesión si es exitosa.
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, credentials).pipe(
      tap((response) => {
        this.saveSession(response);
      })
    );
  }

  /**
   * Cierra la sesión, limpia el storage y redirige al login.
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);

    // Actualizamos el signal a null, la UI reaccionará instantáneamente
    this.currentUserSignal.set(null);

    this.router.navigate(['/auth/login']);
  }

  /**
   * Retorna el token actual (usado por el Interceptor).
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  // --- MÉTODOS PRIVADOS ---

  private saveSession(response: AuthResponse): void {
    // 1. Guardar Token en LocalStorage
    localStorage.setItem(this.TOKEN_KEY, response.token);

    // 2. Guardar objeto Usuario en LocalStorage (para persistencia al recargar)
    localStorage.setItem(this.USER_KEY, JSON.stringify(response.usuario));

    // 3. Actualizar el Signal (esto dispara los computed values en toda la app)
    this.currentUserSignal.set(response.usuario);
  }

  private getUserFromStorage(): UserProfile | null {
    const userStr = localStorage.getItem(this.USER_KEY);
    if (!userStr) return null;
    try {
      return JSON.parse(userStr) as UserProfile;
    } catch (e) {
      console.error('Error parsing user from storage', e);
      return null;
    }
  }
}
