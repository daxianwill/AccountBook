package com.sym.accountbook.ui.components

import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

data class CategoryExpense(
    val categoryName: String,
    val amount: Double,
    val color: Long,
    val percentage: Float
)

@Composable
fun ExpensePieChart(
    categoryExpenses: List<CategoryExpense>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "支出分类占比",
                style = MaterialTheme.typography.titleMedium
            )

            if (categoryExpenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    factory = { context ->
                        PieChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            description.isEnabled = false
                            setUsePercentValues(true)
                            setExtraOffsets(5f, 10f, 5f, 5f)
                            dragDecelerationFrictionCoef = 0.95f
                            isDrawHoleEnabled = true
                            setHoleColor(Color.WHITE)
                            setTransparentCircleColor(Color.WHITE)
                            setTransparentCircleAlpha(110)
                            holeRadius = 58f
                            transparentCircleRadius = 61f
                            setDrawCenterText(true)
                            rotationAngle = 0f
                            isRotationEnabled = true
                            isHighlightPerTapEnabled = true
                            legend.isEnabled = false
                            setEntryLabelColor(Color.BLACK)
                            setEntryLabelTextSize(12f)
                        }
                    },
                    update = { pieChart ->
                        val entries = categoryExpenses.map {
                            PieEntry(it.percentage, it.categoryName)
                        }
                        val colors = categoryExpenses.map { it.color.toInt() }

                        val dataSet = PieDataSet(entries, "支出分类").apply {
                            setDrawIcons(false)
                            sliceSpace = 3f
                            iconsOffset = MPPointF(0f, 40f)
                            selectionShift = 5f
                            this.colors = colors
                        }

                        val data = PieData(dataSet).apply {
                            setValueFormatter(PercentFormatter(pieChart))
                            setValueTextSize(11f)
                            setValueTextColor(Color.WHITE)
                        }

                        pieChart.data = data
                        pieChart.highlightValues(null)
                        pieChart.invalidate()
                    }
                )

                // Legend
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categoryExpenses) { item ->
                        LegendItem(
                            color = androidx.compose.ui.graphics.Color(item.color),
                            label = item.categoryName
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    color: androidx.compose.ui.graphics.Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

data class DailyExpense(
    val date: Date,
    val expense: Double,
    val income: Double
)

@Composable
fun TrendLineChart(
    dailyData: List<DailyExpense>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "收支趋势",
                style = MaterialTheme.typography.titleMedium
            )

            if (dailyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    factory = { context ->
                        LineChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            description.isEnabled = false
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                            setDrawGridBackground(false)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.setDrawGridLines(false)
                            xAxis.labelRotationAngle = -45f
                            xAxis.textSize = 10f
                            axisLeft.setDrawGridLines(true)
                            axisRight.isEnabled = false
                            legend.isEnabled = true
                            legend.textSize = 12f
                            setNoDataText("暂无数据")
                        }
                    },
                    update = { lineChart ->
                        val sortedData = dailyData.sortedBy { it.date }
                        val dateFormat = SimpleDateFormat("MM/dd", Locale.CHINA)

                        val expenseEntries = sortedData.mapIndexed { index, daily ->
                            Entry(index.toFloat(), daily.expense.toFloat())
                        }

                        val incomeEntries = sortedData.mapIndexed { index, daily ->
                            Entry(index.toFloat(), daily.income.toFloat())
                        }

                        val expenseDataSet = LineDataSet(expenseEntries, "支出").apply {
                            color = Color.RED
                            setCircleColor(Color.RED)
                            lineWidth = 2f
                            circleRadius = 3f
                            setDrawCircleHole(false)
                            valueTextSize = 9f
                            setDrawFilled(false)
                        }

                        val incomeDataSet = LineDataSet(incomeEntries, "收入").apply {
                            color = Color.GREEN
                            setCircleColor(Color.GREEN)
                            lineWidth = 2f
                            circleRadius = 3f
                            setDrawCircleHole(false)
                            valueTextSize = 9f
                            setDrawFilled(false)
                        }

                        val lineData = LineData(expenseDataSet, incomeDataSet)
                        lineChart.data = lineData

                        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                return if (index in sortedData.indices) {
                                    dateFormat.format(sortedData[index].date)
                                } else {
                                    ""
                                }
                            }
                        }

                        lineChart.xAxis.labelCount = minOf(7, sortedData.size)
                        lineChart.invalidate()
                    }
                )
            }
        }
    }
}

// 月度收支数据类
data class MonthlyData(
    val month: String,
    val expense: Double,
    val income: Double
)

// 年度收支数据类
data class AnnualData(
    val year: Int,
    val monthStatsExpense: Double,  // 月统计支出
    val annualStatsExpense: Double,  // 年统计支出（不在月统计中显示的）
    val income: Double
)

