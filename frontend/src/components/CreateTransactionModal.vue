<script setup lang="ts">
import { ref, reactive } from "vue";
import { ApiError } from "../api/client";
import type { CreateTransactionRequest } from "../types/api";

const emit = defineEmits<{
  close: [];
  created: [];
}>();

const props = defineProps<{
  submit: (request: CreateTransactionRequest) => Promise<void>;
}>();

const form = reactive({
  amount: "",
  currency: "EUR",
  description: "",
  counterpartyIban: "",
});

const errors = reactive<Record<string, string>>({});
const formError = ref<string | null>(null);
const submitting = ref(false);

function validate(): boolean {
  Object.keys(errors).forEach((key) => delete errors[key]);

  const amount = Number(form.amount);
  if (!form.amount || Number.isNaN(amount)) {
    errors.amount = "Amount is required";
  } else if (amount <= 0) {
    errors.amount = "Amount must be positive";
  }

  if (!/^[A-Za-z]{3}$/.test(form.currency)) {
    errors.currency = "Currency must be a 3-letter code";
  }

  if (!form.description.trim()) {
    errors.description = "Description is required";
  } else if (form.description.length > 255) {
    errors.description = "Description must not exceed 255 characters";
  }

  const iban = form.counterpartyIban.replace(/\s/g, "").toUpperCase();
  if (!iban) {
    errors.counterpartyIban = "IBAN is required";
  } else if (!/^[A-Z]{2}[0-9]{2}[A-Z0-9]{11,30}$/.test(iban)) {
    errors.counterpartyIban = "IBAN format is not valid";
  }

  return Object.keys(errors).length === 0;
}

async function handleSubmit(): Promise<void> {
  formError.value = null;
  if (!validate()) return;

  submitting.value = true;
  try {
    await props.submit({
      amount: Number(form.amount),
      currency: form.currency.toUpperCase(),
      description: form.description.trim(),
      counterpartyIban: form.counterpartyIban.replace(/\s/g, "").toUpperCase(),
    });
    emit("created");
  } catch (error) {
    if (error instanceof ApiError && error.status === 422) {
      Object.assign(errors, error.fieldErrors);
    } else if (error instanceof ApiError) {
      formError.value = error.message;
    } else {
      formError.value = "Unable to record the transaction.";
    }
  } finally {
    submitting.value = false;
  }
}
</script>

<template>
  <div
    class="fixed inset-0 bg-black/40 flex items-center justify-center px-4 z-50"
  >
    <div class="w-full max-w-md bg-white rounded-lg shadow-lg p-6">
      <h2 class="text-lg font-semibold text-slate-900">New transaction</h2>

      <form class="mt-4 space-y-4" @submit.prevent="handleSubmit">
        <div>
          <label for="amount" class="block text-sm font-medium text-slate-700"
            >Amount</label
          >
          <input
            id="amount"
            v-model="form.amount"
            type="number"
            step="0.0001"
            min="0"
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm"
          />
          <p v-if="errors.amount" class="mt-1 text-xs text-red-600">
            {{ errors.amount }}
          </p>
        </div>

        <div>
          <label for="currency" class="block text-sm font-medium text-slate-700"
            >Currency</label
          >
          <input
            id="currency"
            v-model="form.currency"
            type="text"
            maxlength="3"
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm uppercase"
          />
          <p v-if="errors.currency" class="mt-1 text-xs text-red-600">
            {{ errors.currency }}
          </p>
        </div>

        <div>
          <label
            for="description"
            class="block text-sm font-medium text-slate-700"
          >
            Description
          </label>
          <input
            id="description"
            v-model="form.description"
            type="text"
            maxlength="255"
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm"
          />
          <p v-if="errors.description" class="mt-1 text-xs text-red-600">
            {{ errors.description }}
          </p>
        </div>

        <div>
          <label for="iban" class="block text-sm font-medium text-slate-700">
            Counterparty IBAN
          </label>
          <input
            id="iban"
            v-model="form.counterpartyIban"
            type="text"
            class="mt-1 w-full rounded border border-slate-300 px-3 py-2 text-sm font-mono"
          />
          <p v-if="errors.counterpartyIban" class="mt-1 text-xs text-red-600">
            {{ errors.counterpartyIban }}
          </p>
        </div>

        <p
          v-if="formError"
          role="alert"
          class="rounded bg-red-50 px-3 py-2 text-sm text-red-700"
        >
          {{ formError }}
        </p>

        <div class="flex justify-end gap-3 pt-2">
          <button
            type="button"
            class="rounded border border-slate-300 px-4 py-2 text-sm text-slate-600"
            @click="emit('close')"
          >
            Cancel
          </button>
          <button
            type="submit"
            :disabled="submitting"
            class="rounded bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800 disabled:opacity-50"
          >
            {{ submitting ? "Saving…" : "Record transaction" }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
