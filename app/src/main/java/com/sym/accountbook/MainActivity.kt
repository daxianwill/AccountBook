package com.sym.accountbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.InsertChart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sym.accountbook.data.DatabaseInitializer
import com.sym.accountbook.ui.navigation.AppNavHost
import com.sym.accountbook.ui.navigation.Screen
import com.sym.accountbook.ui.theme.AppTheme
import com.sym.accountbook.utils.ThemeManager

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        title = "首页",
        icon = Icons.Default.Home
    )
    object Statistics : BottomNavItem(
        route = Screen.Statistics.route,
        title = "统计",
        icon = Icons.Default.InsertChart
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseInitializer.initialize(this)
        ThemeManager.initialize(this)
        setContent {
            AppTheme(
                darkTheme = ThemeManager.isDarkThemeEnabled,
                dynamicColor = ThemeManager.isDynamicColorEnabled
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Statistics
                    )

                    @OptIn(ExperimentalMaterial3Api::class)
                    Scaffold(
                        bottomBar = {
                            BottomAppBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                items.forEach { item ->
                                    NavigationBarItem(
                                        icon = { Icon(item.icon, contentDescription = item.title) },
                                        label = { Text(item.title) },
                                        selected = currentRoute == item.route,
                                        onClick = {
                                            if (currentRoute != item.route) {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.startDestinationId) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        AppNavHost(
                            navController = navController,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