// 月度收支条形图
@Composable
fun MonthlyBarChart(
    monthlyData: List<MonthlyData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "月度收支对比",
                style = MaterialTheme.typography.titleMedium
            )

            if (monthlyData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    factory = { context ->
                        BarChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            description.isEnabled = false
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                            setDrawGridBackground(false)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.setDrawGridLines(false)
                            xAxis.labelRotationAngle = -45f
                            xAxis.textSize = 10f
                            xAxis.granularity = 1f
                            axisLeft.setDrawGridLines(true)
                            axisLeft.axisMinimum = 0f
                            axisRight.isEnabled = false
                            legend.isEnabled = true
                            legend.textSize = 12f
                            setNoDataText("暂无数据")
                            setFitBars(true)
                        }
                    },
                    update = { barChart ->
                        val sortedData = monthlyData.takeLast(6)  // 显示最近6个月
                        val barWidth = 0.35f
                        val groupSpace = 0.2f
                        val barSpace = 0.05f

                        val expenseEntries = sortedData.mapIndexed { index, data ->
                            BarEntry(index.toFloat(), data.expense.toFloat())
                        }

                        val incomeEntries = sortedData.mapIndexed { index, data ->
                            BarEntry(index.toFloat(), data.income.toFloat())
                        }

                        val expenseDataSet = BarDataSet(expenseEntries, "支出").apply {
                            color = Color.parseColor("#FF6B6B")
                            valueTextSize = 9f
                        }

                        val incomeDataSet = BarDataSet(incomeEntries, "收入").apply {
                            color = Color.parseColor("#4ECDC4")
                            valueTextSize = 9f
                        }

                        val barData = BarData(expenseDataSet, incomeDataSet)
                        barData.barWidth = barWidth
                        barChart.data = barData

                        barChart.xAxis.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                return if (index in sortedData.indices) {
                                    sortedData[index].month
                                } else {
                                    ""
                                }
                            }
                        }

                        barChart.xAxis.labelCount = sortedData.size
                        barChart.groupBars(-0.5f, groupSpace, barSpace)
                        barChart.invalidate()
                    }
                )
            }
        }
    }
}

// 年度收支条形图
@Composable
fun AnnualBarChart(
    annualData: List<AnnualData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "年度收支对比",
                style = MaterialTheme.typography.titleMedium
            )

            if (annualData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无数据",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    factory = { context ->
                        BarChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            description.isEnabled = false
                            setTouchEnabled(true)
                            isDragEnabled = true
                            setScaleEnabled(true)
                            setPinchZoom(true)
                            setDrawGridBackground(false)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.setDrawGridLines(false)
                            xAxis.labelRotationAngle = 0f
                            xAxis.textSize = 10f
                            xAxis.granularity = 1f
                            axisLeft.setDrawGridLines(true)
                            axisLeft.axisMinimum = 0f
                            axisRight.isEnabled = false
                            legend.isEnabled = true
                            legend.textSize = 12f
                            setNoDataText("暂无数据")
                            setFitBars(true)
                        }
                    },
                    update = { barChart ->
                        val sortedData = annualData.sortedBy { it.year }
                        val barWidth = 0.25f
                        val groupSpace = 0.15f
                        val barSpace = 0.03f

                        val monthStatsExpenseEntries = sortedData.mapIndexed { index, data ->
                            BarEntry(index.toFloat(), data.monthStatsExpense.toFloat())
                        }

                        val annualStatsExpenseEntries = sortedData.mapIndexed { index, data ->
                            BarEntry(index.toFloat(), data.annualStatsExpense.toFloat())
                        }

                        val incomeEntries = sortedData.mapIndexed { index, data ->
                            BarEntry(index.toFloat(), data.income.toFloat())
                        }

                        val monthStatsExpenseDataSet = BarDataSet(monthStatsExpenseEntries, "月统计支出").apply {
                            color = Color.parseColor("#FF6B6B")
                            valueTextSize = 9f
                        }

                        val annualStatsExpenseDataSet = BarDataSet(annualStatsExpenseEntries, "年统计支出").apply {
                            color = Color.parseColor("#FFA07A")
                            valueTextSize = 9f
                        }

                        val incomeDataSet = BarDataSet(incomeEntries, "收入").apply {
                            color = Color.parseColor("#4ECDC4")
                            valueTextSize = 9f
                        }

                        val barData = BarData(monthStatsExpenseDataSet, annualStatsExpenseDataSet, incomeDataSet)
                        barData.barWidth = barWidth
                        barChart.data = barData

                        barChart.xAxis.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                return if (index in sortedData.indices) {
                                    "${sortedData[index].year}年"
                                } else {
                                    ""
                                }
                            }
                        }

                        barChart.xAxis.labelCount = sortedData.size
                        barChart.groupBars(-0.5f, groupSpace, barSpace)
                        barChart.invalidate()
                    }
                )
            }
        }
    }
}
