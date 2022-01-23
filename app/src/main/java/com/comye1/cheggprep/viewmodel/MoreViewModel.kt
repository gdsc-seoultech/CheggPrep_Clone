package com.comye1.cheggprep.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MoreViewModel : ViewModel() {

    // SignIn
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user

    fun signIn(email: String, displayName: String) {
        _user.value = User(email, displayName)
        //
    }

    // SignOut
    fun signOut() {
        _user.value = null
    }

    // MainActivity에서 firebaseAuth 호출하기
    private val _firebaseAuth = MutableStateFlow(false)
    val firebaseAuth = _firebaseAuth

    private val _token = MutableStateFlow("")
    val token = _token

    fun triggerAuth(idToken: String) { // firebaseAuthWithGoogle을 호출하기 위함
        _token.value = idToken
        _firebaseAuth.value = true
    }

    fun completeAuth() { // firebaseAuthWithGoogle 실행 후
        _firebaseAuth.value = false
    }

    ////////////////////////////////////
    // MoreScreen
    var moreScreenState = mutableStateOf(MoreState.MainScreen)
        private set

    fun toLogInScreen() {
        moreScreenState.value = MoreState.LogInScreen
    }

    fun toMainScreen() {
        moreScreenState.value = MoreState.MainScreen
    }

}