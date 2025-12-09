export interface LoginRequest {
  username: string;
  password: string;
}

export interface UserProfile {
  id: number;
  username: string;
  nombreCompleto: string;
  email: string;
  rol: 'ADMIN' | 'EMPLEADO';
}

export interface AuthResponse {
  token: string;
  type: string; // "Bearer"
  usuario: UserProfile;
}
