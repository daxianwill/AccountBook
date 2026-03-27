package com.sym.accountbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import com.sym.accountbook.data.entity.Budget
import com.sym.accountbook.data.entity.BudgetPeriod
import com.sym.accountbook.ui.viewmodel.BudgetViewModel
import com.sym.accountbook.ui.viewmodel.BudgetViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetSettingScreen(navController: NavController) {
    val context = LocalContext.current
    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(context.applicationContext as android.app.Application)
    )

    val totalBudget by budgetViewModel.totalBudget.observeAsState()

    var budgetAmount by remember { mutableStateOf("") }
    var isBudgetEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(totalBudget) {
        totalBudget?.let { budget ->
            budgetAmount = budget.amount.toString()
            isBudgetEnabled = budget.isEnabled
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("预算设置") },
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
            // 启用预算开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "启用预算提醒",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = isBudgetEnabled,
                    onCheckedChange = { isBudgetEnabled = it }
                )
            }

            // 预算金额输入
            OutlinedTextField(
                value = budgetAmount,
                onValueChange = { budgetAmount = it },
                label = { Text("月度预算金额 (¥)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isBudgetEnabled
            )

            // 提示信息
            if (isBudgetEnabled) {
                Text(
                    text = "设置月度预算后，当支出接近或超过预算时会收到提醒",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 保存按钮
            Button(
                onClick = {
                    val amount = budgetAmount.toDoubleOrNull() ?: 0.0
                    val budget = totalBudget?.copy(
                        amount = amount,
                        isEnabled = isBudgetEnabled
                    ) ?: Budget(
                        amount = amount,
                        period = BudgetPeriod.MONTHLY,
                        isEnabled = isBudgetEnabled
                    )

                    if (totalBudget != null) {
                        budgetViewModel.updateBudget(budget)
                    } else {
                        budgetViewModel.insertBudget(budget)
                    }

                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                enabled = !isBudgetEnabled || budgetAmount.isNotEmpty()
            ) {
                Text("保存设置")
            }
        }
    }
}
