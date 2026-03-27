package com.sym.accountbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sym.accountbook.ui.theme.SoftGreen
import com.sym.accountbook.ui.theme.SoftRed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight
import com.sym.accountbook.ui.components.AnnualBarChart
import com.sym.accountbook.ui.components.AnnualData
import com.sym.accountbook.ui.components.CategoryExpense
import com.sym.accountbook.ui.components.DailyExpense
import com.sym.accountbook.ui.components.ExpensePieChart
import com.sym.accountbook.ui.components.MonthlyBarChart
import com.sym.accountbook.ui.components.MonthlyData
import com.sym.accountbook.ui.components.TrendLineChart
import com.sym.accountbook.ui.navigation.Screen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import com.sym.accountbook.ui.viewmodel.TransactionViewModel
import com.sym.accountbook.ui.viewmodel.TransactionViewModelFactory
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(navController: NavController) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(context.applicationContext as android.app.Application)
    )

    // 年月选择状态
    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }

    // 计算选中月份的开始和结束日期
    val (startDate, endDate) = calculateDateRange(selectedYear, selectedMonth)

    // 获取分类支出数据
    val transactionsWithCategory by transactionViewModel.allTransactionsWithCategory
        .observeAsState(initial = emptyList())

    // 根据选中的年月筛选交易数据
    val filteredTransactions = remember(transactionsWithCategory, selectedYear, selectedMonth) {
        transactionsWithCategory.filter { item ->
            val itemCalendar = Calendar.getInstance()
            itemCalendar.time = item.transaction.date
            itemCalendar.get(Calendar.YEAR) == selectedYear &&
                    itemCalendar.get(Calendar.MONTH) == selectedMonth
        }
    }

    // 计算分类支出统计
    val monthTotalExpense = filteredTransactions
        .filter { it.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE }
        .sumOf { it.transaction.amount }

    val monthTotalIncome = filteredTransactions
        .filter { it.transaction.type == com.sym.accountbook.data.entity.TransactionType.INCOME }
        .sumOf { it.transaction.amount }

    val balance = monthTotalIncome - monthTotalExpense

    val categoryExpenses = calculateCategoryExpenses(filteredTransactions, monthTotalExpense)

    // 计算每日收支趋势数据
    val dailyExpenses = calculateDailyTrend(filteredTransactions)

    // 计算月度收支数据
    val monthlyData = calculateMonthlyData(transactionsWithCategory)

    // 计算年度收支数据
    val annualData = calculateAnnualData(transactionsWithCategory)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 年月选择器
            MonthYearPicker(
                selectedYear = selectedYear,
                selectedMonth = selectedMonth,
                onYearChange = { selectedYear = it },
                onMonthChange = { selectedMonth = it }
            )

            StatisticCard(
                title = "总收入",
                amount = monthTotalIncome,
                isPositive = true
            )

            StatisticCard(
                title = "总支出",
                amount = monthTotalExpense,
                isPositive = false
            )

            StatisticCard(
                title = "结余",
                amount = balance,
                isPositive = balance >= 0
            )

            // 饼图
            ExpensePieChart(
                categoryExpenses = categoryExpenses,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 支出分类列表
            CategoryExpenseList(
                categoryExpenses = categoryExpenses,
                onCategoryClick = { categoryName ->
                    // 导航到分类详情页面，处理中文编码
                    val encodedName = URLEncoder.encode(categoryName, StandardCharsets.UTF_8.toString())
                    navController.navigate("categoryDetail/$selectedYear/$selectedMonth/$encodedName")
                }
            )

            // 折线图
            TrendLineChart(
                dailyData = dailyExpenses,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 月度条形图
            MonthlyBarChart(
                monthlyData = monthlyData,
                modifier = Modifier.padding(top = 8.dp)
            )

            // 年度条形图
            AnnualBarChart(
                annualData = annualData,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun calculateDailyTrend(
    transactionsWithCategory: List<com.sym.accountbook.data.entity.TransactionWithCategory>
): List<DailyExpense> {
    if (transactionsWithCategory.isEmpty()) return emptyList()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val dailyMap = mutableMapOf<String, DailyTotal>()

    transactionsWithCategory.forEach { item ->
        val dateStr = dateFormat.format(item.transaction.date)
        val existing = dailyMap[dateStr]
        if (existing != null) {
            if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE) {
                existing.expense += item.transaction.amount
            } else {
                existing.income += item.transaction.amount
            }
        } else {
            dailyMap[dateStr] = DailyTotal(
                date = item.transaction.date,
                expense = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE)
                    item.transaction.amount else 0.0,
                income = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.INCOME)
                    item.transaction.amount else 0.0
            )
        }
    }

    return dailyMap.values
        .sortedBy { it.date }
        .takeLast(14)
        .map {
            DailyExpense(
                date = it.date,
                expense = it.expense,
                income = it.income
            )
        }
}

