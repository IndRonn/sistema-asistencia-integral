import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  id: number;
  text: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({
  providedIn: 'root' // Singleton global disponible en toda la app
})
export class ToastService {
  // Signal: La fuente de verdad reactiva
  toasts = signal<ToastMessage[]>([]);

  /**
   * Muestra una notificación flotante.
   * @param text Mensaje a mostrar
   * @param type Tipo de alerta (determina el color)
   */
  show(text: string, type: 'success' | 'error' | 'info' = 'info'): void {
    const id = Date.now();
    const newToast: ToastMessage = { id, text, type };

    // Agregamos inmutablemente al array
    this.toasts.update(current => [...current, newToast]);

    // Auto-destrucción a los 3 segundos (Lógica de limpieza)
    setTimeout(() => {
      this.remove(id);
    }, 3000);
  }

  /**
   * Elimina una notificación específica (usado por el botón X o el timer)
   */
  remove(id: number): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}
