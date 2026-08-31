import client from "./client";
import type { LoginRequest, LoginResponse } from "../types/api";

export const authApi = {
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    const { data } = await client.post<LoginResponse>(
      "/api/v1/auth/login",
      credentials,
    );
    return data;
  },
};
