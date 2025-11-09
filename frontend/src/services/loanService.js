import api from './api';

const loanService = {
  applyForLoan: async (customerId, loanData) => {
    const response = await api.post(`/loans/customer/${customerId}`, loanData);
    return response.data;
  },

  createLoan: async (loanData) => {
    const response = await api.post('/loans', loanData);
    return response.data;
  },

  getLoanById: async (id) => {
    const response = await api.get(`/loans/${id}`);
    return response.data;
  },

  getCustomerLoans: async (customerId) => {
    const response = await api.get(`/loans/customer/${customerId}`);
    return response.data;
  },

  getAllLoans: async () => {
    const response = await api.get('/loans');
    return response.data;
  },

  updateLoanStatus: async (id, status) => {
    const response = await api.put(`/loans/${id}/status`, { status });
    return response.data;
  },

  deleteLoan: async (id) => {
    const response = await api.delete(`/loans/${id}`);
    return response.data;
  },
};

export default loanService;
