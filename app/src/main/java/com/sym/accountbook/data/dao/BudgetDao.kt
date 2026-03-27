package com.sym.accountbook.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sym.accountbook.data.entity.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isEnabled = 1")
    fun getEnabledBudgets(): Flow<List<Budget>>

    @Query("SELECT * FROM budgets WHERE categoryId IS NULL AND isEnabled = 1 LIMIT 1")
    fun getTotalBudget(): Flow<Budget?>

    @Insert
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudget(budgetId: Long)
}
