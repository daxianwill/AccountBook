package com.sym.accountbook.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.Transaction
import com.sym.accountbook.data.entity.TransactionType
import com.sym.accountbook.data.repository.CategoryRepository
import com.sym.accountbook.data.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ImportResult(
    val success: Boolean,
    val totalCount: Int = 0,
    val successCount: Int = 0,
    val failedCount: Int = 0,
    val message: String = ""
)

data class CsvTransactionRow(
    val dateStr: String,
    val typeStr: String,
    val categoryStr: String,
    val amountStr: String,
    val note: String
)

object CsvImporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private val dateFormatAlt = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)

    suspend fun importFromCsv(
        context: Context,
        uri: Uri,
        transactionRepository: TransactionRepository,
        categoryRepository: CategoryRepository
    ): ImportResult = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext ImportResult(
                    success = false,
                    message = "无法打开文件"
                )

            val reader = BufferedReader(InputStreamReader(inputStream))
            val lines = reader.readLines()
            reader.close()

            if (lines.size <= 1) {
                return@withContext ImportResult(
                    success = false,
                    message = "CSV文件为空"
                )
            }

            // 获取所有分类用于匹配
            val allCategories = categoryRepository.getAllCategoriesOnce()
            val categoryMap = allCategories.associateBy { it.name }

            var successCount = 0
            var failedCount = 0
            val failedRows = mutableListOf<String>()

            // 跳过表头，从第二行开始
            for (i in 1 until lines.size) {
                val line = lines[i].trim()
                if (line.isEmpty()) continue

                val row = parseCsvRow(line)
                val result = processRow(
                    row = row,
                    lineNumber = i + 1,
                    categoryMap = categoryMap,
                    categoryRepository = categoryRepository
                )

                if (result.first != null) {
                    transactionRepository.insertTransaction(result.first!!)
                    successCount++
                } else {
                    failedCount++
                    failedRows.add("第${i + 1}行: ${result.second}")
                }
            }

            val message = if (failedCount > 0) {
                "导入完成，成功 $successCount 条，失败 $failedCount 条。${failedRows.take(5).joinToString("；")}"
            } else {
                "导入完成，成功导入 $successCount 条记录"
            }

            ImportResult(
                success = true,
                totalCount = lines.size - 1,
                successCount = successCount,
                failedCount = failedCount,
                message = message
            )

        } catch (e: Exception) {
            ImportResult(
                success = false,
                message = "导入失败: ${e.message}"
            )
        }
    }

    private fun parseCsvRow(line: String): CsvTransactionRow {
        // 简单的 CSV 解析，假设没有嵌套逗号
        val parts = line.split(",").map { it.trim() }
        return CsvTransactionRow(
            dateStr = parts.getOrNull(0) ?: "",
            typeStr = parts.getOrNull(1) ?: "",
            categoryStr = parts.getOrNull(2) ?: "",
            amountStr = parts.getOrNull(3) ?: "",
            note = parts.getOrNull(4) ?: ""
        )
    }

    private suspend fun processRow(
        row: CsvTransactionRow,
        lineNumber: Int,
        categoryMap: Map<String, Category>,
        categoryRepository: CategoryRepository
    ): Pair<Transaction?, String> {

        // 验证日期
        val date = parseDate(row.dateStr)
            ?: return null to "日期格式错误: ${row.dateStr}"

        // 验证类型
        val type = when (row.typeStr) {
            "支出", "expense", "EXPENSE" -> TransactionType.EXPENSE
            "收入", "income", "INCOME" -> TransactionType.INCOME
            else -> return null to "未知类型: ${row.typeStr}"
        }

        // 验证金额
        val amount = row.amountStr.toDoubleOrNull()
            ?: return null to "金额格式错误: ${row.amountStr}"

        if (amount <= 0) {
            return null to "金额必须大于0: ${row.amountStr}"
        }

        // 查找或创建分类
        val categoryName = row.categoryStr.takeIf { it.isNotBlank() } ?: "未分类"
        var category = categoryMap[categoryName]

        // 如果分类不存在，创建一个新分类
        if (category == null) {
            val colors = listOf(0xFF2196F3, 0xFF9C27B0, 0xFFFF9800, 0xFF00BCD4, 0xFFF44336)
            val icons = listOf("🏷️", "📁", "📌", "🎯", "💡", "⭐")
            val newCategory = Category(
                name = categoryName,
                icon = icons.random(),
                type = type,
                color = colors.random()
            )
            val categoryId = categoryRepository.insertCategory(newCategory)
            category = newCategory.copy(id = categoryId)
        }

        // 创建交易记录
        val transaction = Transaction(
            type = type,
            categoryId = category.id,
            amount = amount,
            date = date,
            note = row.note
        )

        return transaction to ""
    }

    private fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            try {
                dateFormatAlt.parse(dateStr)
            } catch (e: Exception) {
                null
            }
        }
    }
}
