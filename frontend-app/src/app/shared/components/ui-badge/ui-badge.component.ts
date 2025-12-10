import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

// ✅ SOLUCIÓN AL ERROR: Exportamos el tipo explícitamente
export type BadgeVariant = 'success' | 'warning' | 'danger' | 'neutral';

@Component({
  selector: 'app-ui-badge',
  standalone: true, // Es Standalone, no necesita module
  imports: [CommonModule],
  template: `
    <span class="inline-flex items-center px-2.5 py-0.5 rounded text-[10px] font-bold border uppercase tracking-widest shadow-sm transition-colors duration-200"
          [ngClass]="classes">
      <ng-content></ng-content> </span>
  `
})
export class UiBadgeComponent {
  // Recibimos la variante como input (default: neutral)
  @Input() variant: BadgeVariant = 'neutral';

  // Calculamos las clases de Tailwind según la variante
  get classes() {
    switch (this.variant) {
      case 'success': // PUNTUAL (Verde)
        return 'bg-sys-primary/10 text-sys-primary border-sys-primary';

      case 'warning': // TARDANZA (Amarillo/Ocre)
        return 'bg-sys-warning/10 text-sys-warning border-sys-warning';

      case 'danger':  // FALTA (Vino)
        return 'bg-sys-alert/10 text-sys-alert border-sys-alert';

      case 'neutral': // DEFAULT (Gris)
      default:
        return 'bg-sys-dim text-sys-silver border-gray-600';
    }
  }
}
