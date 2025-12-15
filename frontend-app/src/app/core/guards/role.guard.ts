import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '@core/services/auth/auth.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const toast = inject(ToastService);

  const expectedRole = route.data['role'];


  const currentRole = authService.currentRole();

  if (!authService.isAuthenticated()) {
    router.navigate(['/auth/login']);
    return false;
  }


  if (currentRole === expectedRole) {
    return true; // Pase usted, Jefe.
  }

  console.warn(`Intrusión detectada. Rol requerido: ${expectedRole}, Rol actual: ${currentRole}`);
  toast.show('Acceso restringido. No tienes los permisos necesarios.', 'error');


  router.navigate(['/dashboard']);
  return false;
};
