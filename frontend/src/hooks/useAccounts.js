import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchCustomerAccounts } from '../store/slices/accountSlice';

const useAccounts = (customerId) => {
  const dispatch = useDispatch();
  const { accounts, loading, error } = useSelector((state) => state.accounts);

  useEffect(() => {
    if (customerId) {
      dispatch(fetchCustomerAccounts(customerId));
    }
  }, [customerId, dispatch]);

  return { accounts, loading, error };
};

export default useAccounts;