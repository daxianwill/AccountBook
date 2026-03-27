package com.sym.accountbook.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import com.sym.accountbook.data.entity.TransactionWithCategory
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CsvExporter {

    fun exportTransactionsToCsv(
        context: Context,
        transactions: List<TransactionWithCategory>
    ): Uri? {
        if (transactions.isEmpty()) {
            Toast.makeText(context, "没有可导出的数据", Toast.LENGTH_SHORT).show()
            return null
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.CHINA)
        val fileName = "transactions_${dateFormat.format(Date())}.csv"

        val csvDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "exports"
        )
        if (!csvDir.exists()) {
            csvDir.mkdirs()
        }

        val csvFile = File(csvDir, fileName)

        return try {
            FileWriter(csvFile).use { writer ->
                writer.append("日期,类型,分类,金额,备注\n")

                val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                transactions.forEach { item ->
                    val dateStr = dateFormatter.format(item.transaction.date)
                    val typeStr = if (item.transaction.type.name == "EXPENSE") "支出" else "收入"
                    val categoryName = item.category?.name ?: "未分类"
                    val amount = "%.2f".format(item.transaction.amount)
                    val note = item.transaction.note.replace(",", "，")

                    writer.append("$dateStr,$typeStr,$categoryName,$amount,$note\n")
                }

                writer.flush()
            }

            Toast.makeText(context, "导出成功: ${csvFile.absolutePath}", Toast.LENGTH_LONG).show()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                csvFile
            )
        } catch (e: IOException) {
            Toast.makeText(context, "导出失败: ${e.message}", Toast.LENGTH_LONG).show()
            null
        }
    }

    fun shareCsvFile(context: Context, fileUri: Uri, fileName: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, fileUri)
            putExtra(Intent.EXTRA_SUBJECT, "记账本交易记录导出")
            putExtra(Intent.EXTRA_TEXT, "交易记录已导出为CSV文件")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(
                shareIntent,
                "分享CSV文件"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}
