<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { ApiError } from "../api/client";

const router = useRouter();
const auth = useAuthStore();

const username = ref("");
const password = ref("");
const errorMessage = ref<string | null>(null);
const submitting = ref(false);

async function handleSubmit(): Promise<void> {
  errorMessage.value = null;
  submitting.value = true;

  try {
    await auth.login({ username: username.value, password: password.value });
    await router.push("/");
  } catch (error) {
    errorMessage.value =
      error instanceof ApiError
        ? error.message
        : "Unable to sign in. Please try again.";
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-slate-100 px-4">
    <div class="w-full max-w-sm bg-white rounded-lg shadow p-8">
      <h1 class="text-xl font-semibold text-slate-900">Secure Ledger</h1>
      <p class="mt-1 text-sm text-slate-500">
        Sign in to view your transactions.
      </p>

      <form class="mt-6 space-y-4" @submit.prevent="handleSubmit">
        <div>
          <label
            for="username"
            class="block text-sm font-medium text-slate-700"
          >
            Username
          </label>
          <input
            id="username"
            v-model="username"
            type="text"
            autocomplete="username"
            required
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
          />
        </div>

        <div>
          <label
            for="password"
            class="block text-sm font-medium text-slate-700"
          >
            Password
          </label>
          <input
            id="password"
            v-model="password"
            type="password"
            autocomplete="current-password"
            required
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm focus:border-slate-500 focus:outline-none"
          />
        </div>

        <p
          v-if="errorMessage"
          role="alert"
          class="rounded bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          {{ errorMessage }}
        </p>

        <button
          type="submit"
          :disabled="submitting"
          class="w-full rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-50"
        >
          {{ submitting ? "Signing in…" : "Sign in" }}
        </button>
      </form>

      <p class="mt-6 text-xs text-slate-400">
        Demo accounts: alice / bob — password <code>Password123!</code>
      </p>
    </div>
  </div>
</template>
