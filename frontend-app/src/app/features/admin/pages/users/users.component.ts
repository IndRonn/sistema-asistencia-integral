import { Component, OnInit, ViewChild, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { UserService } from '@core/services/user/user.service';
import { Usuario } from '@core/models/user.model';
import { UiSwitchComponent } from '@shared/components/ui-switch/ui-switch.component';
import { UiBadgeComponent } from '@shared/components/ui-badge/ui-badge.component';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { UserModalComponent } from '../../components/user-modal/user-modal.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, UiSwitchComponent, UiBadgeComponent, UserModalComponent],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent implements OnInit {
  private userService = inject(UserService);
  private toast = inject(ToastService);

  @ViewChild(UserModalComponent) modal!: UserModalComponent;

  // Estados
  users = signal<Usuario[]>([]);
  isLoading = signal(true);

  // Paginación
  currentPage = signal(0);
  totalElements = signal(0);

  // Buscador
  searchControl = new FormControl('');

  ngOnInit() {
    this.loadUsers();

    // Configuración del buscador
    this.searchControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(value => {
      this.currentPage.set(0);
      this.loadUsers(value || '');
    });
  }

  loadUsers(search: string = this.searchControl.value || '') {
    this.isLoading.set(true);
    this.userService.getAll(this.currentPage(), 10, search).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalElements.set(res.totalElements);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  // Lógica del Switch (Bloquear/Desbloquear)
  onToggleStatus(user: Usuario) {
    const newState = user.estado === 'A' ? 'I' : 'A';

    this.userService.toggleStatus(user.idUsuario, newState).subscribe({
      next: () => {
        user.estado = newState;
        const msg = newState === 'A' ? 'Usuario Reactivado' : 'Usuario Bloqueado';
        this.toast.show(msg, newState === 'A' ? 'success' : 'warning');
      },
      error: () => {
        this.toast.show('Error al cambiar estado', 'error');
        this.loadUsers();
      }
    });
  }

  openCreateModal() {
    this.modal.open();
  }
}
