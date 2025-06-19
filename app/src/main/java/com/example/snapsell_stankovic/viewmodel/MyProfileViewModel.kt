package com.example.snapsell_stankovic.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.snapsell_stankovic.model.Oglas
import com.example.snapsell_stankovic.model.User

class MyProfileViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _myAds = MutableStateFlow<List<Oglas>>(emptyList())
    val myAds: StateFlow<List<Oglas>> = _myAds

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadUserData(context: Context) {
        _loading.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    _userData.value = document.toObject<User>()
                    _loading.value = false
                }
                .addOnFailureListener {
                    _loading.value = false
                    Toast.makeText(context, "Greška pri učitavanju profila", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun loadMyAds(context: Context) {
        _loading.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("oglasi")
                .whereEqualTo("vlasnik", currentUser.uid)
                .addSnapshotListener { value, error ->
                    _loading.value = false
                    if (error != null) {
                        Toast.makeText(context, "Greška pri učitavanju oglasa", Toast.LENGTH_SHORT).show()
                        return@addSnapshotListener
                    }

                    _myAds.value = value?.documents?.mapNotNull { doc ->
                        doc.toObject(Oglas::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                }
        }
    }


    fun deleteAd(ad: Oglas, context: Context) {
        viewModelScope.launch {
            try {
                // Brisanje iz baze
                db.collection("oglasi").document(ad.id).delete()

                // Brisanje slike iz Storagea
                storage.getReferenceFromUrl(ad.imageUrl).delete()

                // Ažurirajnje liste
                _myAds.value = _myAds.value.filter { it.id != ad.id }
            } catch (e: Exception) {
                Toast.makeText(context, "Greška pri brisanju: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
