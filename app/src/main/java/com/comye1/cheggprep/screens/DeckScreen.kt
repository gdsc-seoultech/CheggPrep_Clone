package com.comye1.cheggprep.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.ui.theme.DeepOrange

@Composable
fun DeckScreen(navController: NavController, title: String, cardsNum: Int) {

    Scaffold(topBar = {
        TopAppBar(
            elevation = 0.dp,
            backgroundColor = Color.White,
            title = { Text(text = title) },
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
                text = cardsNum.toString() + if (cardsNum > 1) "Cards" else "Card",
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            repeat(5) {
                CardItem(card = Card("aaaaaaaaaaaa", "bbbbbbbbbbbbbbbbbbbbbbbbbbbbb"))
                Spacer(modifier = Modifier.height(8.dp))
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