import React, { useState, useEffect } from 'react';
import { ArrowRight, RefreshCw } from 'lucide-react';
import accountService from '../services/accountService';
import authService from '../services/authService';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

const Transfer = () => {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [processing, setProcessing] = useState(false);
  const [formData, setFormData] = useState({
    fromAccount: '',
    toAccount: '',
    amount: '',
    description: '',
  });

  useEffect(() => {
    fetchAccounts();
  }, []);

  const fetchAccounts = async () => {
    try {
      setLoading(true);
      const customerId = authService.getCustomerId();

      if (!customerId) {
        toast.error('Customer ID not found');
        return;
      }

      const accountsData = await accountService.getCustomerAccounts(customerId);
      console.log('Accounts for transfer:', accountsData);
      setAccounts(accountsData);
    } catch (error) {
      console.error('Error fetching accounts:', error);
      toast.error('Failed to fetch accounts');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validation
    if (!formData.fromAccount || !formData.toAccount || !formData.amount) {
      toast.error('Please fill all required fields');
      return;
    }

    if (formData.fromAccount === formData.toAccount) {
      toast.error('Cannot transfer to the same account');
      return;
    }

    if (parseFloat(formData.amount) <= 0) {
      toast.error('Amount must be greater than 0');
      return;
    }

    try {
      setProcessing(true);
      console.log('Transfer data:', formData);

      await accountService.transfer(
        formData.fromAccount,
        formData.toAccount,
        parseFloat(formData.amount),
        formData.description || 'Money Transfer'
      );

      toast.success('✅ Transfer successful!');
      setFormData({ fromAccount: '', toAccount: '', amount: '', description: '' });

      // Refresh accounts to show updated balance
      await fetchAccounts();
    } catch (error) {
      console.error('Transfer error:', error);
      
      // Extract detailed error message
      let errorMessage = 'Transfer failed';
      
      if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      } else if (error.message) {
        errorMessage = error.message;
      }
      
      // Show specific error messages
      if (errorMessage.includes('Destination Account')) {
        toast.error('❌ Destination account does not exist. Please verify the account number.');
      } else if (errorMessage.includes('Source Account')) {
        toast.error('❌ Source account not found.');
      } else if (errorMessage.includes('Insufficient balance')) {
        toast.error('❌ Insufficient balance in your account.');
      } else if (errorMessage.includes('not active')) {
        toast.error('❌ One or both accounts are not active.');
      } else {
        toast.error(`❌ ${errorMessage}`);
      }
    } finally {
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Transfer Money</h1>
        <button
          onClick={fetchAccounts}
          className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          <RefreshCw className="w-5 h-5" />
          Refresh
        </button>
      </div>

      {accounts.length === 0 ? (
        <div className="max-w-2xl bg-white rounded-xl shadow-sm p-12 text-center">
          <p className="text-gray-600 mb-4">You need at least one account to make transfers</p>
          <button
            onClick={() => navigate('/accounts')}
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            Go to Accounts
          </button>
        </div>
      ) : (
        <div className="max-w-2xl bg-white rounded-xl shadow-sm p-6">
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                From Account *
              </label>
              <select
                value={formData.fromAccount}
                onChange={(e) => setFormData({ ...formData, fromAccount: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="">Select account</option>
                {accounts.map((acc) => (
                  <option key={acc.id} value={acc.accountNumber}>
                    {acc.accountType} - ****{acc.accountNumber?.slice(-4)} (₹
                    {parseFloat(acc.balance || 0).toLocaleString('en-IN')})
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                To Account Number *
              </label>
              <input
                type="text"
                value={formData.toAccount}
                onChange={(e) => setFormData({ ...formData, toAccount: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                placeholder="Enter account number"
                required
              />
              <p className="text-xs text-gray-500 mt-1">
                You can transfer to any account number (including your own other accounts)
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Amount (₹) *
              </label>
              <input
                type="number"
                step="0.01"
                value={formData.amount}
                onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                placeholder="0.00"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Description (Optional)
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                rows="3"
                placeholder="Add a note..."
              />
            </div>

            <button
              type="submit"
              disabled={processing}
              className="w-full flex items-center justify-center gap-2 bg-blue-600 text-white py-3 rounded-lg hover:bg-blue-700 font-semibold disabled:opacity-50"
            >
              {processing ? 'Processing...' : 'Transfer'}
              <ArrowRight className="w-5 h-5" />
            </button>
          </form>

          {/* Quick Transfer to My Accounts */}
          {accounts.length > 1 && (
            <div className="mt-6 pt-6 border-t">
              <h3 className="text-sm font-medium text-gray-700 mb-3">Quick Transfer to My Accounts</h3>
              <div className="space-y-2">
                {accounts
                  .filter(acc => acc.accountNumber !== formData.fromAccount)
                  .map((acc) => (
                    <button
                      key={acc.id}
                      type="button"
                      onClick={() => setFormData({ ...formData, toAccount: acc.accountNumber })}
                      className="w-full text-left p-3 border border-gray-200 rounded-lg hover:bg-gray-50"
                    >
                      <p className="font-medium text-gray-800">{acc.accountType}</p>
                      <p className="text-sm text-gray-600">{acc.accountNumber}</p>
                    </button>
                  ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default Transfer;