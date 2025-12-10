import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard'; // Alias @core

const routes: Routes = [
  // 1. Redirección inicial (Raíz -> Login)
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // 2. Ruta Pública: Login
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // 3. 🛡️ ZONA EMPLEADO (Dashboard)
  {
    path: 'dashboard',
    canActivate: [authGuard], // Protegido por guardián
    children: [
      {
        path: '', // URL: /dashboard
        loadComponent: () => import('./features/dashboard/pages/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'historial', // URL: /dashboard/historial (¡ESTA ES LA QUE FALTABA!)
        loadComponent: () => import('./features/dashboard/pages/history/history.component').then(m => m.HistoryComponent)
      }
    ]
  },

  // 4. 🛡️ ZONA ADMIN (God Mode)
  {
    path: 'admin',
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      {
        path: 'overview',
        loadComponent: () => import('./features/admin/pages/admin-overview/admin-overview.component').then(m => m.AdminOverviewComponent)
      }
      // Aquí agregaremos más rutas de admin luego
    ]
  },

  // 5. Wildcard: Cualquier ruta desconocida te manda al Login
  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
