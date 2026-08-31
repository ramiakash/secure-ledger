<script setup lang="ts">
const from = defineModel<string>("from", { required: true });
const to = defineModel<string>("to", { required: true });

const emit = defineEmits<{
  apply: [];
  clear: [];
}>();
</script>

<template>
  <div class="bg-white rounded-lg shadow p-4 flex flex-wrap items-end gap-4">
    <div>
      <label for="from" class="block text-xs font-medium text-slate-600"
        >From</label
      >
      <input
        id="from"
        v-model="from"
        type="date"
        class="mt-1 rounded border border-slate-300 px-3 py-1.5 text-sm"
      />
    </div>

    <div>
      <label for="to" class="block text-xs font-medium text-slate-600"
        >To</label
      >
      <input
        id="to"
        v-model="to"
        type="date"
        class="mt-1 rounded border border-slate-300 px-3 py-1.5 text-sm"
      />
    </div>

    <button
      type="button"
      class="rounded bg-slate-900 px-4 py-1.5 text-sm font-medium text-white hover:bg-slate-800"
      @click="emit('apply')"
    >
      Apply
    </button>

    <button
      type="button"
      class="rounded border border-slate-300 px-4 py-1.5 text-sm text-slate-600 hover:bg-slate-50"
      @click="emit('clear')"
    >
      Clear
    </button>
  </div>
</template>
