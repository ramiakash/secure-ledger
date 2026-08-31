import axios, { AxiosError } from "axios";
import type { ProblemDetail } from "../types/api";

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080",
  headers: { "Content-Type": "application/json" },
});

let authToken: string | null = null;

export function setAuthToken(token: string | null): void {
  authToken = token;
}

client.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

export class ApiError extends Error {
  readonly status: number;
  readonly problem?: ProblemDetail;

  constructor(status: number, message: string, problem?: ProblemDetail) {
    super(message);
    this.status = status;
    this.problem = problem;
  }

  get fieldErrors(): Record<string, string> {
    const result: Record<string, string> = {};
    for (const error of this.problem?.errors ?? []) {
      result[error.field] = error.message;
    }
    return result;
  }
}

client.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ProblemDetail>) => {
    if (!error.response) {
      return Promise.reject(
        new ApiError(0, "Cannot reach the server. Is the backend running?"),
      );
    }

    const status = error.response.status;
    const problem = error.response.data;

    const message =
      problem?.detail ??
      problem?.title ??
      (status === 401
        ? "Your session has expired. Please sign in again."
        : "Something went wrong. Please try again.");

    return Promise.reject(new ApiError(status, message, problem));
  },
);

export default client;
