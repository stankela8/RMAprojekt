package com.example.snapsell_stankovic.view

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.snapsell_stankovic.model.Oglas
import com.example.snapsell_stankovic.viewmodel.DetailsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


@Composable
fun DetailsScreen(navController: NavHostController, onBack: () -> Boolean) {
    val oglas = navController.previousBackStackEntry
        ?.savedStateHandle?.get<Oglas>("oglas")
    val viewModel: DetailsViewModel = viewModel()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }

    if (oglas != null) {
        LaunchedEffect(oglas.vlasnik) {
            viewModel.fetchUserData(oglas.vlasnik)
        }

        val vlasnikIme by viewModel.vlasnikIme.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(oglas.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Text(text = oglas.ime, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Cijena: ${oglas.cijena} €", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Kategorija: ${oglas.kategorija}")
            Text(text = "Opis: ${oglas.opis}")
            Text(text = "Vlasnik: $vlasnikIme", fontStyle = FontStyle.Italic)

            // Prikaži gumb samo ako korisnik NIJE vlasnik oglasa
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId != oglas.vlasnik) {
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Kontaktiraj oglašivača")
                }
            }
        }

        if (showDialog) {
            SendInquiryDialog(
                oglas = oglas,
                onDismiss = { showDialog = false },
                onSend = { lokacija, mobitel, email ->
                    sendInquiryToSeller(
                        oglas = oglas,
                        lokacija = lokacija,
                        mobitel = mobitel,
                        email = email,
                        context = context
                    )
                    showDialog = false
                }
            )
        }
    } else {
        Text("Greška: Oglas nije pronađen", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun SendInquiryDialog(
    oglas: Oglas,
    onDismiss: () -> Unit,
    onSend: (lokacija: String, mobitel: String, email: String) -> Unit
) {
    var lokacija by remember { mutableStateOf("") }
    var mobitel by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pošalji upit oglašivaču") },
        text = {
            Column {
                OutlinedTextField(
                    value = lokacija,
                    onValueChange = { lokacija = it },
                    label = { Text("Lokacija") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = mobitel,
                    onValueChange = { mobitel = it },
                    label = { Text("Broj mobitela") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (lokacija.isNotBlank() && mobitel.isNotBlank() && email.isNotBlank()) {
                        onSend(lokacija, mobitel, email)
                    }
                }
            ) {
                Text("Pošalji")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Odustani") }
        }
    )
}


fun sendInquiryToSeller(
    oglas: Oglas,
    lokacija: String,
    mobitel: String,
    email: String,
    context: Context
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val notification = mapOf(
        "message" to "Novi upit za oglas '${oglas.ime}':\nLokacija: $lokacija\nMobitel: $mobitel\nEmail: $email",
        "senderID" to (currentUser?.uid ?: ""),
        "time" to System.currentTimeMillis().toString(),
        "request" to true
    )
    FirebaseFirestore.getInstance().collection("users").document(oglas.vlasnik)
        .update("notifications", FieldValue.arrayUnion(notification))
        .addOnSuccessListener {
            Toast.makeText(context, "Upit je poslan oglašivaču!", Toast.LENGTH_SHORT).show()
            showLocalNotification(
                context,
                title = "Upit poslan!",
                message = "Vaš upit je uspješno poslan oglašivaču."
            )
        }
        .addOnFailureListener {
            Toast.makeText(context, "Greška pri slanju upita.", Toast.LENGTH_SHORT).show()
        }
}


fun showLocalNotification(context: Context, title: String, message: String) {
    val channelId = "oglasi_channel"
    val notificationId = 1001

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = android.app.NotificationChannel(
            channelId,
            "Obavijesti o oglasima",
            android.app.NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    ) {
        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
    } else {
        // doraditi
        //
    }
}
