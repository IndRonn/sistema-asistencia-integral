import { Injectable, inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

// Usamos el Alias @core que configuramos
import { AuthService } from '@core/services/auth/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  // Inyección clásica porque los Interceptores basados en Clases (HTTP_INTERCEPTORS)
  // funcionan mejor así en AppModule.
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    // 1. Clonar y Agregar Header (Si existe token)
    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    // 2. Pasar la petición y escuchar errores
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // 3. Manejo de Errores de Seguridad (401/403)
        if (error.status === 401 || error.status === 403) {
          // Si el token expiró o es falso, cerramos sesión forzosamente
          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
}
