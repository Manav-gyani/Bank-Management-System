import React, { useState, useEffect } from 'react';
import { CreditCard, X } from 'lucide-react';
import cardService from '../services/cardService';
import accountService from '../services/accountService';
import authService from '../services/authService';
import { toast } from 'react-toastify';

const Cards = () => {
  const [cards, setCards] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    accountNumber: '',
    cardType: 'DEBIT',
  });

  useEffect(() => {
    fetchCards();
    fetchAccounts();
  }, []);

  const fetchCards = async () => {
    try {
      setLoading(true);
      const customerId = authService.getCustomerId();
      
      if (!customerId) {
        toast.error('Customer ID not found');
        return;
      }

      const data = await cardService.getCustomerCards(customerId);
      setCards(data);
    } catch (error) {
      console.error('Error fetching cards:', error);
      toast.error('Failed to fetch cards');
    } finally {
      setLoading(false);
    }
  };

  const fetchAccounts = async () => {
    try {
      const customerId = authService.getCustomerId();
      if (!customerId) return;

      const data = await accountService.getCustomerAccounts(customerId);
      setAccounts(data.filter(acc => acc.status === 'ACTIVE'));
    } catch (error) {
      console.error('Error fetching accounts:', error);
    }
  };

  const handleApplyCard = async (e) => {
    e.preventDefault();
    
    try {
      setSubmitting(true);
      
      await cardService.applyForCard(formData.accountNumber, formData.cardType);
      
      toast.success('Card application submitted successfully!');
      setShowApplyModal(false);
      setFormData({ accountNumber: '', cardType: 'DEBIT' });
      fetchCards();
    } catch (error) {
      console.error('Error applying for card:', error);
      toast.error(error.response?.data?.message || 'Failed to apply for card');
    } finally {
      setSubmitting(false);
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
        <h1 className="text-3xl font-bold text-gray-800">Cards</h1>
        <button
          onClick={() => setShowApplyModal(true)}
          className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Apply for Card
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6">
        {cards.length === 0 ? (
          <div className="text-center py-12">
            <CreditCard className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600 mb-4">No cards issued yet</p>
            <button 
              onClick={() => setShowApplyModal(true)}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              Apply for Card
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {cards.map((card) => (
              <div
                key={card.id}
                className="relative bg-gradient-to-br from-blue-600 to-blue-800 rounded-xl p-6 text-white shadow-lg"
              >
                <div className="mb-8">
                  <p className="text-xs opacity-80 mb-1">Card Number</p>
                  <p className="text-lg font-mono tracking-wider">
                    **** **** **** {card.cardNumber?.slice(-4)}
                  </p>
                </div>
                <div className="flex justify-between items-end">
                  <div>
                    <p className="text-xs opacity-80">Card Type</p>
                    <p className="font-semibold">{card.cardType}</p>
                  </div>
                  <div>
                    <p className="text-xs opacity-80">Status</p>
                    <p className="font-semibold">{card.status}</p>
                  </div>
                </div>
                <CreditCard className="absolute top-4 right-4 w-8 h-8 opacity-30" />
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Apply Card Modal */}
      {showApplyModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-xl p-6 w-full max-w-md">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-gray-800">Apply for Card</h2>
              <button
                onClick={() => setShowApplyModal(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {accounts.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-gray-600 mb-4">You need an active account to apply for a card</p>
                <button
                  onClick={() => setShowApplyModal(false)}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                >
                  Close
                </button>
              </div>
            ) : (
              <form onSubmit={handleApplyCard} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Select Account *
                  </label>
                  <select
                    value={formData.accountNumber}
                    onChange={(e) => setFormData({ ...formData, accountNumber: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="">Select account</option>
                    {accounts.map((acc) => (
                      <option key={acc.id} value={acc.accountNumber}>
                        {acc.accountType} - {acc.accountNumber} (₹{parseFloat(acc.balance || 0).toLocaleString('en-IN')})
                      </option>
                    ))}
                  </select>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Card Type *
                  </label>
                  <select
                    value={formData.cardType}
                    onChange={(e) => setFormData({ ...formData, cardType: e.target.value })}
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                  >
                    <option value="DEBIT">Debit Card</option>
                    <option value="CREDIT">Credit Card</option>
                  </select>
                </div>

                <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mt-4">
                  <p className="text-sm text-blue-800">
                    <strong>Note:</strong> Your card will be issued after verification and will be delivered to your registered address within 7-10 business days.
                  </p>
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
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Cards;