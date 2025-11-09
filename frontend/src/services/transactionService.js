import api from './api';

const transactionService = {
  getAccountTransactions: async (accountId) => {
    const response = await api.get(`/transactions/account/${accountId}`);
    return response.data;
  },

  getTransactionsByDateRange: async (accountId, startDate, endDate) => {
    const response = await api.get(
      `/transactions/account/${accountId}/date-range`,
      {
        params: { startDate, endDate }
      }
    );
    return response.data;
  },

  getTransactionById: async (id) => {
    const response = await api.get(`/transactions/${id}`);
    return response.data;
  },

  getAllTransactions: async () => {
    const response = await api.get('/transactions');
    return response.data;
  },
};

export default transactionService;
