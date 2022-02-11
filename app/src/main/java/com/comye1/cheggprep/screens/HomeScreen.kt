package com.comye1.cheggprep.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.comye1.cheggprep.models.DECK_ADDED
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.DECK_ONLY_BOOKMARKED
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.navigation.Screen
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.ui.theme.Teal
import com.comye1.cheggprep.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel) {
    var (selectedFilterIndex, setFilterIndex) = rememberSaveable { // Deck 상세화면 이동 후에도 유지
        mutableStateOf(0)
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 4.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            ) {
                Text(
                    text = "CheggPrep",
                    style = MaterialTheme.typography.h5,
                    color = DeepOrange,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                FilterSection(selectedFilterIndex, setFilterIndex)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
        ) {
            /*
            key 역순으로 (최신순) 정렬
             */
            if (viewModel.user != null) {
                viewModel.myDeckList.sortedByDescending { it.key }.let { list ->
                    when (selectedFilterIndex) {
                        0 ->
                            list.forEach {
                                item {
                                    DeckItem(deck = it, modifier = Modifier.padding(bottom = 8.dp))
                                    {
                                        navController.navigate(
                                            Screen.Deck.route + "/${it.key}"
                                        )
                                    }
                                }

                            }
                        1 ->
                            list.filter { it.bookmarked }.forEach {
                                item {
                                    DeckItem(deck = it, modifier = Modifier.padding(bottom = 8.dp))
                                    {
                                        navController.navigate(
                                            Screen.Deck.route + "/${it.key}"
                                        )
                                    }
                                }
                            }

                        2 ->
                            list.filter { it.deckType == DECK_CREATED }.forEach {
                                item {
                                    DeckItem(deck = it, modifier = Modifier.padding(bottom = 8.dp))
                                    {
                                        navController.navigate(
                                            Screen.Deck.route + "/${it.key}"
                                        )
                                    }
                                }
                            }
                    }
                    item {
                        MakeMyDeck(onClick = { navController.navigate(Screen.Create.route) })
                    }
                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            } else {
                item {
                    MakeMyDeck(onClick = {
                        navController.navigate(Screen.More.route)
                        Toast.makeText(
                            context,
                            "You need to sign in to create a card.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })
                }

            }
        }
    }
}

@Composable
fun FilterSection(selectedFilterIndex: Int, setIndex: (Int) -> Unit) {
    Row {
        FilterText("All", selectedFilterIndex == 0) { setIndex(0) }
        Spacer(modifier = Modifier.width(8.dp))
        FilterText("Bookmarks", selectedFilterIndex == 1) { setIndex(1) }
        Spacer(modifier = Modifier.width(8.dp))
        FilterText("Created", selectedFilterIndex == 2) { setIndex(2) }
    }
}

@Composable
fun FilterText(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = !selected, onClick = onClick)
            .background(color = if (selected) Color.LightGray else Color.Transparent)
            .padding(horizontal = 20.dp, vertical = 2.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.body1, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
fun MakeMyDeck(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.LightGray
            )
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Text(
            text = "Make your own cards",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "It's easy to create your own flashcard deck -for free.",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Default.NoteAdd,
                contentDescription = "bookmark",
                tint = Teal
            )
            Text(
                text = "Get started",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Teal
            )
        }
    }
}

@Composable
fun DeckItem(deck: Deck, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.LightGray
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = deck.deckTitle,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = deck.cardList.size.toString() + if (deck.cardList.size > 1) " Cards" else " Card",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            // 아이콘 부분
            when (deck.deckType) {
                DECK_CREATED -> {
                    if (deck.shared) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "shared",
                            tint = Color.Gray
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "not shared",
                            tint = Color.Gray
                        )
                    }
                }
                DECK_ONLY_BOOKMARKED -> {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "bookmark",
                        tint = Color.Gray
                    )
                }
                DECK_ADDED -> {
                    if (deck.bookmarked) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "bookmark",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


