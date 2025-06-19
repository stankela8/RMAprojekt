package com.example.snapsell_stankovic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.snapsell_stankovic.ui.theme.SnapSell_StankovicTheme
import com.example.snapsell_stankovic.view.AddScreen
import com.example.snapsell_stankovic.view.BottomNavigationScreen
import com.example.snapsell_stankovic.view.DetailsScreen
import com.example.snapsell_stankovic.view.HomeScreen
import com.example.snapsell_stankovic.view.LoginScreen
import com.example.snapsell_stankovic.view.MyProfileScreen
import com.example.snapsell_stankovic.view.NotificationsScreen
import com.example.snapsell_stankovic.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapSell_StankovicTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn()) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("main") {
            BottomNavigationScreen(navController)
        }
    }
}