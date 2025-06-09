package com.example.snapsell_stankovic.viewmodel

import androidx.lifecycle.ViewModel
import com.example.snapsell_stankovic.model.Oglas
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _oglasi = MutableStateFlow<List<Oglas>>(emptyList())
    val oglasi: StateFlow<List<Oglas>> = _oglasi

    val kategorije = listOf("Sve", "Elektronika", "Odjeća", "Namještaj", "Ostalo")

    private var trenutnaKategorija: String = "Sve"

    init {
        listenToOglasi("Sve")
    }

    fun listenToOglasi(kategorija: String) {
        trenutnaKategorija = kategorija
        val query = if (kategorija == "Sve") {
            db.collection("oglasi")
        } else {
            db.collection("oglasi").whereEqualTo("kategorija", kategorija)
        }
        query.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && !snapshot.isEmpty) {
                val lista = snapshot.documents.mapNotNull { it.toObject(Oglas::class.java) }
                _oglasi.value = lista
            } else {
                _oglasi.value = emptyList()
            }
        }
    }
}
