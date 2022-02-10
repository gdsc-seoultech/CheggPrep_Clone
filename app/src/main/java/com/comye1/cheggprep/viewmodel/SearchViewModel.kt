package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.Deck
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class SearchViewModel : ViewModel() {
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
    private var totalDeckList = mutableStateListOf<Deck>()

    // Deck 검색 결과 반환
    val queryResult: SnapshotStateList<Deck>
        get() = totalDeckList.filter { deck ->
            deck.deckTitle.lowercase(Locale.getDefault())
                .contains(queryString.value.lowercase(Locale.getDefault()))
        }.toMutableStateList()

    companion object {
        val database = Firebase.database.reference
    }

    fun getAllDecks() {
        /*
        All안에 있는 Deck들 가져오기... 업뎃은 노
        valueEventListener로 바꾼다 or 검색 요청마다 불러오기
        => 검색 요청마다 불러오기
         */
        database.child("all/decks").get().addOnSuccessListener { all ->
            Log.d("searchscreen", "called")
            /*
            아무것도 없을 때..아무것도 안 보여줌..
             */
            totalDeckList.clear()
            if (all.value != null) {
                val decks = all.value as HashMap<String, *>
                decks.keys.forEach { key ->
                    val deckForAll = decks[key] as HashMap<*, *>
                    // (shared == true)
                    val shared = deckForAll["shared"] as Boolean
                    if (shared) {
                        totalDeckList.add(
                            Deck(
                                deckType = -1,
                                deckTitle = deckForAll["deckTitle"] as String,
                                cardList = deckForAll["cardList"] as List<Card>,
                                bookmarked = false,
                                shared = shared,
                                key = key
                            )
                        )
                    }
                }
            }
        }
    }
}