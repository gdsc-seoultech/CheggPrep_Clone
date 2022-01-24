package com.comye1.cheggprep.models

data class Deck(
    val deckType: Int,
    val deckTitle: String,
    val shared: Boolean,
    val bookmarked: Boolean,
    val cardList: List<Card>,
    val key: String = ""
)

const val DECK_CREATED = 0
const val DECK_ADDED = 1

data class DeckForAll( // Firebase All에 저장될 클래스
    val deckTitle: String,
    val shared: Boolean,
    val cardList: List<Card>,
)

data class DeckForUser( // Firebase User에 저장될 클래스
    val deckType: Int,
    val bookmarked: Boolean,
)