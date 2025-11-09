import React, { useState, useEffect } from 'react';
import { FileText, X } from 'lucide-react';
import loanService from '../services/loanService';
import authService from '../services/authService';
import { toast } from 'react-toastify';

const Loans = () => {
  const [loans, setLoans] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [selectedLoanType, setSelectedLoanType] = useState('');
  const [formData, setFormData] = useState({
    amount: '',
    tenure: '',
    purpose: '',
  });

  const loanTypes = [
    { type: 'HOME_LOAN', name: 'Home Loan', rate: '8.5', icon: 'text-blue-600' },
    { type: 'PERSONAL_LOAN', name: 'Personal Loan', rate: '12', icon: 'text-green-600' },
    { type: 'CAR_LOAN', name: 'Car Loan', rate: '10', icon: 'text-purple-600' },
  ];

  useEffect(() => {
    fetchLoans();
  }, []);

  const fetchLoans = async () => {
    try {
      setLoading(true);
      const customerId = authService.getCustomerId();
      
      if (!customerId) {
        toast.error('Customer ID not found');
        return;
      }

      const data = await loanService.getCustomerLoans(customerId);
      setLoans(data);
    } catch (error) {
      console.error('Error fetching loans:', error);
      toast.error('Failed to fetch loans');
    } finally {
      setLoading(false);
    }
  };

  const handleApplyLoan = async (e) => {
    e.preventDefault();
    
    try {
      setSubmitting(true);
      const customerId = authService.getCustomerId();
      
      if (!customerId) {
        toast.error('Customer ID not found');
        return;
      }

      await loanService.applyForLoan(customerId, {
        ...formData,
        loanType: selectedLoanType,
      });
      
      toast.success('Loan application submitted successfully!');
      setShowApplyModal(false);
      setFormData({ amount: '', tenure: '', purpose: '' });
      setSelectedLoanType('');
      fetchLoans();
    } catch (error) {
      console.error('Error applying for loan:', error);
      toast.error(error.response?.data?.message || 'Failed to apply for loan');
    } finally {
      setSubmitting(false);
    }
  };

  const openApplyModal = (loanType) => {
    setSelectedLoanType(loanType);
    setShowApplyModal(true);
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
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Loans</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        {loanTypes.map((loan) => (
          <div key={loan.type} className="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
            <FileText className={`w-8 h-8 ${loan.icon} mb-3`} />
            <h3 className="text-lg font-semibold text-gray-800 mb-2">{loan.name}</h3>
            <p className="text-sm text-gray-600 mb-4">Interest rate from {loan.rate}% p.a.</p>
            <button 
              onClick={() => openApplyModal(loan.type)}
              className="text-blue-600 hover:underline text-sm font-medium"
            >
              Apply Now →
            </button>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6">
        <h2 className="text-xl font-semibold text-gray-800 mb-4">Your Active Loans</h2>
        {loans.length === 0 ? (
          <p className="text-gray-500 text-center py-8">No active loans</p>
        ) : (
          <div className="space-y-4">
            {loans.map((loan) => (
              <div key={loan.id} className="p-4 border border-gray-200 rounded-lg">
                <div className="flex justify-between items-start">
                  <div>
                    <p className="font-semibold text-gray-800">{loan.loanType?.replace('_', ' ')}</p>
                    <p className="text-sm text-gray-600">Amount: ₹{parseFloat(loan.amount || 0).toLocaleString('en-IN')}</p>
                    <p className="text-sm text-gray-600">Tenure: {loan.tenure} months</p>
                    <p className="text-sm text-gray-600">Status: <span className="font-medium">{loan.status}</span></p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                    loan.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                    loan.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                    'bg-red-100 text-red-800'
                  }`}>
                    {loan.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Apply Loan Modal */}
      {showApplyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">
                Apply for {loanTypes.find(l => l.type === selectedLoanType)?.name}
              </h2>
              <button
                onClick={() => setShowApplyModal(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            <form onSubmit={handleApplyLoan} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Loan Amount (₹) *
                </label>
                <input
                  type="number"
                  value={formData.amount}
                  onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                  min="10000"
                  step="1000"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Tenure (months) *
                </label>
                <select
                  value={formData.tenure}
                  onChange={(e) => setFormData({ ...formData, tenure: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">Select tenure</option>
                  <option value="12">12 months</option>
                  <option value="24">24 months</option>
                  <option value="36">36 months</option>
                  <option value="60">60 months</option>
                  <option value="120">120 months (10 years)</option>
                  <option value="240">240 months (20 years)</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Purpose *
                </label>
                <textarea
                  value={formData.purpose}
                  onChange={(e) => setFormData({ ...formData, purpose: e.target.value })}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                  rows="3"
                  required
                  placeholder="e.g., Home purchase, Vehicle purchase, Personal use"
                />
              </div>

              <div className="flex gap-3 mt-6">
                <button
                  type="button"
                  onClick={() => setShowApplyModal(false)}
                  disabled={submitting}
                  className="flex-1 py-2 border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-50 disabled:opacity-50"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={submitting}
                  className="flex-1 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                >
                  {submitting ? 'Submitting...' : 'Apply'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Loans;