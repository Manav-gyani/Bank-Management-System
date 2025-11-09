import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import accountService from '../../services/accountService';

export const fetchCustomerAccounts = createAsyncThunk(
  'accounts/fetchCustomerAccounts',
  async (customerId, { rejectWithValue }) => {
    try {
      const data = await accountService.getCustomerAccounts(customerId);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch accounts');
    }
  }
);

export const createAccount = createAsyncThunk(
  'accounts/createAccount',
  async ({ customerId, accountType }, { rejectWithValue }) => {
    try {
      const data = await accountService.createAccount(customerId, accountType);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create account');
    }
  }
);

export const deposit = createAsyncThunk(
  'accounts/deposit',
  async ({ accountNumber, amount, description }, { rejectWithValue }) => {
    try {
      const data = await accountService.deposit(accountNumber, amount, description);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Deposit failed');
    }
  }
);

export const withdraw = createAsyncThunk(
  'accounts/withdraw',
  async ({ accountNumber, amount, description }, { rejectWithValue }) => {
    try {
      const data = await accountService.withdraw(accountNumber, amount, description);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Withdrawal failed');
    }
  }
);

export const transfer = createAsyncThunk(
  'accounts/transfer',
  async ({ fromAccount, toAccount, amount, description }, { rejectWithValue }) => {
    try {
      const data = await accountService.transfer(fromAccount, toAccount, amount, description);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Transfer failed');
    }
  }
);

const accountSlice = createSlice({
  name: 'accounts',
  initialState: {
    accounts: [],
    currentAccount: null,
    loading: false,
    error: null,
    successMessage: null,
  },
  reducers: {
    setCurrentAccount: (state, action) => {
      state.currentAccount = action.payload;
    },
    clearError: (state) => {
      state.error = null;
    },
    clearSuccessMessage: (state) => {
      state.successMessage = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch accounts
      .addCase(fetchCustomerAccounts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomerAccounts.fulfilled, (state, action) => {
        state.loading = false;
        state.accounts = action.payload;
      })
      .addCase(fetchCustomerAccounts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Create account
      .addCase(createAccount.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createAccount.fulfilled, (state, action) => {
        state.loading = false;
        state.accounts.push(action.payload);
        state.successMessage = 'Account created successfully';
      })
      .addCase(createAccount.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Deposit
      .addCase(deposit.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deposit.fulfilled, (state) => {
        state.loading = false;
        state.successMessage = 'Deposit successful';
      })
      .addCase(deposit.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Withdraw
      .addCase(withdraw.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(withdraw.fulfilled, (state) => {
        state.loading = false;
        state.successMessage = 'Withdrawal successful';
      })
      .addCase(withdraw.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      // Transfer
      .addCase(transfer.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(transfer.fulfilled, (state) => {
        state.loading = false;
        state.successMessage = 'Transfer successful';
      })
      .addCase(transfer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const { setCurrentAccount, clearError, clearSuccessMessage } = accountSlice.actions;
export default accountSlice.reducer;