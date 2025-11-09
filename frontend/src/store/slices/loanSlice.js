import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import loanService from '../../services/loanService';

export const fetchCustomerLoans = createAsyncThunk(
  'loans/fetchCustomerLoans',
  async (customerId, { rejectWithValue }) => {
    try {
      const data = await loanService.getCustomerLoans(customerId);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to fetch loans');
    }
  }
);

export const createLoan = createAsyncThunk(
  'loans/createLoan',
  async (loanData, { rejectWithValue }) => {
    try {
      const data = await loanService.createLoan(loanData);
      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data?.message || 'Failed to create loan');
    }
  }
);

const loanSlice = createSlice({
  name: 'loans',
  initialState: {
    loans: [],
    loading: false,
    error: null,
  },
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchCustomerLoans.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchCustomerLoans.fulfilled, (state, action) => {
        state.loading = false;
        state.loans = action.payload;
      })
      .addCase(fetchCustomerLoans.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(createLoan.fulfilled, (state, action) => {
        state.loans.push(action.payload);
      });
  },
});

export const { clearError } = loanSlice.actions;
export default loanSlice.reducer;
