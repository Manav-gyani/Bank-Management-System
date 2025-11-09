import React from 'react';
import { NavLink } from 'react-router-dom';
import { useSelector } from 'react-redux';
import {
  LayoutDashboard,
  CreditCard,
  ArrowLeftRight,
  FileText,
  Users,
  Settings,
  Briefcase,
  UserPlus,
  Wallet,
} from 'lucide-react';

const Sidebar = () => {
  const { sidebarOpen } = useSelector((state) => state.ui);

  const menuItems = [
    { path: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { path: '/accounts', icon: CreditCard, label: 'Accounts' },
    { path: '/transfer', icon: ArrowLeftRight, label: 'Transfer' },
    { path: '/transactions', icon: FileText, label: 'Transactions' },
    { path: '/beneficiaries', icon: UserPlus, label: 'Beneficiaries' },
    { path: '/loans', icon: Briefcase, label: 'Loans' },
    { path: '/cards', icon: Wallet, label: 'Cards' },
    { path: '/profile', icon: Users, label: 'Profile' },
    { path: '/settings', icon: Settings, label: 'Settings' },
  ];

  if (!sidebarOpen) return null;

  return (
    <aside className="fixed lg:sticky top-0 left-0 h-screen w-64 bg-white border-r border-gray-200 z-40 overflow-y-auto">
      <div className="p-6">
        <div className="flex items-center gap-2 mb-8">
          <div className="w-10 h-10 bg-blue-600 rounded-lg flex items-center justify-center">
            <span className="text-white font-bold text-xl">B</span>
          </div>
          <span className="text-xl font-bold text-gray-800">BankMS</span>
        </div>

        <nav className="space-y-2">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-600'
                    : 'text-gray-600 hover:bg-gray-50'
                }`
              }
            >
              <item.icon className="w-5 h-5" />
              <span className="font-medium">{item.label}</span>
            </NavLink>
          ))}
        </nav>
      </div>
    </aside>
  );
};

export default Sidebar;