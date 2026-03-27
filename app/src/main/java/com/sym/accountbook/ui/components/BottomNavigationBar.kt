package com.sym.accountbook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.sym.accountbook.ui.navigation.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomAppBar(
        actions = {
            IconButton(onClick = { navController.navigate(Screen.Home.route) }) {
                Icon(Icons.Filled.Home, contentDescription = "首页")
            }
            IconButton(onClick = { navController.navigate(Screen.Statistics.route) }) {
                Icon(Icons.Filled.Star, contentDescription = "统计")
            }
        }
    )
}
