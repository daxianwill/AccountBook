package com.sym.accountbook.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TransactionType {
    INCOME,
    EXPENSE
}

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: TransactionType,
    val categoryId: Long,
    val amount: Double,
    val date: Date,
    val note: String = ""
)
