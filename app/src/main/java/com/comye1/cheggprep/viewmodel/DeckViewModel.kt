package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DeckViewModel : ViewModel() {

    var deck = mutableStateOf<Deck?>(null)

    private val _deckState = MutableStateFlow<DeckState>(DeckState.Loading)
    val deckState: StateFlow<DeckState> = _deckState

    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null

    sealed class DeckState {

        // 로딩중 - 초기 상태
         object Loading: DeckState()

        // 삭제됨 - 북마크 한 Deck가 Owner에 의해 삭제됨
        object Deleted : DeckState()

        // 보이지 않음 - 북마크 하거나 Add 된 Deck가 Invisible 상태임 ==> 해당 없음
        // object Invisible : DeckState()

        // 유효함 - 자신의 Deck 또는 북마크/Add된 Deck가 Visible 상태임임
        class Valid(val deck: Deck) : DeckState()
    }

    /*
    전달받은 key로 deck 가져오기
     */
    fun getDeckByKey(key: String) {

        database = Firebase.database.reference
        user = FirebaseAuth.getInstance().currentUser

        var deckForAll: DeckForAll? = null
        var deckForUser: DeckForUser? = null

        database.child("all/decks/$key").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                deckForAll = snapshot.getValue(DeckForAll::class.java)

                /*
                all에 존재하지 않음 (북마크 해놓은 Deck가 삭제된 경우)
                 */
                if (deckForAll == null){
                    _deckState.value = DeckState.Deleted
                    Log.d("firebase deleted deck", "ok")
                }else {
                    if (user != null) {
                        database.child("user/${user!!.uid}/decks/$key")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    deckForUser = snapshot.getValue(DeckForUser::class.java)
                                    Log.d("firebase user", deckForUser.toString())

                                    if (deckForUser != null) {
                                        deck.value = Deck(
                                            deckTitle = deckForAll!!.deckTitle,
                                            deckType = deckForUser!!.deckType,
                                            cardList = deckForAll!!.cardList,
                                            bookmarked = deckForUser!!.bookmarked,
                                            shared = deckForAll!!.shared,
                                            key = key
                                        )
                                        _deckState.value = DeckState.Valid(
                                            deck.value!!
                                        )
                                    }
                                    else { // user에 존재하지 않음
                                        if (deckForAll == null) {
                                            _deckState.value = DeckState.Deleted
                                            Log.d("firebase deleted deck", "ok")
                                        } else {
                                            deck.value = Deck(
                                                deckTitle = deckForAll!!.deckTitle,
                                                deckType = -1, // 사용자의 Deck도 아니고 추가된 Deck도 아님
                                                cardList = deckForAll!!.cardList,
                                                bookmarked = false,
                                                shared = deckForAll!!.shared,
                                                key = key
                                            )
                                            _deckState.value = DeckState.Valid(
                                                deck.value!!
                                            )
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })

                    }else { // user == null
                        deck.value = Deck(
                            deckTitle = deckForAll!!.deckTitle,
                            deckType = -1, // 사용자의 Deck도 아니고 추가된 Deck도 아님
                            cardList = deckForAll!!.cardList,
                            bookmarked = false,
                            shared = deckForAll!!.shared,
                            key = key
                        )
                        _deckState.value = DeckState.Valid(
                            deck.value!!
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                deck.value = null
            }
        })
    }

    fun deleteDeck(key: String) {
        val database = Firebase.database.reference
        val user = FirebaseAuth.getInstance().currentUser!!

        deck.value?.let {
            if (it.deckType == DECK_CREATED) {
                database.child("all/decks/$key").removeValue().addOnSuccessListener {
                    Log.d("deck remove", "success")
                }
                database.child("user/${user.uid}/decks/$key").removeValue().addOnSuccessListener {
                    Log.d("deck remove", "success")
                }
            }
        }
    }

    fun addBookmark(key: String) {

        val database = Firebase.database.reference
        val user = FirebaseAuth.getInstance().currentUser!!

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

    fun deleteBookmark(key: String) {
        val database = Firebase.database.reference
        val user = FirebaseAuth.getInstance().currentUser!!

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
