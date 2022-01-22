package com.comye1.cheggprep.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.SampleDataSet
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MoreViewModel : ViewModel() {

    // SignIn
    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user

    fun signIn(email: String, displayName: String){
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

    var myDeckList = mutableStateListOf<Deck>()
        private set

    // SearchScreen ////////////////////////
    var searchScreenState = mutableStateOf(SearchState.ButtonScreen)
        private set

    fun toButtonScreen() {
        searchScreenState.value = SearchState.ButtonScreen
    }

    fun toQueryScreen() {
        searchScreenState.value = SearchState.QueryScreen
    }

    fun toResultScreen() {
        searchScreenState.value = SearchState.ResultScreen
    }

    var queryString = mutableStateOf("")
        private set

    fun setQueryString(query: String) {
        queryString.value = query
    }

    // 전체 Deck
    var totalDeckList = mutableStateListOf<Deck>()
        private set

    // Deck 검색 결과 반환
    fun getQueryResult() = totalDeckList.filter { deck ->
        deck.deckTitle.lowercase(Locale.getDefault())
            .contains(queryString.value.lowercase(Locale.getDefault()))
    }.toMutableStateList()
    /////////////////////////////////



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

    init {
        myDeckList = SampleDataSet.myDeckSample.toMutableStateList()
        totalDeckList = SampleDataSet.totalDeckSample.toMutableStateList()
    }
}