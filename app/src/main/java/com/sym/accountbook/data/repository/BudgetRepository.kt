package com.sym.accountbook.data.repository

import android.app.Application
import com.sym.accountbook.data.AppDatabase
import com.sym.accountbook.data.dao.BudgetDao
import com.sym.accountbook.data.entity.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(application: Application) {
    private val budgetDao: BudgetDao = AppDatabase.getDatabase(application).budgetDao()

    fun getEnabledBudgets(): Flow<List<Budget>> {
        return budgetDao.getEnabledBudgets()
    }

    fun getTotalBudget(): Flow<Budget?> {
        return budgetDao.getTotalBudget()
    }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budgetId: Long) {
        budgetDao.deleteBudget(budgetId)
    }
}
