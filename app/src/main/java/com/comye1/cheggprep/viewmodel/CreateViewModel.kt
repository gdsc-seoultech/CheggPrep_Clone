package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.Deck
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CreateViewModel: ViewModel() {

    private val database = Firebase.database.reference

    /*
    RDB에 새로운 Deck 생성 - All 및 User에 저장해야 함.
     */
    fun writeNewDeck(cardList: List<Card>) {
        val key = database.child("all").push().key
        database.child("all").child(key.toString()).setValue(cardList).addOnCompleteListener {
            Log.d("firebase", "success")
        }

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