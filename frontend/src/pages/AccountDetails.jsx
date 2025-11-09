import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowUpRight, ArrowDownLeft, ArrowLeft, Plus, Minus, RefreshCw } from 'lucide-react';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';
import { toast } from 'react-toastify';

const AccountDetails = () => {
  const { accountNumber } = useParams();
  const navigate = useNavigate();
  const [account, setAccount] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showDepositModal, setShowDepositModal] = useState(false);
  const [showWithdrawModal, setShowWithdrawModal] = useState(false);
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    fetchAccountDetails();
  }, [accountNumber]);

  const fetchAccountDetails = async () => {
    try {
      setLoading(true);
      console.log('Fetching account:', accountNumber);

      const accountData = await accountService.getAccount(accountNumber);
      console.log('Account data:', accountData);
      setAccount(accountData);

      const txnData = await transactionService.getAccountTransactions(accountData.id);
      console.log('Transactions:', txnData);

      // Sort by date (newest first)
      txnData.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      setTransactions(txnData);
    } catch (error) {
      console.error('Error fetching account details:', error);
      toast.error('Failed to fetch account details');
    } finally {
      setLoading(false);
    }
  };

  const handleDeposit = async () => {
    try {
      setProcessing(true);
      await accountService.deposit(accountNumber, parseFloat(amount), description || 'Deposit');
      toast.success('Deposit successful!');
      setShowDepositModal(false);
      setAmount('');
      setDescription('');
      fetchAccountDetails(); // Refresh data
    } catch (error) {
      console.error('Deposit error:', error);
      toast.error(error.response?.data?.message || 'Deposit failed');
    } finally {
      setProcessing(false);
    }
  };

  const handleWithdraw = async () => {
    try {
      setProcessing(true);
      await accountService.withdraw(accountNumber, parseFloat(amount), description || 'Withdrawal');
      toast.success('Withdrawal successful!');
      setShowWithdrawModal(false);
      setAmount('');
      setDescription('');
      fetchAccountDetails(); // Refresh data
    } catch (error) {
      console.error('Withdrawal error:', error);
      toast.error(error.response?.data?.message || 'Withdrawal failed');
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

  if (!account) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-600 mb-4">Account not found</p>
        <button
          onClick={() => navigate('/accounts')}
          className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Back to Accounts
        </button>
      </div>
    );
  }

  return (
    <div>
      <div className="flex items-center gap-4 mb-6">
        <button
          onClick={() => navigate('/accounts')}
          className="p-2 hover:bg-gray-100 rounded-lg"
        >
          <ArrowLeft className="w-6 h-6" />
        </button>
        <h1 className="text-3xl font-bold text-gray-800">Account Details</h1>
        <button
          onClick={fetchAccountDetails}
          className="ml-auto flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          <RefreshCw className="w-5 h-5" />
          Refresh
        </button>
      </div>

      {/* Account Info Card */}
      <div className="bg-gradient-to-r from-blue-600 to-blue-800 rounded-xl p-8 text-white mb-6">
        <div className="flex justify-between items-start mb-6">
          <div>
            <p className="text-blue-100 mb-1">Account Number</p>
            <p className="text-2xl font-bold">{account.accountNumber}</p>
          </div>
          <span className="px-4 py-1 bg-white bg-opacity-20 rounded-full text-sm">
            {account.accountType}
          </span>
        </div>
        <div className="mb-6">
          <p className="text-blue-100 mb-1">Available Balance</p>
          <p className="text-4xl font-bold">
            ₹{parseFloat(account.balance || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => setShowDepositModal(true)}
            className="flex items-center gap-2 px-4 py-2 bg-white text-blue-600 rounded-lg hover:bg-blue-50 font-medium"
          >
            <Plus className="w-5 h-5" />
            Deposit
          </button>
          <button
            onClick={() => setShowWithdrawModal(true)}
            className="flex items-center gap-2 px-4 py-2 bg-white bg-opacity-20 text-white rounded-lg hover:bg-opacity-30 font-medium"
          >
            <Minus className="w-5 h-5" />
            Withdraw
          </button>
        </div>
      </div>

      {/* Transaction History */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
        <h2 className="text-lg font-semibold text-gray-800 mb-4">Transaction History</h2>
        {transactions.length === 0 ? (
          <p className="text-gray-500 text-center py-8">No transactions yet</p>
        ) : (
          <div className="space-y-3">
            {transactions.map((txn) => (
              <div
                key={txn.id}
                className="flex justify-between items-center p-4 hover:bg-gray-50 rounded-lg border border-gray-100"
              >
                <div className="flex items-center gap-4">
                  <div
                    className={`w-12 h-12 rounded-full flex items-center justify-center ${
                      txn.type === 'DEPOSIT' ? 'bg-green-100' : 'bg-red-100'
                    }`}
                  >
                    {txn.type === 'DEPOSIT' ? (
                      <ArrowDownLeft className="w-6 h-6 text-green-600" />
                    ) : (
                      <ArrowUpRight className="w-6 h-6 text-red-600" />
                    )}
                  </div>
                  <div>
                    <p className="font-medium text-gray-800">{txn.description}</p>
                    <p className="text-sm text-gray-500">
                      {new Date(txn.timestamp).toLocaleString()}
                    </p>
                    <p className="text-xs text-gray-400">Ref: {txn.referenceNumber}</p>
                  </div>
                </div>
                <div className="text-right">
                  <p
                    className={`text-xl font-semibold ${
                      txn.type === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                    }`}
                  >
                    {txn.type === 'DEPOSIT' ? '+' : '-'}₹
                    {parseFloat(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </p>
                  <p className="text-sm text-gray-500">
                    Balance: ₹{parseFloat(txn.balanceAfter).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                  </p>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Deposit Modal */}
      {showDepositModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl p-6 w-full max-w-md">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Deposit Money</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Amount (₹)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder="0.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description (Optional)
                </label>
                <input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder="Add a note..."
                />
              </div>
            </div>
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowDepositModal(false)}
                disabled={processing}
                className="flex-1 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 disabled:opacity-50"
              >
                Cancel
              </button>
              <button
                onClick={handleDeposit}
                disabled={!amount || processing}
                className="flex-1 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
              >
                {processing ? 'Processing...' : 'Deposit'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Withdraw Modal */}
      {showWithdrawModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl p-6 w-full max-w-md">
            <h2 className="text-xl font-bold text-gray-800 mb-4">Withdraw Money</h2>
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Amount (₹)
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder="0.00"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Description (Optional)
                </label>
                <input
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  placeholder="Add a note..."
                />
              </div>
            </div>
            <div className="flex gap-3 mt-6">
              <button
                onClick={() => setShowWithdrawModal(false)}
                disabled={processing}
                className="flex-1 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 disabled:opacity-50"
              >
                Cancel
              </button>
              <button
                onClick={handleWithdraw}
                disabled={!amount || processing}
                className="flex-1 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:opacity-50"
              >
                {processing ? 'Processing...' : 'Withdraw'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AccountDetails;