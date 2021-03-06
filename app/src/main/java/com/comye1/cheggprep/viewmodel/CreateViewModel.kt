package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.models.DeckForAll
import com.comye1.cheggprep.models.DeckForUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreateViewModel : ViewModel() {

    /*
    RDB에 새로운 Deck 생성 - All 및 User에 저장해야 함.
    */
    fun writeNewDeck(deck: Deck): String {
        val database = Firebase.database.reference
        val user = FirebaseAuth.getInstance().currentUser

        user?.also {
            val key = database.child("all/decks").push().key ?: ""
            val deckForAll =
                DeckForAll(
                    deckTitle = deck.deckTitle,
                    shared = deck.shared,
                    cardList = deck.cardList
                )
            val deckForUser = DeckForUser(
                deckType = deck.deckType,
                bookmarked = deck.bookmarked
            )
            database.child("all/decks")
                .child(key)
                .setValue(deckForAll)
                .addOnCompleteListener {
                    Log.d("firebase", "success")
                }
            database.child("user/${user.uid}/decks")
                .child(key)
                .setValue(deckForUser)
                .addOnCompleteListener {
                    Log.d("firebase", "success")
                }
            // TODO 실패 처리 어떻게 하냐..
            return key
        }
        return "LOGIN_NEEDED"
    }

    // CreateScreen /////////////////
    var createScreenState = mutableStateOf(CreateState.TitleScreen)
        private set

    fun toCardScreen() {
        createScreenState.value = CreateState.CardScreen
    }

    fun toTitleScreen() {
        createScreenState.value = CreateState.TitleScreen
    }

}