private data class DailyTotal(
    val date: Date,
    var expense: Double,
    var income: Double
)

private fun calculateCategoryExpenses(
    transactionsWithCategory: List<com.sym.accountbook.data.entity.TransactionWithCategory>,
    totalExpense: Double
): List<CategoryExpense> {
    if (totalExpense <= 0) return emptyList()

    val categoryMap = mutableMapOf<String, CategoryTotal>()
    val colors = listOf(
        0xFF2196F3, 0xFF9C27B0, 0xFFFF9800, 0xFF00BCD4,
        0xFFF44336, 0xFF795548, 0xFF4CAF50, 0xFFFFEB3B,
        0xFFE91E63, 0xFF009688
    )

    transactionsWithCategory
        .filter { it.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE }
        .forEach { item ->
            val categoryName = item.category?.name ?: "未分类"
            val categoryColor = item.category?.color ?: colors.random()

            val existing = categoryMap[categoryName]
            if (existing != null) {
                existing.total += item.transaction.amount
            } else {
                categoryMap[categoryName] = CategoryTotal(categoryName, item.transaction.amount, categoryColor)
            }
        }

    return categoryMap.values
        .sortedByDescending { it.total }
        .map {
            CategoryExpense(
                categoryName = it.name,
                amount = it.total,
                color = it.color,
                percentage = (it.total / totalExpense * 100).toFloat()
            )
        }
}

private data class CategoryTotal(
    val name: String,
    var total: Double,
    val color: Long
)

// 计算月度收支数据
private fun calculateMonthlyData(
    transactionsWithCategory: List<com.sym.accountbook.data.entity.TransactionWithCategory>
): List<MonthlyData> {
    if (transactionsWithCategory.isEmpty()) return emptyList()

    val monthFormat = SimpleDateFormat("yyyy-MM", Locale.CHINA)
    val monthDisplayFormat = SimpleDateFormat("MM月", Locale.CHINA)
    val monthlyMap = mutableMapOf<String, MonthTotal>()

    transactionsWithCategory.forEach { item ->
        val monthKey = monthFormat.format(item.transaction.date)
        val monthDisplay = monthDisplayFormat.format(item.transaction.date)

        val existing = monthlyMap[monthKey]
        if (existing != null) {
            if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE) {
                existing.expense += item.transaction.amount
            } else {
                existing.income += item.transaction.amount
            }
        } else {
            monthlyMap[monthKey] = MonthTotal(
                monthKey = monthKey,
                monthDisplay = monthDisplay,
                expense = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE)
                    item.transaction.amount else 0.0,
                income = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.INCOME)
                    item.transaction.amount else 0.0
            )
        }
    }

    return monthlyMap.values
        .sortedBy { it.monthKey }
        .map {
            MonthlyData(
                month = it.monthDisplay,
                expense = it.expense,
                income = it.income
            )
        }
}

private data class MonthTotal(
    val monthKey: String,
    val monthDisplay: String,
    var expense: Double,
    var income: Double
)

