
export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserProfile {
  idUsuario: number;
  username: string;
  nombres: string;
  apellidos: string;
  email: string;
  rol: 'ADMIN' | 'EMPLEADO';
  estado?: string;
}


export interface LoginResponse {
  token: string;
  usuario: UserProfile;
}
