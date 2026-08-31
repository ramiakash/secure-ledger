import client from "./client";
import type {
  CreateTransactionRequest,
  PageResponse,
  Transaction,
} from "../types/api";

export interface TransactionQuery {
  from?: string;
  to?: string;
  page?: number;
  size?: number;
  sort?: "bookedAt" | "amount" | "createdAt";
  direction?: "asc" | "desc";
}

export const transactionsApi = {
  async list(query: TransactionQuery = {}): Promise<PageResponse<Transaction>> {
    const { data } = await client.get<PageResponse<Transaction>>(
      "/api/v1/transactions",
      { params: query },
    );
    return data;
  },

  async create(request: CreateTransactionRequest): Promise<Transaction> {
    const { data } = await client.post<Transaction>(
      "/api/v1/transactions",
      request,
    );
    return data;
  },
};
