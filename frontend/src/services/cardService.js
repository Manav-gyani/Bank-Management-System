import api from './api';

const cardService = {
  applyForCard: async (accountNumber, cardType) => {
    const response = await api.post(`/cards/account/${accountNumber}`, { cardType });
    return response.data;
  },

  createCard: async (cardData) => {
    const response = await api.post('/cards', cardData);
    return response.data;
  },

  getCardById: async (id) => {
    const response = await api.get(`/cards/${id}`);
    return response.data;
  },

  getCustomerCards: async (customerId) => {
    const response = await api.get(`/cards/customer/${customerId}`);
    return response.data;
  },

  getAllCards: async () => {
    const response = await api.get('/cards');
    return response.data;
  },

  blockCard: async (id) => {
    const response = await api.put(`/cards/${id}/block`);
    return response.data;
  },

  activateCard: async (id) => {
    const response = await api.put(`/cards/${id}/activate`);
    return response.data;
  },

  deleteCard: async (id) => {
    const response = await api.delete(`/cards/${id}`);
    return response.data;
  },
};

export default cardService;