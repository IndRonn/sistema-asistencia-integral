import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { authGuard } from '@core/guards/auth.guard';
import { roleGuard } from '@core/guards/role.guard';

const routes: Routes = [
  // 1. Redirección Inicial
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // 2. Auth
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },

  // 3. ZONA EMPLEADO
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

  // 4. ZONA ADMIN
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { role: 'ADMIN' },
    // Cargamos el Layout (Menú Lateral)
    loadComponent: () => import('./features/admin/admin-layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      { path: '', redirectTo: 'overview', pathMatch: 'full' },

      {
        path: 'overview',
        loadComponent: () => import('./features/admin/pages/admin-overview/admin-overview.component').then(m => m.AdminOverviewComponent)
      },

      {
        path: 'usuarios',
        loadComponent: () => import('./features/admin/pages/users/users.component').then(m => m.UsersComponent)
      },

      {
        path: 'configuracion',
        loadComponent: () => import('./features/admin/pages/settings/settings.component').then(m => m.SettingsComponent)
      },

      {
        path: 'reportes',
        loadComponent: () => import('./features/admin/pages/reports/reports.component').then(m => m.ReportsComponent)
      }
    ]
  },

  { path: '**', redirectTo: 'auth/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
