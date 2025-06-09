package com.example.snapsell_stankovic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _vlasnikIme = MutableStateFlow("")
    val vlasnikIme: StateFlow<String> = _vlasnikIme

    fun fetchUserData(uid: String) {
        viewModelScope.launch {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    val ime = document.getString("ime") ?: ""
                    val prezime = document.getString("prezime") ?: ""
                    _vlasnikIme.value = "$ime $prezime".trim()
                }
                .addOnFailureListener {
                    _vlasnikIme.value = "Nepoznat korisnik"
                }
        }
    }
}
