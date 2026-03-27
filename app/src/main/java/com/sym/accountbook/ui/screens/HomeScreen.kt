package com.sym.accountbook.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sym.accountbook.ui.theme.SoftGreen
import com.sym.accountbook.ui.theme.SoftRed
import com.sym.accountbook.ui.theme.SoftYellow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import android.app.Application
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sym.accountbook.data.entity.TransactionType
import com.sym.accountbook.data.entity.TransactionWithCategory
import com.sym.accountbook.ui.navigation.Screen
import com.sym.accountbook.ui.viewmodel.BudgetViewModel
import com.sym.accountbook.ui.viewmodel.BudgetViewModelFactory
import com.sym.accountbook.ui.viewmodel.TransactionViewModel
import com.sym.accountbook.ui.viewmodel.TransactionViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(context.applicationContext as android.app.Application)
    )
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(context.applicationContext as android.app.Application)
    )

    var totalExpense by remember { mutableStateOf(0.0) }
    var totalIncome by remember { mutableStateOf(0.0) }

    val totalBudget by budgetViewModel.totalBudget.observeAsState()

    // 月份筛选状态
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)

    var selectedYear by remember { mutableStateOf(currentYear) }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var showMonthPicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time
    }

    val transactionsWithCategory by transactionViewModel.allTransactionsWithCategory
        .observeAsState(initial = emptyList())

    // 根据选中的月份筛选交易记录
    val filteredTransactions = remember(transactionsWithCategory, selectedYear, selectedMonth) {
        transactionsWithCategory.filter { item ->
            val itemCalendar = Calendar.getInstance()
            itemCalendar.time = item.transaction.date
            itemCalendar.get(Calendar.YEAR) == selectedYear &&
                    itemCalendar.get(Calendar.MONTH) == selectedMonth
        }
    }

    // Calculate summary based on filtered transactions
    LaunchedEffect(filteredTransactions) {
        totalExpense = filteredTransactions
            .filter { it.transaction.type == TransactionType.EXPENSE }
            .sumOf { it.transaction.amount }

        totalIncome = filteredTransactions
            .filter { it.transaction.type == TransactionType.INCOME }
            .sumOf { it.transaction.amount }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("喵喵记账本") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CategoryManager.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "分类管理")
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "设置")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddTransaction.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "添加记录")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SummaryCard(
                totalExpense = totalExpense,
                totalIncome = totalIncome,
                budget = totalBudget,
                onBudgetSettingClick = { navController.navigate(Screen.BudgetSetting.route) }
            )

            // 月份选择器和最近记录标题
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "最近记录",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // 月份选择按钮
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showMonthPicker = true }
                ) {
                    Text(
                        text = String.format("%d年%d月", selectedYear, selectedMonth + 1),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = "选择月份",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            TransactionList(transactions = filteredTransactions, navController = navController)

            // 月份选择对话框
            if (showMonthPicker) {
                MonthPickerDialog(
                    initialYear = selectedYear,
                    initialMonth = selectedMonth,
                    onDismiss = { showMonthPicker = false },
                    onConfirm = { year, month ->
                        selectedYear = year
                        selectedMonth = month
                        showMonthPicker = false
                    }
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    totalExpense: Double,
    totalIncome: Double,
    budget: com.sym.accountbook.data.entity.Budget?,
    onBudgetSettingClick: () -> Unit
) {
    val balance = totalIncome - totalExpense

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "本月支出",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "¥ %.2f".format(totalExpense),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SoftRed
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "本月收入",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "¥ %.2f".format(totalIncome),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.Green
                    )
                }
            }

            // 预算信息
            if (budget?.isEnabled == true && budget.amount > 0) {
                val remaining = budget.amount - totalExpense
                val usageRate = if (budget.amount > 0) totalExpense / budget.amount else 0.0

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onBudgetSettingClick)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "月度预算",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                Icons.Filled.ArrowForward,
                                contentDescription = "修改预算",
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Text(
                            text = "¥ %.2f".format(budget.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (remaining >= 0) "剩余 ¥ %.2f".format(remaining) else "超支 ¥ %.2f".format(-remaining),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (remaining >= 0) SoftGreen else SoftRed
                        )
                        Text(
                            text = "%.0f%%".format(usageRate * 100),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    // 预算进度条
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (usageRate > 1) 1f else usageRate.toFloat())
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (usageRate > 0.9) SoftRed else if (usageRate > 0.7) SoftYellow else SoftGreen)
                        )
                    }

                    // 预算提醒
                    if (usageRate > 0.9) {
                        Text(
                            text = if (usageRate > 1) "⚠️ 已超出预算！" else "⚠️ 预算即将用尽！",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftRed,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                // 未设置预算时显示设置入口
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onBudgetSettingClick),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "设置月度预算",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        Icons.Filled.ArrowForward,
                        contentDescription = "设置预算",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "本月结余",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "¥ %.2f".format(balance),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (balance >= 0) SoftGreen else SoftRed
                )
            }
        }
    }
}

