package com.comye1.cheggprep.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.comye1.cheggprep.SampleDataSet
import com.comye1.cheggprep.models.Deck
import java.util.*

class CheggViewModel : ViewModel() {

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

    // CreateScreen /////////////////
    var createScreenState = mutableStateOf(CreateState.TitleScreen)
        private set



    fun toCardScreen() {
        createScreenState.value = CreateState.CardScreen
    }
    ////////////////////////////////////
    init {
        myDeckList = SampleDataSet.myDeckSample.toMutableStateList()
        totalDeckList = SampleDataSet.totalDeckSample.toMutableStateList()
    }
}