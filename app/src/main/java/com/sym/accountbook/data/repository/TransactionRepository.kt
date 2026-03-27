package com.sym.accountbook.data.repository

import com.sym.accountbook.data.dao.TransactionDao
import com.sym.accountbook.data.entity.Transaction
import com.sym.accountbook.data.entity.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

class TransactionRepository(private val transactionDao: TransactionDao) {
    val allTransactions: Flow<List<Transaction>> = transactionDao.getAllTransactions()
    val allTransactionsWithCategory: Flow<List<TransactionWithCategory>> = transactionDao.getAllTransactionsWithCategory()

    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    fun getTransactionById(transactionId: Long): Flow<Transaction?> {
        return transactionDao.getTransactionById(transactionId)
    }

    fun getTotalExpense(startDate: Date, endDate: Date): Flow<Double> {
        return transactionDao.getTotalExpense(startDate, endDate)
    }

    fun getTotalIncome(startDate: Date, endDate: Date): Flow<Double> {
        return transactionDao.getTotalIncome(startDate, endDate)
    }

    fun getCurrentMonthExpense(): Flow<Double> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time
        return transactionDao.getTotalExpense(startDate, endDate)
    }

    fun getCurrentMonthIncome(): Flow<Double> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time
        return transactionDao.getTotalIncome(startDate, endDate)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction)
    }
}
