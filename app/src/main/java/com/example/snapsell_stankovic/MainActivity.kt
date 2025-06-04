package com.example.snapsell_stankovic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.snapsell_stankovic.ui.theme.SnapSell_StankovicTheme
import com.example.snapsell_stankovic.view.AddScreen
import com.example.snapsell_stankovic.view.BottomNavigationScreen
import com.example.snapsell_stankovic.view.DetailsScreen
import com.example.snapsell_stankovic.view.HomeScreen
import com.example.snapsell_stankovic.view.LoginScreen
import com.example.snapsell_stankovic.view.MyProfileScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnapSell_StankovicTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("home") {
            BottomNavigationScreen(navController) // ✅ prikazuje Home, Add, Profile
        }
        composable("details") {
            DetailsScreen(navController) // ✅ prikaz pojedinačnog oglasa
        }
    }
}
