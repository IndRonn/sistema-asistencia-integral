// Tipos estrictos para el Select (Dropdown)
export type JustificationType = 'SALUD' | 'PERSONAL' | 'TRABAJO';

// Payload: Lo que enviamos al Backend
export interface JustificationRequest {
  idAsistencia: number | null; // Null si es falta total (sin registro previo)
  fecha: string;               // "YYYY-MM-DD" (Obligatorio)
  motivo: string;              // Mínimo 10 caracteres
  tipo: JustificationType;     // Enum estricto
}

// Respuesta: Lo que el Backend nos devuelve (201 Created)
export interface JustificationResponse {
  mensaje: string; // "Solicitud enviada correctamente..."
  idJustificacion?: number;
  estado?: string;
}
