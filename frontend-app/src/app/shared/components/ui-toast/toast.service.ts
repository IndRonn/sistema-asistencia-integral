import { Injectable, signal } from '@angular/core';

// ✅ AGREGAMOS 'warning' AL TIPO
export interface ToastMessage {
  id: number;
  text: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toasts = signal<ToastMessage[]>([]);

  // ✅ ACTUALIZAMOS LA FIRMA DEL MÉTODO
  show(text: string, type: 'success' | 'error' | 'info' | 'warning' = 'info'): void {
    const id = Date.now();
    const newToast: ToastMessage = { id, text, type };

    this.toasts.update(current => [...current, newToast]);

    setTimeout(() => {
      this.remove(id);
    }, 3000); // 3 segundos
  }

  remove(id: number): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }
}
