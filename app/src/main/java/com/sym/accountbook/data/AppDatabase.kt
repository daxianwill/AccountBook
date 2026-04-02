package com.sym.accountbook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sym.accountbook.data.dao.BudgetDao
import com.sym.accountbook.data.dao.CategoryDao
import com.sym.accountbook.data.dao.TransactionDao
import com.sym.accountbook.data.entity.Budget
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.Transaction

@Database(
    entities = [Transaction::class, Category::class, Budget::class],
    version = 3,
    exportSchema = true
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
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
                .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加showInMonthStats列，默认值为1（true）
                database.execSQL("ALTER TABLE categories ADD COLUMN showInMonthStats INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加showInMonthStats列，默认值为1（true）
                database.execSQL("ALTER TABLE categories ADD COLUMN showInMonthStats INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}
