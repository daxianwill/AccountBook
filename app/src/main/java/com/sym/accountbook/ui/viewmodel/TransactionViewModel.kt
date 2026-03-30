package com.sym.accountbook.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sym.accountbook.data.AppDatabase
import com.sym.accountbook.data.entity.Transaction
import com.sym.accountbook.data.entity.TransactionWithCategory
import com.sym.accountbook.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.Date

class TransactionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransactionRepository
    val allTransactions: LiveData<List<Transaction>>
    val allTransactionsWithCategory: LiveData<List<TransactionWithCategory>>
    
    // Flow versions for better performance
    val allTransactionsFlow: Flow<List<Transaction>>
    val allTransactionsWithCategoryFlow: Flow<List<TransactionWithCategory>>

    init {
        val transactionDao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(transactionDao)
        allTransactionsFlow = repository.allTransactions
        allTransactionsWithCategoryFlow = repository.allTransactionsWithCategory
        allTransactions = allTransactionsFlow.asLiveData()
        allTransactionsWithCategory = allTransactionsWithCategoryFlow.asLiveData()
    }

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): LiveData<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate).asLiveData()
    }
    
    fun getTransactionsByDateRangeFlow(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return repository.getTransactionsByDateRange(startDate, endDate)
    }

    fun getTransactionsWithCategoryByDateRange(startDate: Date, endDate: Date): Flow<List<TransactionWithCategory>> {
        return repository.getTransactionsWithCategoryByDateRange(startDate, endDate)
    }

    fun getTotalExpense(startDate: Date, endDate: Date): LiveData<Double> {
        return repository.getTotalExpense(startDate, endDate).asLiveData()
    }
    
    fun getTotalExpenseFlow(startDate: Date, endDate: Date): Flow<Double> {
        return repository.getTotalExpense(startDate, endDate)
    }

    fun getTotalIncome(startDate: Date, endDate: Date): LiveData<Double> {
        return repository.getTotalIncome(startDate, endDate).asLiveData()
    }
    
    fun getTotalIncomeFlow(startDate: Date, endDate: Date): Flow<Double> {
        return repository.getTotalIncome(startDate, endDate)
    }

    fun getCurrentMonthExpense(): LiveData<Double> {
        return repository.getCurrentMonthExpense().asLiveData()
    }
    
    fun getCurrentMonthExpenseFlow(): Flow<Double> {
        return repository.getCurrentMonthExpense()
    }

    fun getCurrentMonthIncome(): LiveData<Double> {
        return repository.getCurrentMonthIncome().asLiveData()
    }
    
    fun getCurrentMonthIncomeFlow(): Flow<Double> {
        return repository.getCurrentMonthIncome()
    }

    fun insertTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.insertTransaction(transaction)
    }

    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.updateTransaction(transaction)
    }

    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.deleteTransaction(transaction)
    }

    suspend fun getTransactionById(transactionId: Long): Transaction? {
        return repository.getTransactionById(transactionId).firstOrNull()
    }
}
