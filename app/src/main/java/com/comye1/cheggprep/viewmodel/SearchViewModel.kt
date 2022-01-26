package com.comye1.cheggprep.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
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
    var totalDeckList = mutableStateListOf<Deck>()
        private set

    // Deck 검색 결과 반환
    fun getQueryResult() = totalDeckList.filter { deck ->
        deck.deckTitle.lowercase(Locale.getDefault())
            .contains(queryString.value.lowercase(Locale.getDefault()))
    }.toMutableStateList()
    /////////////////////////////////

    companion object {
        /*
        가져온다..!
         */
        val database = Firebase.database.reference

    }

    init {
        /*
        All안에 있는 Deck들 가져오기... 업뎃은 노
         */
        database.child("all/decks").get().addOnSuccessListener { all ->
            Log.d("search firebase", all.toString())
//            if (all.value != null) {
                val decks = all.value as HashMap<String, *>
                decks.keys.forEach { key ->
                    val deckForAll = decks[key] as HashMap<*, *>
                    // 사용자의 Deck 이거나 (deckType == 0)
                    // 다른 사용자의 Deck 이면서 공유된 것 (shared == true)
                    // TODO 다른 뷰모델에서 가져와야 하는건가 로직짜기 어렵다!!!!!!!!111
                    totalDeckList.add(
                        Deck(
                            deckType = -1,
                            deckTitle = deckForAll["deckTitle"] as String,
                            cardList = deckForAll["cardList"] as List<Card>,
                            bookmarked = false,
                            shared = deckForAll["shared"] as Boolean,
                            key = key
                        )
                    )
//                }
            }

        }


    }
}