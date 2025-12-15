export interface ReporteAsistencia {
  username: string;
  nombreCompleto: string;
  fecha: string;
  horaEntrada: string;
  horaSalida: string | null;
  estado: string;
  estadoDescripcion: string; // "PUNTUAL", "TARDANZA", "JUSTIFICADO"
  justificacion: string | null;
}

export interface FiltrosReporte {
  inicio: string;
  fin: string;
  idEmpleado?: number;
}
