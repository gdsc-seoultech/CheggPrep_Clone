package com.comye1.cheggprep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.cheggprep.navigation.BottomNavigationBar
import com.comye1.cheggprep.navigation.Screen
import com.comye1.cheggprep.screens.*
import com.comye1.cheggprep.ui.theme.CheggPrepTheme

// TODO : 3Cards ( 띄어쓰기 ) BottomNavigationBar scale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CheggPrepTheme {
                val navController = rememberNavController()

                val (bottomBarShown, showBottomBar) = remember {
                    mutableStateOf(true)
                }

                Scaffold(
                    bottomBar = {
                        if(bottomBarShown){
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            showBottomBar(true)
                            HomeScreen(navController)
                        }
                        composable(Screen.Search.route) {
                            showBottomBar(true)
                            SearchScreen(navController)
                        }
                        composable(Screen.Create.route) {
                            showBottomBar(false)
                            CreateScreen(navController)
                        }
                        composable(Screen.More.route) {
                            showBottomBar(true)
                            MoreScreen(navController)
                        }
                        composable(Screen.Deck.route +"/{deckTitle}/{cardsNum}") { backStackEntry ->
                            val deckTitle = backStackEntry.arguments?.getString("deckTitle") ?: "invalid card"
                            val cardsNum = backStackEntry.arguments?.getString("cardsNum")?.toInt() ?: 0
                            showBottomBar(false)
                            DeckScreen(navController = navController, title = deckTitle, cardsNum = cardsNum)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}


@Composable
fun DeckInSubject() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.LightGray
            )
            .clickable {

            }
            .padding(16.dp)) {
        Text(
            text = "recursion",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "8 Cards",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}

@Composable
fun StudyGuide() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.LightGray
            )
            .clickable {

            }
            .padding(16.dp)) {
        Text(
            text = "c-plus-plus",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "12 Decks · 207 Cards",
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
    }
}


@Composable
fun MyDeckItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = Color.LightGray
            )
            .clickable {

            }
            .padding(16.dp)) {
        Text(
            text = "recursion",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "11 Cards",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Icon(
                imageVector = Icons.Default.VisibilityOff,
                contentDescription = "visibility_off",
                tint = Color.Gray
            )
        }

    }
}


