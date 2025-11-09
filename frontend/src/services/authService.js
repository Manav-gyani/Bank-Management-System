import api from './api';

const authService = {
  login: async (username, password) => {
    const response = await api.post('/auth/login', { username, password });
    if (response.data.accessToken) {
      localStorage.setItem('token', response.data.accessToken);

      // Store user data with customerId and userId from login response
      const userData = {
        id: response.data.userId, // Store user ID
        username: response.data.username,
        email: response.data.email,
        accessToken: response.data.accessToken,
        customerId: response.data.customerId // Get customerId directly from login response
      };
      localStorage.setItem('user', JSON.stringify(userData));
      
      // Store customerId separately for easy access
      if (response.data.customerId) {
        localStorage.setItem('customerId', response.data.customerId);
        console.log('✅ Customer ID stored:', response.data.customerId);
      } else {
        console.warn('⚠️ No customerId in login response');
      }

      return userData;
    }
    return response.data;
  },

  register: async (userData) => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('customerId');
  },

  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  getCustomerId: () => {
    // Try to get from localStorage first
    let customerId = localStorage.getItem('customerId');
    
    // If not found, try to get from user object
    if (!customerId) {
      const user = localStorage.getItem('user');
      if (user) {
        try {
          const userData = JSON.parse(user);
          customerId = userData.customerId;
          // Store it for future use
          if (customerId) {
            localStorage.setItem('customerId', customerId);
          }
        } catch (e) {
          console.error('Error parsing user data:', e);
        }
      }
    }
    
    return customerId;
  },

  getToken: () => {
    return localStorage.getItem('token');
  },

  checkUsernameAvailability: async (username) => {
    const response = await api.get(`/auth/check-username?username=${username}`);
    return response.data;
  },

  checkEmailAvailability: async (email) => {
    const response = await api.get(`/auth/check-email?email=${email}`);
    return response.data;
  },
};

export default authService;
