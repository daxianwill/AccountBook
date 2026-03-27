package com.sym.accountbook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sym.accountbook.data.dao.BudgetDao
import com.sym.accountbook.data.dao.CategoryDao
import com.sym.accountbook.data.dao.TransactionDao
import com.sym.accountbook.data.entity.Budget
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.Transaction

@Database(
    entities = [Transaction::class, Category::class, Budget::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
