import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '@core/services/auth/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // 1. Verificar si está logueado (usando el Signal computed)
  if (authService.isAuthenticated()) {
    return true; // Acceso concedido
  }

  // 2. Si no, redirigir al Login
  // Opción Slytherin: Sin mensajes, solo redirección fría.
  router.navigate(['/auth/login']);
  return false;
};
