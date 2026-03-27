package com.sym.accountbook.data

import android.content.Context
import androidx.room.Room
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseInitializer {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_ICON_MIGRATED = "icon_migrated"

    fun initialize(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
        val isIconMigrated = sharedPrefs.getBoolean(KEY_ICON_MIGRATED, false)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            if (isFirstLaunch) {
                insertDefaultCategories(db)
                sharedPrefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
            } else if (!isIconMigrated) {
                // 迁移现有分类的图标为 emoji
                migrateIconsToEmoji(db)
                sharedPrefs.edit().putBoolean(KEY_ICON_MIGRATED, true).apply()
            }
        }
    }

    private suspend fun migrateIconsToEmoji(db: AppDatabase) {
        val iconMap = mapOf(
            "directions_car" to "🚗",
            "swap_horiz" to "💸",
            "shopping_cart" to "🛒",
            "flight" to "✈️",
            "restaurant" to "🍜",
            "people" to "🎁",
            "attach_money" to "💰",
            "star" to "🌟",
            "trending_up" to "📈",
            "add_circle" to "💵",
            "category" to "📁"
        )

        val categories = db.categoryDao().getAllCategoriesOnce()
        categories.forEach { category ->
            val newIcon = iconMap[category.icon] ?: category.icon
            if (newIcon != category.icon) {
                val updatedCategory = category.copy(icon = newIcon)
                db.categoryDao().updateCategory(updatedCategory)
            }
        }
    }

    private suspend fun insertDefaultCategories(db: AppDatabase) {
        val defaultExpenseCategories = listOf(
            Category(name = "交通", icon = "🚗", type = TransactionType.EXPENSE, color = 0xFF2196F3),
            Category(name = "转账", icon = "💸", type = TransactionType.EXPENSE, color = 0xFF9C27B0),
            Category(name = "购物", icon = "🛒", type = TransactionType.EXPENSE, color = 0xFFFF9800),
            Category(name = "旅行", icon = "✈️", type = TransactionType.EXPENSE, color = 0xFF00BCD4),
            Category(name = "餐饮", icon = "🍜", type = TransactionType.EXPENSE, color = 0xFFF44336),
            Category(name = "其他人情", icon = "🎁", type = TransactionType.EXPENSE, color = 0xFF795548)
        )

        val defaultIncomeCategories = listOf(
            Category(name = "工资", icon = "💰", type = TransactionType.INCOME, color = 0xFF4CAF50),
            Category(name = "奖金", icon = "🌟", type = TransactionType.INCOME, color = 0xFFFFEB3B),
            Category(name = "投资收益", icon = "📈", type = TransactionType.INCOME, color = 0xFF8BC34A),
            Category(name = "其他收入", icon = "💵", type = TransactionType.INCOME, color = 0xFF607D8B)
        )

        defaultExpenseCategories.forEach { db.categoryDao().insertCategory(it) }
        defaultIncomeCategories.forEach { db.categoryDao().insertCategory(it) }
    }
}