@Composable
fun TransactionList(transactions: List<TransactionWithCategory>, navController: NavController) {
    if (transactions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "暂无记录，点击 + 添加一笔",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        // 按日期分组
        val groupedTransactions = remember(transactions) {
            transactions.groupBy { item ->
                val calendar = Calendar.getInstance()
                calendar.time = item.transaction.date
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            }.toSortedMap(compareByDescending { it })
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            groupedTransactions.forEach { (date, items) ->
                // 日期标题
                item {
                    DateHeader(date = date)
                }
                // 当日交易记录
                items(items) { item ->
                    TransactionItem(
                        item = item,
                        onClick = {
                            navController.navigate(Screen.EditTransaction.createRoute(item.transaction.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: java.util.Date) {
    val dateFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)
    val dayFormat = SimpleDateFormat("EEEE", Locale.CHINA)

    val now = Calendar.getInstance()
    
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val yesterday = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val dateCalendar = Calendar.getInstance().apply {
        time = date
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    val displayText = when {
        isSameDay(dateCalendar, today) -> "今天"
        isSameDay(dateCalendar, yesterday) -> "昨天"
        else -> dateFormat.format(date)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = " · ${dayFormat.format(date)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TransactionItem(item: TransactionWithCategory, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("MM月dd日", Locale.CHINA)
    val isExpense = item.transaction.type == TransactionType.EXPENSE

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(end = 16.dp)) {
                    Text(
                        text = item.category?.name ?: "未分类",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (item.transaction.note.isNotBlank()) item.transaction.note 
                               else dateFormat.format(item.transaction.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isExpense) "-¥ %.2f".format(item.transaction.amount) 
                           else "+¥ %.2f".format(item.transaction.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) SoftRed else SoftGreen,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    Icons.Filled.ArrowForward,
                    contentDescription = "详情",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// 月份选择对话框
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthPickerDialog(
    initialYear: Int,
    initialMonth: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var tempYear by remember { mutableStateOf(initialYear) }
    var tempMonth by remember { mutableStateOf(initialMonth) }

    val months = (1..12).map { "${it}月" }
    val years = (2020..2030).toList()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择月份") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 年份选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "年份",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.width(60.dp)
                    )
                    IconButton(
                        onClick = { if (tempYear > 2020) tempYear-- }
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "减少")
                    }
                    Text(
                        text = "$tempYear 年",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = { if (tempYear < 2030) tempYear++ }
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "增加")
                    }
                }

                // 月份选择
                Text(
                    text = "月份",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(months) { month ->
                        val index = months.indexOf(month)
                        val isSelected = index == tempMonth
                        Card(
                            modifier = Modifier
                                .height(48.dp)
                                .clickable { tempMonth = index },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 0.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = month,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(tempYear, tempMonth) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
