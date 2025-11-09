import React from 'react';
import { Lock, Bell, Shield } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

const Settings = () => {
  const navigate = useNavigate();

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Settings</h1>

      <div className="max-w-2xl space-y-4">
        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center gap-3 mb-3">
            <Lock className="w-5 h-5 text-gray-600" />
            <h3 className="font-semibold text-gray-800">Security</h3>
          </div>
          <button 
            onClick={() => navigate('/profile')}
            className="text-blue-600 hover:underline text-sm"
          >
            Change Password
          </button>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center gap-3 mb-3">
            <Bell className="w-5 h-5 text-gray-600" />
            <h3 className="font-semibold text-gray-800">Notifications</h3>
          </div>
          <div className="space-y-2">
            <label className="flex items-center gap-2">
              <input type="checkbox" defaultChecked className="rounded" />
              <span className="text-sm text-gray-700">Email notifications</span>
            </label>
            <label className="flex items-center gap-2">
              <input type="checkbox" defaultChecked className="rounded" />
              <span className="text-sm text-gray-700">SMS alerts</span>
            </label>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <div className="flex items-center gap-3">
            <Shield className="w-5 h-5 text-gray-600" />
            <h3 className="font-semibold text-gray-800">Privacy</h3>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Settings;