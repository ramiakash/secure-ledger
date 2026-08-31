export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  username: string;
  displayName: string;
}

export interface Transaction {
  id: string;
  amount: number;
  currency: string;
  description: string;
  counterpartyIban: string;
  bookedAt: string;
  createdAt: string;
}

export interface CreateTransactionRequest {
  amount: number;
  currency: string;
  description: string;
  counterpartyIban: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface FieldError {
  field: string;
  message: string;
}

export interface ProblemDetail {
  title?: string;
  detail?: string;
  status?: number;
  instance?: string;
  timestamp?: string;
  errors?: FieldError[];
  correlationId?: string;
}
