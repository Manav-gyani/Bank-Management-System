import { useSelector } from 'react-redux';

const useAuth = () => {
  const { user, isAuthenticated, loading } = useSelector((state) => state.auth);

  return {
    user,
    isAuthenticated,
    loading,
    isCustomer: user?.roles?.includes('CUSTOMER'),
    isEmployee: user?.roles?.includes('EMPLOYEE'),
    isAdmin: user?.roles?.includes('ADMIN'),
  };
};

export default useAuth;