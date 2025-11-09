import React, { useState, useEffect } from 'react';
import { Search, Filter, Download, RefreshCw } from 'lucide-react';
import accountService from '../services/accountService';
import transactionService from '../services/transactionService';
import authService from '../services/authService';
import { toast } from 'react-toastify';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('ALL');

  useEffect(() => {
    fetchAllTransactions();
  }, []);

  const fetchAllTransactions = async () => {
    try {
      setLoading(true);
      const customerId = authService.getCustomerId();

      if (!customerId) {
        toast.error('Customer ID not found');
        return;
      }

      // Get all customer accounts
      const accounts = await accountService.getCustomerAccounts(customerId);
      console.log('Accounts:', accounts);

      // Fetch transactions for all accounts
      let allTransactions = [];
      for (const account of accounts) {
        try {
          const txns = await transactionService.getAccountTransactions(account.id);

          // Add account info to each transaction
          const txnsWithAccountInfo = txns.map(txn => ({
            ...txn,
            accountNumber: account.accountNumber,
            accountType: account.accountType
          }));

          allTransactions = [...allTransactions, ...txnsWithAccountInfo];
        } catch (error) {
          console.error(`Error fetching transactions for account ${account.id}:`, error);
        }
      }

      // Sort by date (newest first)
      allTransactions.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));

      console.log('All transactions:', allTransactions);
      setTransactions(allTransactions);
    } catch (error) {
      console.error('Error fetching transactions:', error);
      toast.error('Failed to fetch transactions');
    } finally {
      setLoading(false);
    }
  };

  const filteredTransactions = transactions.filter((txn) => {
    const matchesSearch = txn.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         txn.transactionId?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = filterType === 'ALL' || txn.type === filterType;
    return matchesSearch && matchesType;
  });

  const exportToCSV = () => {
    const headers = ['Date', 'Type', 'Description', 'Amount', 'Balance', 'Account', 'Reference'];
    const csvData = filteredTransactions.map(txn => [
      new Date(txn.timestamp).toLocaleDateString(),
      txn.type,
      txn.description,
      txn.amount,
      txn.balanceAfter,
      txn.accountNumber,
      txn.referenceNumber
    ]);

    const csv = [headers, ...csvData].map(row => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `transactions-${new Date().toISOString().split('T')[0]}.csv`;
    a.click();
    toast.success('Transactions exported successfully!');
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
        <h1 className="text-3xl font-bold text-gray-800">Transactions</h1>
        <button
          onClick={fetchAllTransactions}
          className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
        >
          <RefreshCw className="w-5 h-5" />
          Refresh
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
        <div className="flex flex-col md:flex-row gap-4 mb-6">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-3 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Search transactions..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <select
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
          >
            <option value="ALL">All Types</option>
            <option value="DEPOSIT">Deposits</option>
            <option value="WITHDRAWAL">Withdrawals</option>
            <option value="TRANSFER">Transfers</option>
            <option value="PAYMENT">Payments</option>
          </select>
          <button
            onClick={exportToCSV}
            className="flex items-center gap-2 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50"
          >
            <Download className="w-5 h-5" />
            Export
          </button>
        </div>

        {filteredTransactions.length === 0 ? (
          <p className="text-gray-500 text-center py-8">
            {transactions.length === 0 ? 'No transactions found' : 'No transactions match your search'}
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Date & Time
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Description
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Account
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                    Balance After
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {filteredTransactions.map((txn) => (
                  <tr key={txn.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div>
                        <p>{new Date(txn.timestamp).toLocaleDateString()}</p>
                        <p className="text-xs text-gray-500">
                          {new Date(txn.timestamp).toLocaleTimeString()}
                        </p>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900">
                      <div>
                        <p>{txn.description}</p>
                        <p className="text-xs text-gray-500">Ref: {txn.referenceNumber}</p>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`px-2 py-1 text-xs rounded-full ${
                          txn.type === 'DEPOSIT'
                            ? 'bg-green-100 text-green-800'
                            : txn.type === 'WITHDRAWAL'
                            ? 'bg-red-100 text-red-800'
                            : 'bg-blue-100 text-blue-800'
                        }`}
                      >
                        {txn.type}
                      </span>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-600">
                      <div>
                        <p>{txn.accountType}</p>
                        <p className="text-xs text-gray-500">****{txn.accountNumber?.slice(-4)}</p>
                      </div>
                    </td>
                    <td
                      className={`px-6 py-4 whitespace-nowrap text-right text-sm font-medium ${
                        txn.type === 'DEPOSIT' ? 'text-green-600' : 'text-red-600'
                      }`}
                    >
                      {txn.type === 'DEPOSIT' ? '+' : '-'}₹
                      {parseFloat(txn.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm text-gray-900">
                      ₹{parseFloat(txn.balanceAfter).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Summary */}
      {filteredTransactions.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm p-6">
          <h3 className="font-semibold text-gray-800 mb-3">Summary</h3>
          <div className="grid grid-cols-3 gap-4">
            <div>
              <p className="text-sm text-gray-500">Total Transactions</p>
              <p className="text-xl font-bold text-gray-800">{filteredTransactions.length}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Total Deposits</p>
              <p className="text-xl font-bold text-green-600">
                ₹{filteredTransactions
                  .filter(t => t.type === 'DEPOSIT')
                  .reduce((sum, t) => sum + parseFloat(t.amount), 0)
                  .toLocaleString('en-IN')}
              </p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Total Withdrawals</p>
              <p className="text-xl font-bold text-red-600">
                ₹{filteredTransactions
                  .filter(t => t.type === 'WITHDRAWAL' || t.type === 'TRANSFER')
                  .reduce((sum, t) => sum + parseFloat(t.amount), 0)
                  .toLocaleString('en-IN')}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Transactions;
