package com.evenmoney.repositories.mocks

import com.evenmoney.repositories.interfaces.IAuthRepository
import com.google.firebase.auth.FirebaseUser

class FakeAuthRepository : IAuthRepository {
    override fun register(email: String, password: String, userData: Map<String,String>, onComplete: (Boolean, String?) -> Unit) {
        onComplete(true, null)
    }

    override fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        onComplete(true, null)
    }

    override fun getCurrentUser(): FirebaseUser? {
        return null
    }

    override fun logout() {
    }

    override fun readUserName(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        callback(true,"")
    }
}