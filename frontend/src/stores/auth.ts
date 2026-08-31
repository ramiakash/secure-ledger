import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { authApi } from "../api/auth";
import { setAuthToken } from "../api/client";
import type { LoginRequest } from "../types/api";

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(null);
  const username = ref<string | null>(null);
  const displayName = ref<string | null>(null);

  const isAuthenticated = computed(() => token.value !== null);

  async function login(credentials: LoginRequest): Promise<void> {
    const response = await authApi.login(credentials);

    token.value = response.accessToken;
    username.value = response.username;
    displayName.value = response.displayName;

    setAuthToken(response.accessToken);
  }

  function logout(): void {
    token.value = null;
    username.value = null;
    displayName.value = null;

    setAuthToken(null);
  }

  return {
    token,
    username,
    displayName,
    isAuthenticated,
    login,
    logout,
  };
});
