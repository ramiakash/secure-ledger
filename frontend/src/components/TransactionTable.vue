<script setup lang="ts">
import type { Transaction } from "../types/api";

defineProps<{
  transactions: Transaction[];
  loading: boolean;
  sortField: string;
  sortDirection: string;
}>();

const emit = defineEmits<{
  sort: [field: "bookedAt" | "amount"];
}>();

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString(undefined, {
    year: "numeric",
    month: "short",
    day: "2-digit",
  });
}

function formatAmount(amount: number, currency: string): string {
  return new Intl.NumberFormat(undefined, {
    style: "currency",
    currency,
  }).format(amount);
}

function arrow(
  field: string,
  sortField: string,
  sortDirection: string,
): string {
  if (field !== sortField) return "";
  return sortDirection === "desc" ? " ↓" : " ↑";
}
</script>

<template>
  <div class="bg-white rounded-lg shadow overflow-x-auto">
    <table class="w-full text-sm">
      <thead class="bg-slate-50 text-left text-slate-600">
        <tr>
          <th class="px-4 py-3 font-medium">
            <button
              type="button"
              class="hover:text-slate-900"
              @click="emit('sort', 'bookedAt')"
            >
              Date{{ arrow("bookedAt", sortField, sortDirection) }}
            </button>
          </th>
          <th class="px-4 py-3 font-medium text-right">
            <button
              type="button"
              class="hover:text-slate-900"
              @click="emit('sort', 'amount')"
            >
              Amount{{ arrow("amount", sortField, sortDirection) }}
            </button>
          </th>
          <th class="px-4 py-3 font-medium">Description</th>
          <th class="px-4 py-3 font-medium">Counterparty IBAN</th>
        </tr>
      </thead>

      <tbody class="divide-y divide-slate-100">
        <tr v-if="loading">
          <td colspan="4" class="px-4 py-8 text-center text-slate-400">
            Loading…
          </td>
        </tr>

        <tr v-else-if="transactions.length === 0">
          <td colspan="4" class="px-4 py-8 text-center text-slate-400">
            No transactions found.
          </td>
        </tr>

        <tr v-for="tx in transactions" :key="tx.id" class="hover:bg-slate-50">
          <td class="px-4 py-3 text-slate-700 whitespace-nowrap">
            {{ formatDate(tx.bookedAt) }}
          </td>
          <td
            class="px-4 py-3 text-right font-medium text-slate-900 whitespace-nowrap"
          >
            {{ formatAmount(tx.amount, tx.currency) }}
          </td>
          <td class="px-4 py-3 text-slate-700">{{ tx.description }}</td>
          <td class="px-4 py-3 text-slate-500 font-mono text-xs">
            {{ tx.counterpartyIban }}
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
