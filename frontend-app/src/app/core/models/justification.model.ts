
export type JustificationType = 'SALUD' | 'PERSONAL' | 'TRABAJO';


export interface JustificationRequest {
  idAsistencia: number | null;
  fecha: string;
  motivo: string;
  tipo: JustificationType;
}

export interface JustificationResponse {
  mensaje: string;
  idJustificacion?: number;
  estado?: string;
}
