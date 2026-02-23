package ru.akhilko.christian_calendar.core.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun signInAnonymouslyIfNeeded() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            try {
                Log.d("AuthRepository", "No user found, attempting anonymous sign-in...")
                val authResult = auth.signInAnonymously().await()
                Log.d(
                    "AuthRepository",
                    "Anonymous sign-in SUCCESSFUL. User UID: ${authResult.user?.uid}"
                )
            } catch (e: Exception) {
                Log.e("AuthRepository", "Anonymous sign-in FAILED", e)
            }
        } else {
            Log.d("AuthRepository", "User already signed in. UID: ${currentUser.uid}")
        }
    }
}
