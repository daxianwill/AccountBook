package com.sym.accountbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sym.accountbook.ui.screens.AddTransactionScreen
import com.sym.accountbook.ui.screens.BudgetSettingScreen
import com.sym.accountbook.ui.screens.CategoryDetailScreen
import com.sym.accountbook.ui.screens.CategoryManagerScreen
import com.sym.accountbook.ui.screens.HomeScreen
import com.sym.accountbook.ui.screens.SettingsScreen
import com.sym.accountbook.ui.screens.StatisticsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object EditTransaction : Screen("edit_transaction/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_transaction/$transactionId"
    }
    object Statistics : Screen("statistics")
    object CategoryManager : Screen("category_manager")
    object BudgetSetting : Screen("budget_setting")
    object Settings : Screen("settings")
    object CategoryDetail : Screen("categoryDetail/{year}/{month}/{categoryName}") {
        fun createRoute(year: Int, month: Int, categoryName: String) = 
            "categoryDetail/$year/$month/$categoryName"
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(navController = navController)
        }
        composable(Screen.EditTransaction.route) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")?.toLongOrNull()
            AddTransactionScreen(navController = navController, transactionId = transactionId)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
        composable(Screen.CategoryManager.route) {
            CategoryManagerScreen(navController = navController)
        }
        composable(Screen.BudgetSetting.route) {
            BudgetSettingScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.CategoryDetail.route) { backStackEntry ->
            val year = backStackEntry.arguments?.getString("year")?.toIntOrNull() ?: 0
            val month = backStackEntry.arguments?.getString("month")?.toIntOrNull() ?: 0
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryDetailScreen(
                navController = navController,
                year = year,
                month = month,
                categoryName = categoryName
            )
        }
    }
}
