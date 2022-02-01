package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DeckViewModel : ViewModel() {

    var deck = mutableStateOf<Deck?>(null)

    /*
    전달받은 key로 deck 가져오기
     */
    val database = Firebase.database.reference
    val user = FirebaseAuth.getInstance().currentUser!!

    fun getDeckByKey(key: String) {
        database.child("all/decks/$key").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val deckForAll = snapshot.getValue(DeckForAll::class.java)
                var deckForUser: DeckForUser?
                Log.d("firebase all", deckForAll.toString())
                database.child("user/${user.uid}/decks/$key")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            deckForUser = snapshot.getValue(DeckForUser::class.java)
                            Log.d("firebase user", deckForUser.toString())
                            if (deckForAll != null && deckForUser != null) {
                                deck.value = Deck(
                                    deckTitle = deckForAll.deckTitle,
                                    deckType = deckForUser!!.deckType,
                                    cardList = deckForAll.cardList,
                                    bookmarked = deckForUser!!.bookmarked,
                                    shared = deckForAll.shared,
                                    key = key
                                )
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun addBookmark(key: String): Unit {
        if (!user.isAnonymous) {
            deck.value?.apply {
                if (deckType != DECK_ADDED) // 학습 내역이 없음
                    deckType = DECK_ONLY_BOOKMARKED // only 북마크만!
            }
            // deck 속성을 변경하지 말고 새로운 deck를 인가해줘야 한다!
            deck.value = deck.value?.copy(bookmarked = true)
            deck.value?.also {
                val deckForUser = DeckForUser(deckType = it.deckType, bookmarked = true)
                database.child("user/${user.uid}/decks/$key")
                    .setValue(deckForUser)
                    .addOnSuccessListener {
                        Log.d("firebase bookmarked", deck.toString())
                    }

            }
        }
    }

    fun deleteBookmark(key: String): Unit {
        if (!user.isAnonymous) {
            when (deck.value?.deckType) {
                DECK_ADDED -> { // 북마크만 false로 변경
                    deck.value = deck.value?.copy(bookmarked = false)
                    // deck 속성을 변경하지 말고 새로운 deck를 인가해줘야 한다!
                    database.child("user/${user.uid}/decks/$key")
                        .setValue(
                            deck
                        ).addOnSuccessListener {
                            Log.d("firebase bookmarked", deck.toString())
                        }
                }
                DECK_ONLY_BOOKMARKED -> { // 사용자 decks에서 제거
                    database.child("user/${user.uid}/decks/$key")
                        .removeValue()
                        .addOnSuccessListener {
                            Log.d("firebase unbookmark", deck.toString())
                        }
                }
            }
        }
    }
}


//class DeckViewModelFactory(
//    private val key: String
//) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return DeckViewModel(key) as T
//    }
//}