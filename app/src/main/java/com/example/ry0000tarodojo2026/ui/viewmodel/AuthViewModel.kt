package com.example.ry0000tarodojo2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        _authState.value = if (user != null) {
            AuthState.Authenticated(user.uid)
        } else {
            AuthState.Unauthenticated
        }
    }

    init {
        // ログイン状態の監視
        auth.addAuthStateListener(authStateListener)
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }

    fun loginOrRegister(email: String, pass: String, isLogin: Boolean, onResult: (Boolean, String?) -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            onResult(false, "メールアドレスとパスワードを入力してください")
            return
        }
        if (isLogin) {
            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, it.localizedMessage ?: "ログインに失敗しました") }
        } else {
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, it.localizedMessage) }
        }
    }
    
    fun logout() {
        auth.signOut()
    }
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val uid: String) : AuthState()
    object Unauthenticated : AuthState()
}
