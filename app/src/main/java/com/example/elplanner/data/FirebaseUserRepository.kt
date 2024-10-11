package com.example.elplanner.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class FirebaseUserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = Firebase.database.reference

    fun createUser(username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            database.child("users").child(currentUser.uid)
                .setValue(username)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }
        }
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}