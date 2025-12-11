import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '@core/services/auth/auth.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastService);

  // 1. ¿Qué rol exige esta ruta? (Lo definiremos en el routing module)
  const expectedRole = route.data['role'];

  // 2. ¿Qué rol tiene el usuario?
  const currentRole = authService.currentRole();

  // Validación básica de autenticación (Redundante si usas authGuard antes, pero seguro)
  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login']);
    return false;
  }

  // 3. LA GRAN COMPARACIÓN
  if (currentRole === expectedRole) {
    return true; // Pase usted, Jefe.
  }

  // 4. ACCESO DENEGADO
  console.warn(`Intrusión detectada. Rol requerido: ${expectedRole}, Rol actual: ${currentRole}`);
  toast.show('⛔ Acceso restringido. No tienes los permisos necesarios.', 'error');

  // Redirigir a zona segura (Dashboard de empleado)
  router.navigate(['/dashboard']);
  return false;
};
