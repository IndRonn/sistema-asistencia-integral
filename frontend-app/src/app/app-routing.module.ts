import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard'; // ✅ Importar RoleGuard

const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // ZONA EMPLEADO (Solo requiere estar logueado)
  {
    path: 'dashboard',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/dashboard/pages/home/home.component').then(m => m.HomeComponent)
      },
      {
        path: 'historial',
        loadComponent: () => import('./features/dashboard/pages/history/history.component').then(m => m.HistoryComponent)
      }
    ]
  },

  // 🛡️ ZONA ADMIN (God Mode)
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard], // ✅ DOBLE SEGURIDAD: Token + Rol
    data: { role: 'ADMIN' },             // ✅ REQUISITO: Debe ser ADMIN
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },
      {
        path: 'overview',
        loadComponent: () => import('./features/admin/pages/admin-overview/admin-overview.component').then(m => m.AdminOverviewComponent)
      },
      // Más adelante: /admin/usuarios, /admin/reportes, etc.
    ]
  },

  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
