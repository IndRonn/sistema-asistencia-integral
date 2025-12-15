export interface KpiResumen {
  totalEmpleados: number;
  presentes: number;
  ausentes: number;
  puntuales: number;
  tardanzas: number;
  tasaAsistencia: number;
}

export interface MonitorVivo {
  idUsuario: number;
  nombreCompleto: string;
  horaEntrada: string;
  ip: string;
  dispositivo: string;
}

export interface HistoricoSemana {
  dia: string;
  fecha: string;
  total: number;
  puntuales: number;
  tardanzas: number;
  faltas: number;
}
