import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import { DollarSign, CreditCard, ArrowUpRight, ArrowDownLeft, TrendingUp } from 'lucide-react';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';
import authService from '../services/authService';
import { useNavigate } from 'react-router-dom';
import { ensureCustomerId } from '../utils/ensureCustomerId';

const Dashboard = () => {
  const navigate = useNavigate();
  const [accounts, setAccounts] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalBalance: 0,
    totalDeposits: 0,
    totalWithdrawals: 0
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      
      // Ensure customerId is available
      const customerId = await ensureCustomerId();

      console.log('Fetching data for customer:', customerId);

      // Fetch accounts
      const accountsData = await accountService.getCustomerAccounts(customerId);
      console.log('Accounts fetched:', accountsData);
      setAccounts(accountsData);

      // Calculate total balance
      const totalBalance = accountsData.reduce((sum, acc) => sum + parseFloat(acc.balance || 0), 0);

      // Fetch transactions for all accounts
      let allTransactions = [];
      for (const account of accountsData) {
        try {
          const txns = await transactionService.getAccountTransactions(account.id);
          allTransactions = [...allTransactions, ...txns];
        } catch (error) {
          console.error(`Error fetching transactions for account ${account.id}:`, error);
        }
      }

      console.log('Transactions fetched:', allTransactions);

      // Sort by date (newest first)
      allTransactions.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      setTransactions(allTransactions);

      // Calculate stats
      const deposits = allTransactions
        .filter(t => t.type === 'DEPOSIT')
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);

      const withdrawals = allTransactions
        .filter(t => t.type === 'WITHDRAWAL' || t.type === 'TRANSFER')
        .reduce((sum, t) => sum + parseFloat(t.amount || 0), 0);

      setStats({
        totalBalance,
        totalDeposits: deposits,
        totalWithdrawals: withdrawals
      });

    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
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
        <h1 className="text-3xl font-bold text-gray-800">Dashboard</h1>
        <button
          onClick={fetchDashboardData}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Refresh
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600 text-sm">Total Balance</span>
            <DollarSign className="w-5 h-5 text-green-600" />
          </div>
          <p className="text-2xl font-bold text-gray-800">
            ₹{stats.totalBalance.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
          </p>
          <p className="text-xs text-green-600 mt-1">All accounts combined</p>
        </div>

        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600 text-sm">Active Accounts</span>
            <CreditCard className="w-5 h-5 text-blue-600" />
          </div>
          <p className="text-2xl font-bold text-gray-800">{accounts.length}</p>
          <p className="text-xs text-gray-500 mt-1">All accounts active</p>
        </div>

        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600 text-sm">Total Deposits</span>
            <ArrowUpRight className="w-5 h-5 text-green-600" />
          </div>
          <p className="text-2xl font-bold text-gray-800">
            ₹{stats.totalDeposits.toLocaleString('en-IN')}
          </p>
          <p className="text-xs text-gray-500 mt-1">All time</p>
        </div>

        <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
          <div className="flex items-center justify-between mb-2">
            <span className="text-gray-600 text-sm">Expenses</span>
            <ArrowDownLeft className="w-5 h-5 text-red-600" />
          </div>
          <p className="text-2xl font-bold text-gray-800">
            ₹{stats.totalWithdrawals.toLocaleString('en-IN')}
          </p>
          <p className="text-xs text-gray-500 mt-1">All time</p>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Accounts */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Your Accounts</h2>
          {accounts.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No accounts found</p>
          ) : (
            <div className="space-y-3">
              {accounts.map((account) => (
                <div
                  key={account.id}
                  onClick={() => navigate(`/accounts/${account.accountNumber}`)}
                  className="flex justify-between items-center p-4 bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg hover:shadow-md transition-shadow cursor-pointer"
                >
                  <div>
                    <p className="font-semibold text-gray-800">{account.accountType} Account</p>
                    <p className="text-sm text-gray-600">****{account.accountNumber?.slice(-4)}</p>
                  </div>
                  <p className="text-lg font-bold text-gray-800">
                    ₹{parseFloat(account.balance || 0).toLocaleString('en-IN')}
                  </p>
                </div>
              ))}
            </div>
          )}
          <button
            onClick={() => navigate('/accounts')}
            className="w-full mt-4 py-2 border-2 border-blue-600 text-blue-600 rounded-lg hover:bg-blue-50 font-medium"
          >
            View All Accounts
          </button>
        </div>

        {/* Recent Transactions */}
        <div className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <h2 className="text-lg font-semibold text-gray-800 mb-4">Recent Transactions</h2>
          {transactions.length === 0 ? (
            <p className="text-gray-500 text-center py-8">No transactions yet</p>
          ) : (
            <div className="space-y-3">
              {transactions.slice(0, 5).map((txn) => (
                <div
                  key={txn.id}
                  className="flex justify-between items-center p-3 hover:bg-gray-50 rounded-lg transition-colors"
                >
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-10 h-10 rounded-full flex items-center justify-center ${
                        txn.type === 'DEPOSIT' ? 'bg-green-100' : 'bg-red-100'
                      }`}
                    >
                      {txn.type === 'DEPOSIT' ? (
                        <ArrowDownLeft className="w-5 h-5 text-green-600" />
                      ) : (
                        <ArrowUpRight className="w-5 h-5 text-red-600" />
                      )}
                    </div>
                    <div>
                      <p className="font-medium text-gray-800">{txn.description}</p>
                      <p className="text-xs text-gray-500">
                        {new Date(txn.timestamp).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <p
                    className={`font-semibold ${
                      txn.type === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                    }`}
                  >
                    {txn.type === 'DEPOSIT' ? '+' : '-'}₹
                    {parseFloat(txn.amount).toLocaleString('en-IN')}
                  </p>
                </div>
              ))}
            </div>
          )}
          <button
            onClick={() => navigate('/transactions')}
            className="w-full mt-4 py-2 text-blue-600 hover:bg-blue-50 rounded-lg font-medium"
          >
            View All Transactions
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;