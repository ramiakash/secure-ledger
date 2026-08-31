<script setup lang="ts">
import { useRouter } from "vue-router";
import { useAuthStore } from "./stores/auth";

const router = useRouter();
const auth = useAuthStore();

async function handleLogout(): Promise<void> {
  auth.logout();
  await router.push("/login");
}
</script>

<template>
  <div class="min-h-screen bg-slate-100">
    <header
      v-if="auth.isAuthenticated"
      class="bg-white border-b border-slate-200"
    >
      <div
        class="mx-auto max-w-5xl px-4 py-3 flex items-center justify-between"
      >
        <span class="font-semibold text-slate-900">Secure Ledger</span>

        <div class="flex items-center gap-4">
          <span class="text-sm text-slate-600">{{ auth.displayName }}</span>
          <button
            type="button"
            class="text-sm text-slate-500 hover:text-slate-900"
            @click="handleLogout"
          >
            Sign out
          </button>
        </div>
      </div>
    </header>

    <RouterView />
  </div>
</template>
