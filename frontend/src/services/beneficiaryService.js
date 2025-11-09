import api from './api';

const beneficiaryService = {
  addBeneficiary: async (customerId, beneficiaryData) => {
    const response = await api.post(`/beneficiaries/customer/${customerId}`, beneficiaryData);
    return response.data;
  },

  getBeneficiaries: async (customerId) => {
    const response = await api.get(`/beneficiaries/customer/${customerId}`);
    return response.data;
  },

  getCustomerBeneficiaries: async (customerId) => {
    const response = await api.get(`/beneficiaries/customer/${customerId}`);
    return response.data;
  },

  getBeneficiaryById: async (id) => {
    const response = await api.get(`/beneficiaries/${id}`);
    return response.data;
  },

  updateBeneficiary: async (id, beneficiaryData) => {
    const response = await api.put(`/beneficiaries/${id}`, beneficiaryData);
    return response.data;
  },

  verifyBeneficiary: async (id) => {
    const response = await api.put(`/beneficiaries/${id}/verify`);
    return response.data;
  },

  deleteBeneficiary: async (id) => {
    const response = await api.delete(`/beneficiaries/${id}`);
    return response.data;
  },
};

export default beneficiaryService;
