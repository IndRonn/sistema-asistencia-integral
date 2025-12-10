export interface DashboardStatus {
  estado: 'SIN_MARCAR' | 'EN_JORNADA' | 'FINALIZADO';
  mensaje: string;
  horaEntrada: string | null; // "HH:mm:ss"
  horaSalida: string | null;
  esTardanza: boolean;
}

// Tipado para la UI (Colores y Etiquetas)
export interface UIStateConfig {
  color: string;
  borderColor: string;
  label: string;
  actionLabel: string; // "INICIAR JORNADA" vs "TERMINAR JORNADA"
}
