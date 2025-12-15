export interface Usuario {
  idUsuario: number;
  username: string;
  nombres: string;
  apellidos: string;
  email: string;
  rol: 'ADMIN' | 'EMPLEADO';
  estado: 'A' | 'I'; // Activo / Inactivo
}

export interface UsuarioRequest {
  username: string;
  password?: string;
  nombres: string;
  apellidos: string;
  email: string;
  rol: 'ADMIN' | 'EMPLEADO';
}

export interface UserResponse {
  content: Usuario[];
  totalElements: number;
  totalPages: number;
}
