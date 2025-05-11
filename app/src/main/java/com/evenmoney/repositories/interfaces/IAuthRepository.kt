package com.evenmoney.repositories.interfaces

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    fun register(email: String, password: String, userData: Map<String, String>, onComplete: (Boolean, String?) -> Unit)
    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit)
    fun getCurrentUser(): FirebaseUser?
    fun logout()
    fun readUserName(userId: String, callback: (Boolean, String) -> Unit)
}