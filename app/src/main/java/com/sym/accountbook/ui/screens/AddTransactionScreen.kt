package com.sym.accountbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.sym.accountbook.ui.theme.SoftGreen
import com.sym.accountbook.ui.theme.SoftRed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.Transaction
import com.sym.accountbook.data.entity.TransactionType
import com.sym.accountbook.ui.components.DatePickerComponent
import com.sym.accountbook.ui.viewmodel.CategoryViewModel
import com.sym.accountbook.ui.viewmodel.CategoryViewModelFactory
import com.sym.accountbook.ui.viewmodel.TransactionViewModel
import com.sym.accountbook.ui.viewmodel.TransactionViewModelFactory
import androidx.compose.runtime.livedata.observeAsState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionId: Long? = null
) {
    val context = LocalContext.current
    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(context.applicationContext as android.app.Application)
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(context.applicationContext as android.app.Application)
    )

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var selectedDate by remember { mutableStateOf(Date()) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var originalTransaction by remember { mutableStateOf<Transaction?>(null) }

    val dateFormat = remember { SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA) }

    val categories by categoryViewModel.getCategoriesByType(selectedType)
        .observeAsState(initial = emptyList())

    // 如果是编辑模式，加载原有数据
    LaunchedEffect(transactionId) {
        if (transactionId != null && transactionId > 0) {
            transactionViewModel.getTransactionById(transactionId)?.let { transaction ->
                originalTransaction = transaction
                amount = transaction.amount.toString()
                note = transaction.note
                selectedType = transaction.type
                selectedCategoryId = transaction.categoryId
                selectedDate = transaction.date
            }
        }
    }

    // Auto-select first category when type changes
    if (categories.isNotEmpty() && selectedCategoryId == null) {
        selectedCategoryId = categories.first().id
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId != null && transactionId > 0) "编辑记录" else "添加记录", fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (transactionId != null && transactionId > 0) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "删除")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 类型选择器 - 优化为切换按钮样式
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 支出按钮
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedType = TransactionType.EXPENSE
                                selectedCategoryId = null
                            }
                            .background(
                                if (selectedType == TransactionType.EXPENSE)
                                    SoftRed.copy(alpha = 0.15f)
                                else
                                    Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                        text = "💸 支出",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedType == TransactionType.EXPENSE)
                            SoftRed else MaterialTheme.colorScheme.onSurface
                    )
                    }
                    
                    // 收入按钮
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedType = TransactionType.INCOME
                                selectedCategoryId = null
                            }
                            .background(
                                if (selectedType == TransactionType.INCOME)
                                    SoftGreen.copy(alpha = 0.15f)
                                else
                                    Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                        text = "💰 收入",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedType == TransactionType.INCOME)
                            SoftGreen else MaterialTheme.colorScheme.onSurface
                    )
                    }
                }
            }

            // 金额输入卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "金额",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¥",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.EXPENSE) SoftRed else SoftGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            placeholder = { Text("0.00", style = MaterialTheme.typography.headlineSmall) },
                            textStyle = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (selectedType == TransactionType.EXPENSE) SoftRed else SoftGreen
                            )
                        )
                    }
                }
            }

            // 日期时间选择卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "日期时间",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    DatePickerComponent.showDatePicker(context, selectedDate) { newDate ->
                                        selectedDate = newDate
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = "日期",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dateFormat.format(selectedDate),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Button(
                            onClick = {
                                DatePickerComponent.showTimePicker(context, selectedDate) { newDate ->
                                    selectedDate = newDate
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text("调整时间")
                        }
                    }
                }
            }

            // 分类选择卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "选择分类",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (categories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "暂无分类，请先在分类管理中添加",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(categories) { cat ->
                                OptimizedCategoryGridItem(
                                    category = cat,
                                    isSelected = selectedCategoryId == cat.id,
                                    onClick = { selectedCategoryId = cat.id }
                                )
                            }
                        }
                    }
                }
            }

            // 备注输入卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "备注（可选）",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("添加备注...") },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // 保存按钮
            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (amountDouble != null && amountDouble > 0 && selectedCategoryId != null) {
                        if (transactionId != null && transactionId > 0 && originalTransaction != null) {
                            // 编辑模式：更新
                            val updatedTransaction = originalTransaction!!.copy(
                                type = selectedType,
                                categoryId = selectedCategoryId!!,
                                amount = amountDouble,
                                date = selectedDate,
                                note = note
                            )
                            transactionViewModel.updateTransaction(updatedTransaction)
                        } else {
                            // 添加模式：插入
                            val transaction = Transaction(
                                type = selectedType,
                                categoryId = selectedCategoryId!!,
                                amount = amountDouble,
                                date = selectedDate,
                                note = note
                            )
                            transactionViewModel.insertTransaction(transaction)
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    "保存",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条记录吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        originalTransaction?.let {
                            transactionViewModel.deleteTransaction(it)
                        }
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("删除", color = SoftRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun CategoryGridItem(
    category: com.sym.accountbook.data.entity.Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(bottom = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

// 简洁的分类项组件
@Composable
fun OptimizedCategoryGridItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val categoryColor = Color(category.color)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 简洁显示图标
        Text(
            text = category.icon,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) categoryColor else Color.Unspecified
        )
        Spacer(modifier = Modifier.height(4.dp))
        // 简洁显示文字
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            color = if (isSelected)
                categoryColor
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
        // 选中时下划线指示
        if (isSelected) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
        }
    }
}
