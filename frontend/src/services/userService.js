import api from './api';

const userService = {
  getCurrentUser: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },

  getCurrentProfile: async () => {
    const response = await api.get('/users/me/profile');
    return response.data;
  },

  getUserById: async (id) => {
    const response = await api.get(`/users/${id}`);
    return response.data;
  },

  getAllUsers: async () => {
    const response = await api.get('/users');
    return response.data;
  },

  updateUser: async (id, userData) => {
    const response = await api.put(`/users/${id}`, userData);
    return response.data;
  },

  updateProfile: async (id, profileData) => {
    const response = await api.put(`/users/${id}/profile`, profileData);
    return response.data;
  },

  changePassword: async (id, passwordData) => {
    const response = await api.put(`/users/${id}/change-password`, passwordData);
    return response.data;
  },

  deleteUser: async (id) => {
    const response = await api.delete(`/users/${id}`);
    return response.data;
  },
};

export default userService;
