import React, { useState, useEffect } from 'react';
import { User, Mail, Phone, MapPin, Lock, Edit } from 'lucide-react';
import { useSelector, useDispatch } from 'react-redux';
import Modal from '../components/common/Modal';
import Input from '../components/common/Input';
import Button from '../components/common/Button';
import Alert from '../components/common/Alert';
import userService from '../services/userService';
import { setUser } from '../store/slices/authSlice';

const Profile = () => {
  const { user } = useSelector((state) => state.auth);
  const dispatch = useDispatch();

  const [showEditModal, setShowEditModal] = useState(false);
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [fetchingProfile, setFetchingProfile] = useState(true);
  const [alert, setAlert] = useState(null);
  const [profileData, setProfileData] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0); // Add refresh key

  const [editForm, setEditForm] = useState({
    username: '',
    email: '',
    firstName: '',
    lastName: '',
    phone: '',
    address: '',
  });

  const [passwordForm, setPasswordForm] = useState({
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  // Fetch complete profile data on component mount
  useEffect(() => {
    const fetchProfile = async () => {
      setFetchingProfile(true);
      try {
        console.log('Fetching profile data...');
        const profile = await userService.getCurrentProfile();
        console.log('Profile fetched:', profile);
        setProfileData(profile);
        
        // Store customerId in localStorage if it exists
        if (profile.customerId) {
          localStorage.setItem('customerId', profile.customerId);
          console.log('✅ CustomerId updated in localStorage:', profile.customerId);
          
          // Also update user object
          const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
          currentUser.customerId = profile.customerId;
          localStorage.setItem('user', JSON.stringify(currentUser));
        }
        
        setEditForm({
          username: profile.username || '',
          email: profile.email || '',
          firstName: profile.firstName || '',
          lastName: profile.lastName || '',
          phone: profile.phone || '',
          address: profile.address || '',
        });
      } catch (error) {
        console.error('Failed to fetch profile:', error);
        setAlert({ type: 'error', message: 'Failed to load profile data' });
      } finally {
        setFetchingProfile(false);
      }
    };
    fetchProfile();
  }, [refreshKey]); // Re-fetch when refreshKey changes

  // Check if user has ID, if not fetch current user data
  useEffect(() => {
    const checkAndFetchUserData = async () => {
      if (user && !user.id) {
        console.warn('User ID not found in localStorage, fetching current user...');
        try {
          const currentUser = await userService.getCurrentUser();
          const updatedUser = {
            ...user,
            id: currentUser.id,
          };
          dispatch(setUser(updatedUser));
          localStorage.setItem('user', JSON.stringify(updatedUser));
        } catch (error) {
          console.error('Failed to fetch user data:', error);
          setAlert({ 
            type: 'error', 
            message: 'Please log out and log in again to update your profile.' 
          });
        }
      }
    };
    checkAndFetchUserData();
  }, [user, dispatch]);

  // Debug: Log user object
  useEffect(() => {
    console.log('Current user object:', user);
    console.log('Profile data:', profileData);
  }, [user, profileData]);

  const handleEditProfile = async (e) => {
    e.preventDefault();
    setLoading(true);
    setAlert(null);

    // Check if user ID exists
    if (!user?.id) {
      setAlert({ 
        type: 'error', 
        message: 'Session error. Please log out and log in again.' 
      });
      setLoading(false);
      return;
    }

    console.log('Updating profile with ID:', user.id, 'Data:', editForm);

    try {
      const response = await userService.updateProfile(user.id, editForm);
      console.log('Update response:', response);
      
      // Close modal immediately
      setShowEditModal(false);
      
      // Show success alert
      setAlert({ type: 'success', message: 'Profile updated successfully! Refreshing...' });
      
      // Wait a moment for backend to fully commit
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Trigger refresh by incrementing refreshKey
      setRefreshKey(prev => prev + 1);
      
      // Clear alert after 3 seconds
      setTimeout(() => setAlert(null), 3000);
    } catch (error) {
      console.error('Update profile error:', error.response || error);
      setAlert({ 
        type: 'error', 
        message: error.response?.data?.message || 'Failed to update profile' 
      });
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setLoading(true);
    setAlert(null);

    // Check if user ID exists
    if (!user?.id) {
      setAlert({ 
        type: 'error', 
        message: 'Session error. Please log out and log in again.' 
      });
      setLoading(false);
      return;
    }

    // Validate passwords match
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      setAlert({ type: 'error', message: 'New passwords do not match' });
      setLoading(false);
      return;
    }

    // Validate password length
    if (passwordForm.newPassword.length < 6) {
      setAlert({ type: 'error', message: 'Password must be at least 6 characters' });
      setLoading(false);
      return;
    }

    try {
      console.log('Changing password for user:', user.id);
      await userService.changePassword(user.id, passwordForm);
      console.log('Password changed successfully in database');
      
      // Close modal first
      setShowPasswordModal(false);
      
      // Then show success alert
      setAlert({ 
        type: 'success', 
        message: 'Password changed successfully! Please use your new password for next login.' 
      });
      
      setPasswordForm({
        oldPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
      
      // Clear alert after 5 seconds
      setTimeout(() => setAlert(null), 5000);
    } catch (error) {
      console.error('Change password error:', error.response || error);
      setAlert({ 
        type: 'error', 
        message: error.response?.data?.message || 'Failed to change password' 
      });
    } finally {
      setLoading(false);
    }
  };

  // Handler for opening edit modal with current values
  const handleOpenEditModal = () => {
    if (profileData) {
      setEditForm({
        username: profileData.username || '',
        email: profileData.email || '',
        firstName: profileData.firstName || '',
        lastName: profileData.lastName || '',
        phone: profileData.phone || '',
        address: profileData.address || '',
      });
    }
    setShowEditModal(true);
  };

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Profile</h1>

      {alert && (
        <Alert
          type={alert.type}
          message={alert.message}
          onClose={() => setAlert(null)}
        />
      )}

      {fetchingProfile ? (
        <div className="flex justify-center items-center h-64">
          <div className="text-gray-600">Loading profile...</div>
        </div>
      ) : (
      <div className="max-w-2xl bg-white rounded-xl shadow-sm p-6">
        <div className="flex items-center gap-4 mb-6 pb-6 border-b">
          <div className="w-20 h-20 bg-blue-600 rounded-full flex items-center justify-center text-white text-3xl font-bold">
            {profileData?.firstName?.charAt(0).toUpperCase() || user?.username?.charAt(0).toUpperCase()}
          </div>
          <div>
            <h2 className="text-2xl font-bold text-gray-800">
              {profileData?.firstName && profileData?.lastName 
                ? `${profileData.firstName} ${profileData.lastName}` 
                : user?.username}
            </h2>
            <p className="text-gray-600">Customer</p>
          </div>
        </div>

        <div className="space-y-4">
          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <User className="w-5 h-5 text-gray-600" />
            <div>
              <p className="text-sm text-gray-500">Username</p>
              <p className="font-medium">{profileData?.username || user?.username}</p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <Mail className="w-5 h-5 text-gray-600" />
            <div>
              <p className="text-sm text-gray-500">Email</p>
              <p className="font-medium">{profileData?.email || user?.email}</p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <User className="w-5 h-5 text-gray-600" />
            <div className="flex-1">
              <p className="text-sm text-gray-500">Full Name</p>
              <p className="font-medium">
                {profileData?.firstName || profileData?.lastName
                  ? `${profileData.firstName || ''} ${profileData.lastName || ''}`.trim()
                  : 'Not set'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <Phone className="w-5 h-5 text-gray-600" />
            <div>
              <p className="text-sm text-gray-500">Phone</p>
              <p className="font-medium">{profileData?.phone || 'Not set'}</p>
            </div>
          </div>

          <div className="flex items-center gap-3 p-4 bg-gray-50 rounded-lg">
            <MapPin className="w-5 h-5 text-gray-600" />
            <div>
              <p className="text-sm text-gray-500">Address</p>
              <p className="font-medium">{profileData?.address || 'Not set'}</p>
            </div>
          </div>
        </div>

        <div className="mt-6 pt-6 border-t flex gap-4">
          <button
            onClick={handleOpenEditModal}
            className="flex-1 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 flex items-center justify-center gap-2"
          >
            <Edit className="w-4 h-4" />
            Edit Profile
          </button>
          <button
            onClick={() => setShowPasswordModal(true)}
            className="flex-1 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 flex items-center justify-center gap-2"
          >
            <Lock className="w-4 h-4" />
            Change Password
          </button>
        </div>
      </div>
      )}

      {/* Edit Profile Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => setShowEditModal(false)}
        title="Edit Profile"
      >
        <form onSubmit={handleEditProfile} className="space-y-4">
          <Input
            label="Username"
            type="text"
            value={editForm.username}
            onChange={(e) => setEditForm({ ...editForm, username: e.target.value })}
            required
          />
          <Input
            label="Email"
            type="email"
            value={editForm.email}
            onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
            required
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              type="text"
              value={editForm.firstName}
              onChange={(e) => setEditForm({ ...editForm, firstName: e.target.value })}
            />
            <Input
              label="Last Name"
              type="text"
              value={editForm.lastName}
              onChange={(e) => setEditForm({ ...editForm, lastName: e.target.value })}
            />
          </div>
          <Input
            label="Phone Number"
            type="tel"
            value={editForm.phone}
            onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
            placeholder="+91 9876543210"
          />
          <Input
            label="Address"
            type="text"
            value={editForm.address}
            onChange={(e) => setEditForm({ ...editForm, address: e.target.value })}
            placeholder="Your full address"
          />
          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              onClick={() => setShowEditModal(false)}
              variant="outline"
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              loading={loading}
              className="flex-1"
            >
              Save Changes
            </Button>
          </div>
        </form>
      </Modal>

      {/* Change Password Modal */}
      <Modal
        isOpen={showPasswordModal}
        onClose={() => setShowPasswordModal(false)}
        title="Change Password"
      >
        <form onSubmit={handleChangePassword} className="space-y-4">
          <Input
            label="Current Password"
            type="password"
            value={passwordForm.oldPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, oldPassword: e.target.value })}
            required
          />
          <Input
            label="New Password"
            type="password"
            value={passwordForm.newPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
            required
            minLength={6}
          />
          <Input
            label="Confirm New Password"
            type="password"
            value={passwordForm.confirmPassword}
            onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
            required
            minLength={6}
          />
          <div className="flex gap-4 pt-4">
            <Button
              type="button"
              onClick={() => setShowPasswordModal(false)}
              variant="outline"
              className="flex-1"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              loading={loading}
              className="flex-1"
            >
              Change Password
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default Profile;