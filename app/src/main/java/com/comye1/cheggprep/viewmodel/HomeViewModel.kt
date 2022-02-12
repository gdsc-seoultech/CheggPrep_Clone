package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.Deck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
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
        if (user != null) {
            database.child("user/${user!!.uid}/decks").get().addOnSuccessListener { snapshot ->
                // myDeckList를 비우고 데이터를 새로 받아옴
                myDeckList.clear()
                if (snapshot.value != null) {
                    for (deckForUser in snapshot.children) { // 로직 변경

                        val key = deckForUser.key as String

                        database.child("all/decks/$key").get().addOnSuccessListener { all ->
                            if (all.value != null) {
                                // all.value는 DeckForUser
                                val deckTitle = all.child("deckTitle").value as String
                                val shared = all.child("shared").value as Boolean
                                val cardList = all.child("cardList").value as List<Card>
                                val bookmarked = deckForUser.child("bookmarked").value as Boolean
                                val deckType = (deckForUser.child("deckType").value as Long).toInt()

                                // 사용자의 Deck 이거나 (deckType == 0)
                                // 다른 사용자의 Deck 이면서 공유된 것 (shared == true)

                                if (deckType == DECK_CREATED || shared) {
                                    myDeckList.add(
                                        Deck(
                                            deckType = deckType,
                                            deckTitle = deckTitle,
                                            cardList = cardList,
                                            bookmarked = bookmarked,
                                            shared = shared,
                                            key = key
                                        )
                                    )
                                    Log.d("myDeckList", myDeckList.size.toString())
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    fun clearDecks() {
        myDeckList.clear()
    }

    fun refresh() {
        getUserDecks()
    }
}