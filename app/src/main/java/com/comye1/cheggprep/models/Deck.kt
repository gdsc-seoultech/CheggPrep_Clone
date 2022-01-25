package com.comye1.cheggprep.models

data class Deck(
    var deckType: Int ,
    var deckTitle: String ,
    var shared: Boolean ,
    var bookmarked: Boolean,
    var cardList: List<Card>,
    var key: String = "key"
)

const val DECK_CREATED = 0
const val DECK_ADDED = 1

data class DeckForAll( // Firebase All에 저장될 클래스
    val deckTitle: String = "no title",
    val shared: Boolean = false,
    val cardList: List<Card> = listOf(),
)

data class DeckForUser( // Firebase User에 저장될 클래스
    val deckType: Int = DECK_CREATED,
    val bookmarked: Boolean = false,
)