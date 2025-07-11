package com.example.snapsell_stankovic.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.snapsell_stankovic.model.Oglas

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomNavItem("Početna", Icons.Default.Home, "home")
    object Add : BottomNavItem("Dodaj", Icons.Default.Add, "add")
    object Profile : BottomNavItem("Moj Profil", Icons.Default.Person, "myprofile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun BottomNavigationScreen(rootNavController: NavHostController) {
        val tabNavController = rememberNavController()
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Add,
            BottomNavItem.Profile
        )
        val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                tabNavController.navigate(item.route) {
                                    popUpTo(tabNavController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = tabNavController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        navController = tabNavController,
                        onNavigateToDetails = { oglas ->
                            tabNavController.currentBackStackEntry?.savedStateHandle?.set(
                                "oglas",
                                oglas
                            )
                            tabNavController.navigate("details")
                        }
                    )
                }
                composable(BottomNavItem.Add.route) { AddScreen() }
                composable("details") {
                    DetailsScreen(
                        navController = tabNavController,
                        onBack = { tabNavController.popBackStack() }
                    )
                }
                composable("notifications") {
                    NotificationsScreen(tabNavController)
                }
                composable(BottomNavItem.Profile.route) {
                    MyProfileScreen(
                        rootNavController = rootNavController, // za logout
                        tabNavController = tabNavController    // za navigaciju unutar tabova
                    )
                }
            }
        }
    }
