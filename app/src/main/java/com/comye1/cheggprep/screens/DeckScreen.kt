package com.comye1.cheggprep.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.DECK_ONLY_BOOKMARKED
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.viewmodel.DeckViewModel

@Composable
fun DeckScreen(navController: NavController, key: String) {

    val viewModel: DeckViewModel = viewModel()

    LaunchedEffect(key1 = true) {
        viewModel.getDeckByKey(key)
    }

    val deck by remember {
        viewModel.deck
    }

    val (userMenuExpanded, setUserMenuExpanded) = remember {
        mutableStateOf(false)
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
                    when (deck.deckType) {
                        DECK_CREATED -> {
                            IconButton(onClick = { setUserMenuExpanded(true) }) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "more"
                                )
                            }

                            UserDeckMenu(expanded = userMenuExpanded, dismiss = {
                                setUserMenuExpanded(false)
                            }
                            )
                        }
                        DECK_ONLY_BOOKMARKED -> {
                            IconButton(onClick = { viewModel.deleteBookmark(key = key) }) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "add bookmark"
                                )
                            }
                        }
                        else -> {
                            IconButton(onClick = { viewModel.addBookmark(key = key) }) {
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
fun UserDeckMenu(expanded: Boolean, dismiss: () -> Unit) {
    /*
    Edit -> title, visibility 수정, Done 버튼
    Add -> card 추가
    Delete -> dialog -> HomeScreen
     */
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { dismiss() },
        modifier = Modifier.width(180.dp)
    ) {
        DropdownMenuItem(
            onClick = {
                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Edit deck details")
        }
        DropdownMenuItem(
            onClick = {

                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add card")
        }
        DropdownMenuItem(
            onClick = {

                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Delete")
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
