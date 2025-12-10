export interface DashboardStatus {
  estado: 'SIN_MARCAR' | 'EN_JORNADA' | 'FINALIZADO';
  mensaje: string;
  horaEntrada: string | null;
  horaSalida: string | null;
  esTardanza: boolean;
  // ✅ NUEVOS CAMPOS (Información de Reglas)
  horaInicioConfig: string;     // "08:00"
  toleranciaMinutos: string;    // "15"
}

export interface UIStateConfig {
  color: string;
  borderColor: string;
  label: string;
  actionLabel: string;
}

export interface CheckInResponse {
  mensaje: string;
  tipoMarca: 'ENTRADA' | 'SALIDA';
  horaExacta: string;
  estadoAsistencia: string;
}

// Para el Historial (Hito 2.3)
export interface AttendanceRecord {
  id: number;
  fecha: string;
  horaEntrada: string;
  horaSalida: string | null;
  estado: string;
  esJustificable: boolean;
}

export interface HistoryResponse {
  content: AttendanceRecord[];
  totalElements: number;
  totalPages: number;
}

export interface AttendanceRecord {
  idAsistencia: number;
  idUsuario: number;
  nombreCompleto: string;
  email: string;
  fecha: string;            // "2025-12-10"
  horaEntrada: string;      // "04:56:31"
  horaSalida: string | null;
  estado: string;           // "P"
  estadoTexto: string;      // "PUNTUAL"
  esTardanza: boolean;
  esJustificable: boolean;  // ✅ Ahora viene directo del back
}

export interface HistoryResponse {
  content: AttendanceRecord[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
