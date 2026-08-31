<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useTransactionStore } from "../stores/transactions";
import TransactionTable from "../components/TransactionTable.vue";
import DateRangeFilter from "../components/DateRangeFilter.vue";
import CreateTransactionModal from "../components/CreateTransactionModal.vue";

const store = useTransactionStore();
const showModal = ref(false);

function clearFilters(): void {
  store.from = "";
  store.to = "";
  store.applyFilters();
}

function onCreated(): void {
  showModal.value = false;
}

onMounted(() => {
  void store.fetch();
});
</script>

<template>
  <main class="mx-auto max-w-5xl px-4 py-8">
    <h1 class="text-lg font-semibold text-slate-900">Transactions</h1>

    <p
      v-if="store.errorMessage"
      role="alert"
      class="mt-4 rounded bg-red-50 px-3 py-2 text-sm text-red-700"
    >
      {{ store.errorMessage }}
    </p>
    <div class="mt-4 flex justify-end">
      <button
        type="button"
        class="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
        @click="showModal = true"
      >
        New transaction
      </button>
    </div>

    <div class="mt-4">
      <DateRangeFilter
        v-model:from="store.from"
        v-model:to="store.to"
        @apply="store.applyFilters"
        @clear="clearFilters"
      />
    </div>

    <CreateTransactionModal
      v-if="showModal"
      :submit="store.create"
      @close="showModal = false"
      @created="onCreated"
    />
    <div class="mt-6">
      <TransactionTable
        :transactions="store.items"
        :loading="store.loading"
        :sort-field="store.sort"
        :sort-direction="store.direction"
        @sort="store.toggleSort"
      />
    </div>

    <div class="mt-4 flex items-center justify-between text-sm text-slate-600">
      <span>{{ store.totalElements }} transaction(s)</span>

      <div class="flex items-center gap-2">
        <button
          type="button"
          :disabled="store.isFirst"
          class="rounded border border-slate-300 px-3 py-1 disabled:opacity-40"
          @click="store.goToPage(store.page - 1)"
        >
          Previous
        </button>
        <span
          >Page {{ store.page + 1 }} of
          {{ Math.max(store.totalPages, 1) }}</span
        >
        <button
          type="button"
          :disabled="store.isLast"
          class="rounded border border-slate-300 px-3 py-1 disabled:opacity-40"
          @click="store.goToPage(store.page + 1)"
        >
          Next
        </button>
      </div>
    </div>
  </main>
</template>
