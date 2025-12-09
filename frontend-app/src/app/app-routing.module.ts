import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard'; // Nuestro Guardián

const routes: Routes = [
  // Redirección inicial
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // Ruta Pública: Login (Carga Lazy)
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // 🛡️ RUTAS PROTEGIDAS (Las crearemos en Fase 2, pero dejamos la estructura)
  {
    path: 'dashboard',
    canActivate: [authGuard],
    // Temporalmente redirige al login o carga un componente placeholder si ya lo creaste
    loadComponent: () => import('./features/dashboard/pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'admin/overview',
    canActivate: [authGuard],
    loadComponent: () => import('./features/admin/pages/admin-overview/admin-overview.component').then(m => m.AdminOverviewComponent)
  },

  // Wildcard: Cualquier ruta desconocida -> Login
  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
