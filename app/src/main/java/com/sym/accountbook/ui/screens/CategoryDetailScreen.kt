package com.sym.accountbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sym.accountbook.data.entity.TransactionWithCategory
import com.sym.accountbook.ui.theme.SoftRed
import com.sym.accountbook.ui.viewmodel.TransactionViewModel
import com.sym.accountbook.ui.viewmodel.TransactionViewModelFactory
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavController,
    year: Int,
    month: Int,
    categoryName: String
) {
    // 解码URL编码的分类名称
    val decodedCategoryName = remember(categoryName) {
        URLDecoder.decode(categoryName, StandardCharsets.UTF_8.toString())
    }
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(context.applicationContext as android.app.Application)
    )

    // 获取该分类下的所有交易
    val transactionsWithCategory by transactionViewModel.allTransactionsWithCategory
        .observeAsState(initial = emptyList())

    // 筛选指定年月和分类的交易（只显示支出）
    val filteredTransactions = remember(transactionsWithCategory, year, month, decodedCategoryName) {
        transactionsWithCategory.filter { item ->
            val itemCalendar = Calendar.getInstance()
            itemCalendar.time = item.transaction.date
            itemCalendar.get(Calendar.YEAR) == year &&
                    itemCalendar.get(Calendar.MONTH) == month &&
                    item.category?.name == decodedCategoryName &&
                    item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE
        }
    }

    // 计算总金额
    val totalAmount = filteredTransactions.sumOf { it.transaction.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedCategoryName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 汇总信息
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${year}年${month + 1}月 · 支出汇总",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "笔数：${filteredTransactions.size} 笔",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "总计：¥ %.2f".format(totalAmount),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SoftRed
                        )
                    }
                }
            }

            // 交易列表
            if (filteredTransactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "暂无交易记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTransactions) { item ->
                        TransactionDetailItem(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionDetailItem(item: TransactionWithCategory) {
    val dateFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.CHINA)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 日期和备注
                Text(
                    text = if (item.transaction.note.isNotBlank()) 
                        item.transaction.note else "无备注",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${dateFormat.format(item.transaction.date)} ${timeFormat.format(item.transaction.date)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 金额
            Text(
                text = "-¥ %.2f".format(item.transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SoftRed
            )
        }
    }
}
