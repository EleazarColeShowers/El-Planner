package com.example.elplanner.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class FirebaseUserRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = Firebase.database.reference

    fun syncTaskToFirebase(taskItem: TaskItem, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val taskId = taskItem.id.toString()  // Use the task ID as the Firebase node key
//TODO: RE-STRUCTURE REALTIME DATABASE
        database.child("users").child(userId).child("tasks").child(taskId)
            .setValue(taskItem)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
}