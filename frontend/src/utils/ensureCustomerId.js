import authService from '../services/authService';
import userService from '../services/userService';

/**
 * Ensures customerId is available in localStorage
 * If not found, fetches it from the backend
 */
export const ensureCustomerId = async () => {
  let customerId = authService.getCustomerId();
  
  if (!customerId) {
    console.log('⚠️ CustomerId not found in localStorage, fetching from backend...');
    try {
      const profile = await userService.getCurrentProfile();
      if (profile.customerId) {
        localStorage.setItem('customerId', profile.customerId);
        
        // Also update user object
        const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
        currentUser.customerId = profile.customerId;
        localStorage.setItem('user', JSON.stringify(currentUser));
        
        console.log('✅ CustomerId fetched and stored:', profile.customerId);
        customerId = profile.customerId;
      } else {
        console.error('❌ No customerId in profile response');
      }
    } catch (error) {
      console.error('❌ Failed to fetch customerId:', error);
      throw new Error('Unable to fetch customer ID. Please log in again.');
    }
  }
  
  return customerId;
};
