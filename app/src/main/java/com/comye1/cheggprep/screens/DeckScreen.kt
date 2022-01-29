package com.comye1.cheggprep.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.comye1.cheggprep.models.*
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun DeckScreen(navController: NavController, key: String) {

    var deck by remember {
        mutableStateOf<Deck?>(null)
    }

    /*
    전달받은 key로 deck 가져오기
     */
    val database = Firebase.database.reference
    val user = FirebaseAuth.getInstance().currentUser!!

    val addBookmark: () -> Unit = {
        if (!user.isAnonymous) {
            deck?.apply {
                if (deckType != DECK_ADDED) // 학습 내역이 없음
                    deckType = DECK_ONLY_BOOKMARKED // only 북마크만!
            }
            // deck 속성을 변경하지 말고 새로운 deck를 인가해줘야 한다!
            deck = deck?.copy(bookmarked = true)
            deck?.also {
                val deckForUser = DeckForUser(deckType = it.deckType, bookmarked = true)
                database.child("user/${user.uid}/decks/$key")
                    .setValue(deckForUser)
                    .addOnSuccessListener {
                        Log.d("firebase bookmarked", deck.toString())
                    }

            }
        }
    }

    val deleteBookmark: () -> Unit = {
        if (!user.isAnonymous) {
            when (deck?.deckType) {
                DECK_ADDED -> { // 북마크만 false로 변경
                    deck = deck?.copy(bookmarked = false)
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

    database.child("all/decks/$key").get().addOnSuccessListener { all ->

        val deckForAll = all.getValue(DeckForAll::class.java)
        var deckForUser: DeckForUser? = null

        database.child("user/${user.uid}/decks/$key").get().addOnSuccessListener { user ->
            Log.d("firebase all", all.toString())
            Log.d("firebase user", user.toString())

            deckForUser = user.getValue(DeckForUser::class.java)

            if (deckForAll != null && deckForUser != null) {
                deck = Deck(
                    deckTitle = deckForAll.deckTitle,
                    deckType = deckForUser!!.deckType,
                    cardList = deckForAll.cardList,
                    bookmarked = deckForUser!!.bookmarked,
                    shared = deckForAll.shared,
                    key = key
                )
            }
        }

        if (deckForAll != null && deckForUser == null) {
            deck = Deck(
                deckTitle = deckForAll.deckTitle,
                deckType = -1, // 사용자의 Deck도 아니고 추가된 Deck도 아님
                cardList = deckForAll.cardList,
                bookmarked = false,
                shared = deckForAll.shared,
                key = key
            )
        }
    }

    deck?.let { deck ->
        Scaffold(topBar = {
            TopAppBar(
                elevation = 0.dp,
                backgroundColor = Color.White,
                title = { Text(text = deck.deckTitle) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "navigate back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                    }
                    if (deck.deckType == DECK_CREATED) {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "more"
                            )
                        }
                    } else {
                        if (deck.bookmarked) {
                            IconButton(onClick = {
                                deleteBookmark()
                                Log.d("log deck", deck.deckType.toString())
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "add bookmark"
                                )
                            }
                        } else {
                            IconButton(onClick = addBookmark) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "add bookmark"
                                )
                            }
                        }
                    }
                }
            )
        },
            bottomBar = {
                Column(modifier = Modifier.background(color = Color.White)) {
                    Divider(modifier = Modifier.height(2.dp), color = Color.LightGray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier
                            .clip(shape = CircleShape)
                            .clickable { }
                            .background(color = DeepOrange)
                            .padding(horizontal = 24.dp, vertical = 8.dp)) {
                            Text(
                                text = "Practice all cards",
                                color = Color.White,
                                style = MaterialTheme.typography.h5,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = deck.cardList.size.toString() + if (deck.cardList.size > 1) " Cards" else " Card",
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                deck.cardList.forEach {
                    CardItem(card = Card(it.front, it.back))
                    Spacer(modifier = Modifier.height(8.dp))
                }

            }
        }
    }

}

@Composable
fun CardItem(card: Card) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.LightGray)
    ) {
        Text(
            text = card.front,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.ExtraBold
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp), color = Color.LightGray
        )
        Text(
            text = card.back,
            modifier = Modifier.padding(16.dp),
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}
