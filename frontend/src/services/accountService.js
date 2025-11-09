import api from './api';

const accountService = {
  createAccount: async (customerId, accountType) => {
    const response = await api.post(`/accounts/customer/${customerId}`, {
      accountType: accountType
    });
    return response.data;
  },

  getAccount: async (accountNumber) => {
    const response = await api.get(`/accounts/${accountNumber}`);
    return response.data;
  },

  getCustomerAccounts: async (customerId) => {
    const response = await api.get(`/accounts/customer/${customerId}`);
    return response.data;
  },

  getAccountWithCustomer: async (accountNumber) => {
    const response = await api.get(`/accounts/${accountNumber}/with-customer`);
    return response.data;
  },

  getBalance: async (accountNumber) => {
    const response = await api.get(`/accounts/${accountNumber}/balance`);
    return response.data;
  },

  deposit: async (accountNumber, amount, description) => {
    const response = await api.post(`/accounts/${accountNumber}/deposit`, {
      amount,
      description,
    });
    return response.data;
  },

  withdraw: async (accountNumber, amount, description) => {
    const response = await api.post(`/accounts/${accountNumber}/withdraw`, {
      amount,
      description,
    });
    return response.data;
  },

  transfer: async (fromAccount, toAccount, amount, description) => {
    const response = await api.post('/accounts/transfer', {
      fromAccount,
      toAccount,
      amount,
      description,
    });
    return response.data;
  },
};

export default accountService;
