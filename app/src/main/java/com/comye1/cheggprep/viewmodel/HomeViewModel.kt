package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.models.DeckForUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    var myDeckList = mutableStateListOf<Deck>()
        private set

    /*
    가져온다..!
     */
    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null

    private fun getUserDecks() {

        database = Firebase.database.reference
        user = FirebaseAuth.getInstance().currentUser
        /*
        User안에 있는 Deck들 가져오기... 이제 업뎃도 해야 한다
         */
        user?.let {
            database.child("user/${user!!.uid}/decks").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            val deckForUserList = snapshot.value as HashMap<String, DeckForUser>

                            deckForUserList.keys.sorted().forEach { key ->
                                // it.value는 DeckForAll
                                val deckForUser = deckForUserList[key] as HashMap<*, *>

                                myDeckList.clear() // myDeckList를 비우고 데이터를 새로 받아옴

                                database.child("all/decks/$key").get().addOnSuccessListener { all ->
                                    // all.value는 DeckForUser
                                    val deckForAll = all.value as HashMap<*, *>

                                    // 사용자의 Deck 이거나 (deckType == 0)
                                    // 다른 사용자의 Deck 이면서 공유된 것 (shared == true)
                                    val deckType = (deckForUser["deckType"] as Long).toInt()
                                    val shared = deckForAll["shared"] as Boolean

                                    if (deckType == DECK_CREATED || shared) {
                                        myDeckList.add(
                                            Deck(
                                                deckType = deckType,
                                                deckTitle = deckForAll["deckTitle"] as String,
                                                cardList = deckForAll["cardList"] as List<Card>,
                                                bookmarked = deckForUser["bookmarked"] as Boolean,
                                                shared = shared,
                                                key = key
                                            )
                                        )
                                        Log.d("myDeckList", myDeckList.last().toString())
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.d("homescreen", "canceled")
                        myDeckList.clear()
                    }
                }
            )
        }
    }

    fun refresh() {
        getUserDecks()
    }
}