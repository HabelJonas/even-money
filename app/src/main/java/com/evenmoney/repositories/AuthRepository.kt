package com.evenmoney.repositories

import com.evenmoney.repositories.interfaces.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository : IAuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun register(
        email: String,
        password: String,
        userData: Map<String, String>,
        onComplete: ((Boolean, String?) -> Unit)
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        db.collection("users").document(it)
                            .set(userData)
                            .addOnSuccessListener { onComplete(true, null) }
                            .addOnFailureListener { e -> onComplete(false, e.message) }
                    } ?: onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    override fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logout() {
        auth.signOut()
    }

    override fun readUserName(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { docSnapshot ->
                if(docSnapshot != null){
                    val userName = docSnapshot.getString("firstname") ?: ""
                    callback(true, userName)
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message.toString())
            }
    }
}