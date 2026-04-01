package com.sym.accountbook.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sym.accountbook.data.entity.Category
import com.sym.accountbook.data.entity.TransactionType
import com.sym.accountbook.ui.viewmodel.CategoryViewModel
import com.sym.accountbook.ui.viewmodel.CategoryViewModelFactory
import androidx.compose.runtime.livedata.observeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerScreen(navController: NavController) {
    val context = LocalContext.current
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(context.applicationContext as android.app.Application)
    )

    val categories by categoryViewModel.allCategories.observeAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("分类管理") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "添加分类")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "暂无分类，点击 + 添加",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                CategoryListContent(
                    categories = categories,
                    onDeleteClick = {
                        categoryToDelete = it
                        showDeleteDialog = true
                    }
                )
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { category ->
                categoryViewModel.insertCategory(category)
                showAddDialog = false
            }
        )
    }

    if (showDeleteDialog && categoryToDelete != null) {
        DeleteCategoryDialog(
            category = categoryToDelete!!,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                categoryViewModel.deleteCategory(categoryToDelete!!)
                showDeleteDialog = false
            }
        )
    }
}

@Composable
fun CategoryListContent(categories: List<Category>, onDeleteClick: (Category) -> Unit) {
    val expenseCategories = categories.filter { it.type == TransactionType.EXPENSE }
    val incomeCategories = categories.filter { it.type == TransactionType.INCOME }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (expenseCategories.isNotEmpty()) {
            item {
                CategorySectionHeader(
                    title = "支出分类",
                    icon = "💸",
                    color = MaterialTheme.colorScheme.primary,
                    count = expenseCategories.size
                )
            }
            items(expenseCategories) { cat ->
                CategoryItem(category = cat, onDeleteClick = onDeleteClick)
            }
        }

        if (incomeCategories.isNotEmpty()) {
            item {
                CategorySectionHeader(
                    title = "收入分类",
                    icon = "💰",
                    color = Color(0xFF4CAF50),
                    count = incomeCategories.size
                )
            }
            items(incomeCategories) { cat ->
                CategoryItem(category = cat, onDeleteClick = onDeleteClick)
            }
        }
    }
}

@Composable
fun CategorySectionHeader(
    title: String,
    icon: String,
    color: Color,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 装饰性左侧条
        Box(
            modifier = Modifier
                .width(4.dp)
                .size(24.dp)
                .background(
                    color = color,
                    shape = MaterialTheme.shapes.small
                )
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = color
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 数量标签
        Box(
            modifier = Modifier
                .background(
                    color = color.copy(alpha = 0.15f),
                    shape = CircleShape
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count 个",
                style = MaterialTheme.typography.bodySmall,
                color = color,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryItem(category: Category, onDeleteClick: (Category) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { },
                onLongClick = { onDeleteClick(category) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 彩色圆形图标
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(category.color),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 分类信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                Text(
                    text = if (category.type == TransactionType.EXPENSE) "支出" else "收入",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 右侧箭头指示器
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "编辑",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onAdd: (Category) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val colors = listOf(
        0xFF2196F3, 0xFF9C27B0, 0xFFFF9800, 0xFF00BCD4,
        0xFFF44336, 0xFF795548, 0xFF4CAF50, 0xFFFFEB3B
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("添加分类") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("分类名称") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedType = TransactionType.EXPENSE
                        }
                    ) {
                        RadioButton(
                            selected = selectedType == TransactionType.EXPENSE,
                            onClick = { selectedType = TransactionType.EXPENSE }
                        )
                        Text("支出")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedType = TransactionType.INCOME
                        }
                    ) {
                        RadioButton(
                            selected = selectedType == TransactionType.INCOME,
                            onClick = { selectedType = TransactionType.INCOME }
                        )
                        Text("收入")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        val randomColor = colors.random()
                        val icons = listOf("📁", "🏷️", "📌", "🎯", "💡", "⭐", "📦", "🎁")
                        onAdd(
                            Category(
                                name = categoryName,
                                icon = icons.random(),
                                type = selectedType,
                                color = randomColor
                            )
                        )
                    }
                }
            ) {
                Text("添加")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteCategoryDialog(
    category: Category,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除分类") },
        text = {
            Text("确定要删除分类 '${category.name}' 吗？\n\n此操作将同时删除该分类下的所有交易记录。")
        },
        confirmButton = {
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("删除")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
