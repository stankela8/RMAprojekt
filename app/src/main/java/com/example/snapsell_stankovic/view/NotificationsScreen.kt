package com.example.snapsell_stankovic.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    var notifications by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .addSnapshotListener { snapshot, _ ->
                    val list = snapshot?.get("notifications") as? List<Map<String, Any>>
                    notifications = list ?: emptyList()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Obavijesti",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        if (notifications.isEmpty()) {
            Text("Nemate novih zahtjeva ili obavijesti.")
        } else {
            notifications.forEach { notif ->
                val rawTime = notif["time"]?.toString()
                val formattedTime = rawTime?.toLongOrNull()?.let { millis ->
                    val date = Date(millis)
                    SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
                } ?: "Nepoznato vrijeme"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = notif["message"]?.toString() ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Vrijeme: $formattedTime",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}