package com.example.snapsell_stankovic.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.snapsell_stankovic.model.Oglas
import com.example.snapsell_stankovic.viewmodel.AuthViewModel
import com.example.snapsell_stankovic.viewmodel.MyProfileViewModel
import androidx.compose.material.icons.filled.ExitToApp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProfileScreen(rootNavController: NavHostController) {
    val context = LocalContext.current
    val myProfileViewModel: MyProfileViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedAd by remember { mutableStateOf<Oglas?>(null) }

    LaunchedEffect(Unit) {
        myProfileViewModel.loadUserData(context)
        myProfileViewModel.loadMyAds(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Moj profil") },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.logout(context)
                            rootNavController.navigate("login") {
                                popUpTo(0)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Odjava"
                        )
                    }
                }
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MyProfileContent(
                myProfileViewModel = myProfileViewModel,
                onDeleteAd = { ad ->
                    selectedAd = ad
                    showDeleteDialog = true
                }
            )
        }
    }

    if (showDeleteDialog && selectedAd != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Potvrdi brisanje") },
            text = { Text("Jeste li sigurni da želite obrisati ovaj oglas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedAd?.let { myProfileViewModel.deleteAd(it, context) }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Obriši")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Odustani")
                }
            }
        )
    }
}

@Composable
private fun MyProfileContent(
    myProfileViewModel: MyProfileViewModel,
    onDeleteAd: (Oglas) -> Unit
) {
    val userData by myProfileViewModel.userData.collectAsState()
    val myAds by myProfileViewModel.myAds.collectAsState()
    val loading by myProfileViewModel.loading.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loading) {
                    CircularProgressIndicator()
                } else {
                    userData?.let { user ->
                        Text(
                            text = user.ime ?: "Nepoznat korisnik",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = user.email ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        items(myAds) { ad ->
            AdItem(ad = ad, onDelete = { onDeleteAd(ad) })
        }
    }
}

@Composable
fun AdItem(ad: Oglas, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ad.imageUrl,
                contentDescription = "Slika artikla",
                modifier = Modifier
                    .size(80.dp)
                    .clip(MaterialTheme.shapes.small)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ad.ime,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${ad.cijena} €",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Obriši oglas"
                )
            }
        }
    }
}
