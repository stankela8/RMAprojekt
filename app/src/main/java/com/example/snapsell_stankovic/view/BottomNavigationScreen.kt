package com.example.snapsell_stankovic.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Add : BottomNavItem("Add", Icons.Default.Add)
    object Profile : BottomNavItem("MyProfile", Icons.Default.Person)
}

@Composable
fun BottomNavigationScreen(navController: NavHostController) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(BottomNavItem.Home, BottomNavItem.Add, BottomNavItem.Profile).forEach { item ->
                    NavigationBarItem(
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                is BottomNavItem.Home -> HomeScreen(navController)         // ✅ navController proslijeđen
                is BottomNavItem.Add -> AddScreen()
                is BottomNavItem.Profile -> MyProfileScreen()
            }
        }
    }
}
