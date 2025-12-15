
export interface JustificacionPendiente {
  idJustificacion: number;
  empleadoNombre: string;   // "Juan Pérez"
  rol: string;              // "EMPLEADO"
  fechaJustificar: string;  // "2025-12-10"
  motivo: string;           // "Me duele la panza"
  tipo: 'SALUD' | 'PERSONAL' | 'TRABAJO';
  fechaSolicitud: string;   // ISO DateTime
}


export interface Resolucion {
  estado: 'APROBADO' | 'RECHAZADO';
  comentario: string;      // Comentario del juez
}


export interface ResolucionResponse {
  mensaje: string;
}
