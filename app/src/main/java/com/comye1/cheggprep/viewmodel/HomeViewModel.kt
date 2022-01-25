package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.models.DeckForAll
import com.comye1.cheggprep.models.DeckForUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    var myDeckList = mutableStateListOf<Deck>()
        private set

    /*
    가져온다..!
     */
    val database = Firebase.database.reference
    val user = FirebaseAuth.getInstance().currentUser!!

    init {
        /*
        User안에 있는 Deck들 가져오기... 이제 업뎃도 해야 한다
         */
        database.child("user/${user.uid}/decks").get().addOnSuccessListener { user ->
//            Log.d("home firebase user", user.toString())
            val deckForUserList = user.value as HashMap<String, DeckForUser>
            Log.d("firebase user list", deckForUserList.toString())

            deckForUserList.keys.sorted().forEach { key ->
//                Log.d("firebase user iteration", "##")
                // it.value는 DeckForAll
                val deckForUser = deckForUserList[key] as HashMap<*, *>
//                Log.d("deckForUser", "$key , ${deckForUser["deckType"]}")

                database.child("all/decks/$key").get().addOnSuccessListener { all ->
                    // all.value는 DeckForUser
                    val deckForAll = all.value as HashMap<*, *>

                    myDeckList.add(
                        Deck(
                            deckType = (deckForUser["deckType"] as Long).toInt(),
                            deckTitle = deckForAll["deckTitle"] as String,
                            cardList = deckForAll["cardList"] as List<Card>,
                            bookmarked = deckForUser["bookmarked"] as Boolean,
                            shared = deckForAll["shared"] as Boolean,
                            key = key
                        )
                    )
                }

            }
//
        }

    }


}