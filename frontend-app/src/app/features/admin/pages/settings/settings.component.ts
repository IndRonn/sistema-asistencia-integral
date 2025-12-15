import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ConfigService } from '@core/services/config/config.service';
import { ToastService } from '@shared/components/ui-toast/toast.service';
import { Configuracion, ConfigChange } from '@core/models/config.model';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.css']
})
export class SettingsComponent implements OnInit {
  private configService = inject(ConfigService);
  private toast = inject(ToastService);
  private fb = inject(FormBuilder);

  isLoading = signal(true);
  isSaving = signal(false);

  form: FormGroup = this.fb.group({});
  configs = signal<Configuracion[]>([]);

  ngOnInit() {
    this.loadConfigs();
  }

  loadConfigs() {
    this.isLoading.set(true);
    this.configService.getConfigs().subscribe({
      next: (data) => {
        this.configs.set(data);

        // Generar formulario dinámico
        data.forEach(cfg => {
          const validators = [Validators.required];
          if (cfg.clave === 'HORA_ENTRADA') validators.push(Validators.pattern(/^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/));
          if (cfg.clave === 'TOLERANCIA_MINUTOS') validators.push(Validators.pattern(/^[0-9]+$/));

          this.form.addControl(cfg.clave, this.fb.control(cfg.valor, validators));
        });

        this.isLoading.set(false);
      },
      error: () => {
        this.toast.show('Error cargando configuración', 'error');
        this.isLoading.set(false);
      }
    });
  }

  save() {
    if (this.form.invalid) {
      this.toast.show('Formato inválido (Revise HH:mm o Números)', 'warning');
      return;
    }

    this.isSaving.set(true);

    // Convertir Formulario a Array de cambios
    const cambios: ConfigChange[] = Object.keys(this.form.value).map(key => ({
      clave: key,
      valor: this.form.value[key]
    }));

    this.configService.updateConfigs(cambios).subscribe({
      next: () => {
        this.toast.show('Configuración actualizada y Caché refrescada', 'success');
        this.isSaving.set(false);
      },
      error: () => {
        this.toast.show('Error al guardar configuración', 'error');
        this.isSaving.set(false);
      }
    });
  }
}
