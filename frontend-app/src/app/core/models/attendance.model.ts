
export interface DashboardStatus {
  estado: 'SIN_MARCAR' | 'EN_JORNADA' | 'FINALIZADO';
  mensaje: string;
  horaEntrada: string | null;
  horaSalida: string | null;
  esTardanza: boolean;
  horaInicioConfig: string;
  toleranciaMinutos: string;
}

export interface CheckInResponse {
  mensaje: string;
  tipoMarca: 'ENTRADA' | 'SALIDA';
  horaExacta: string;
  estadoAsistencia: string;
}


export interface UIStateConfig {
  color: string;
  borderColor: string;
  label: string;
  actionLabel: string;
}

export interface AttendanceRecord {
  // Identificadores
  idAsistencia: number;
  idUsuario?: number;
  nombreCompleto?: string;
  email?: string;
  fecha: string;
  horaEntrada: string;
  horaSalida: string | null;


  estado: string;
  estadoDescripcion: string;
  esTardanza?: boolean;
  esJustificable?: boolean;


  justificacionEstado?: 'PENDIENTE' | 'APROBADO' | 'RECHAZADO' | null;
  mensajeAdmin?: string | null;
}

export interface HistoryResponse {
  content: AttendanceRecord[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;     // Página actual
  first: boolean;
  last: boolean;
}