// 计算年度收支数据
private fun calculateAnnualData(
    transactionsWithCategory: List<com.sym.accountbook.data.entity.TransactionWithCategory>
): List<AnnualData> {
    if (transactionsWithCategory.isEmpty()) return emptyList()

    val yearFormat = SimpleDateFormat("yyyy", Locale.CHINA)
    val annualMap = mutableMapOf<Int, YearTotal>()

    transactionsWithCategory.forEach { item ->
        val yearStr = yearFormat.format(item.transaction.date)
        val year = yearStr.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)

        val existing = annualMap[year]
        if (existing != null) {
            if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE) {
                existing.expense += item.transaction.amount
            } else {
                existing.income += item.transaction.amount
            }
        } else {
            annualMap[year] = YearTotal(
                year = year,
                expense = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.EXPENSE)
                    item.transaction.amount else 0.0,
                income = if (item.transaction.type == com.sym.accountbook.data.entity.TransactionType.INCOME)
                    item.transaction.amount else 0.0
            )
        }
    }

    return annualMap.values
        .sortedBy { it.year }
        .map {
            AnnualData(
                year = it.year,
                expense = it.expense,
                income = it.income
            )
        }
}

private data class YearTotal(
    val year: Int,
    var expense: Double,
    var income: Double
)

@Composable
fun StatisticCard(title: String, amount: Double, isPositive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "¥ %.2f".format(amount),
                style = MaterialTheme.typography.headlineSmall,
                color = if (isPositive) SoftGreen else SoftRed,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// 年月选择器组件
@Composable
fun MonthYearPicker(
    selectedYear: Int,
    selectedMonth: Int,
    onYearChange: (Int) -> Unit,
    onMonthChange: (Int) -> Unit
) {
    val monthNames = listOf(
        "1月", "2月", "3月", "4月", "5月", "6月",
        "7月", "8月", "9月", "10月", "11月", "12月"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 年份选择
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onYearChange(selectedYear - 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "上一年",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "$selectedYear 年",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { onYearChange(selectedYear + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "下一年",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 月份选择
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (selectedMonth > 0) {
                            onMonthChange(selectedMonth - 1)
                        } else {
                            onYearChange(selectedYear - 1)
                            onMonthChange(11)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "上一月",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    text = monthNames[selectedMonth],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )

                IconButton(
                    onClick = {
                        if (selectedMonth < 11) {
                            onMonthChange(selectedMonth + 1)
                        } else {
                            onYearChange(selectedYear + 1)
                            onMonthChange(0)
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "下一月",
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

// 计算日期范围
private fun calculateDateRange(year: Int, month: Int): Pair<Date, Date> {
    val calendar = Calendar.getInstance()

    // 开始日期：当月第一天 00:00:00
    calendar.set(year, month, 1, 0, 0, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startDate = calendar.time

    // 结束日期：当月最后一天 23:59:59
    calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endDate = calendar.time

    return Pair(startDate, endDate)
}

// 支出分类列表组件
@Composable
fun CategoryExpenseList(
    categoryExpenses: List<CategoryExpense>,
    onCategoryClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "支出分类明细",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (categoryExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无支出数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                categoryExpenses.forEachIndexed { index, category ->
                    CategoryExpenseListItem(
                        category = category,
                        rank = index + 1,
                        onClick = { onCategoryClick(category.categoryName) }
                    )
                }
            }
        }
    }
}

// 分类列表项组件
@Composable
fun CategoryExpenseListItem(
    category: CategoryExpense,
    rank: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 排名标签
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = when (rank) {
                            1 -> Color(0xFFFFD700) // 金色
                            2 -> Color(0xFFC0C0C0) // 银色
                            3 -> Color(0xFFCD7F32) // 铜色
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 分类颜色指示器
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = Color(category.color),
                        shape = CircleShape
                    )
            )

            // 分类名称
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 金额
            Text(
                text = "¥ %.2f".format(category.amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = SoftRed
            )

            // 占比
            Text(
                text = "%.1f%%".format(category.percentage),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 箭头图标
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = "查看详情",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
