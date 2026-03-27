package com.sym.accountbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long? = null,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val isEnabled: Boolean = true
)

enum class BudgetPeriod {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
