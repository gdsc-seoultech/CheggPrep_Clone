package com.comye1.cheggprep.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.comye1.cheggprep.SampleDataSet
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.models.DeckForAll
import com.comye1.cheggprep.models.DeckForUser
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    database.child("all/decks/$key").get().addOnSuccessListener { all ->
        database.child("user/${user.uid}/decks/$key").get().addOnSuccessListener { user ->
            Log.d("firebase all", all.toString())
            Log.d("firebase user", user.toString())

            val deckForAll = all.getValue(DeckForAll::class.java)
            val deckForUser = user.getValue(DeckForUser::class.java)

            if (deckForAll != null && deckForUser != null) {
                deck = Deck(
                    deckTitle = deckForAll.deckTitle,
                    deckType = deckForUser.deckType,
                    cardList = deckForAll.cardList,
                    bookmarked = deckForUser.bookmarked,
                    shared = deckForAll.shared,
                    key = key
                )
            }
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
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "more")
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

//fun getDeckByKey(key: String): Deck? {
//}