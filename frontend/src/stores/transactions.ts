import { defineStore } from "pinia";
import { ref } from "vue";
import { transactionsApi, type TransactionQuery } from "../api/transactions";
import { ApiError } from "../api/client";
import type { CreateTransactionRequest, Transaction } from "../types/api";

export const useTransactionStore = defineStore("transactions", () => {
  const items = ref<Transaction[]>([]);
  const page = ref(0);
  const size = ref(20);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const isFirst = ref(true);
  const isLast = ref(true);

  const from = ref<string>("");
  const to = ref<string>("");
  const sort = ref<"bookedAt" | "amount">("bookedAt");
  const direction = ref<"asc" | "desc">("desc");

  const loading = ref(false);
  const errorMessage = ref<string | null>(null);

  async function fetch(): Promise<void> {
    loading.value = true;
    errorMessage.value = null;

    const query: TransactionQuery = {
      page: page.value,
      size: size.value,
      sort: sort.value,
      direction: direction.value,
    };
    if (from.value) query.from = from.value;
    if (to.value) query.to = to.value;

    try {
      const result = await transactionsApi.list(query);
      items.value = result.content;
      totalElements.value = result.totalElements;
      totalPages.value = result.totalPages;
      isFirst.value = result.first;
      isLast.value = result.last;
    } catch (error) {
      errorMessage.value =
        error instanceof ApiError
          ? error.message
          : "Unable to load transactions.";
      items.value = [];
    } finally {
      loading.value = false;
    }
  }

  async function create(request: CreateTransactionRequest): Promise<void> {
    await transactionsApi.create(request);
    page.value = 0;
    await fetch();
  }

  function applyFilters(): void {
    page.value = 0;
    void fetch();
  }

  function toggleSort(field: "bookedAt" | "amount"): void {
    if (sort.value === field) {
      direction.value = direction.value === "desc" ? "asc" : "desc";
    } else {
      sort.value = field;
      direction.value = "desc";
    }
    page.value = 0;
    void fetch();
  }

  function goToPage(target: number): void {
    page.value = target;
    void fetch();
  }

  return {
    items,
    page,
    size,
    totalElements,
    totalPages,
    isFirst,
    isLast,
    from,
    to,
    sort,
    direction,
    loading,
    errorMessage,
    fetch,
    create,
    applyFilters,
    toggleSort,
    goToPage,
  };
});
