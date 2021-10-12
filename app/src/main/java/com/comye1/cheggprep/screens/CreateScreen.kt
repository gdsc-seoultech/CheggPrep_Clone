package com.comye1.cheggprep.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.ui.theme.LightOrange

@Composable
fun CreateScreen(navController: NavHostController) {

    val (deckTitle, setDeckTitle) = remember {
        mutableStateOf("")
    }

    val (visibility, setVisibility) = remember {
        mutableStateOf(true)
    }

    Scaffold(topBar = {
        TopAppBar(
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Create new deck",
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "close create screen"
                    )
                }
            },
            actions = {
                TextButton(onClick = { /*TODO*/ }, enabled = deckTitle.isNotBlank()) {
                    Text(
                        "Next",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            DeckTitleTextField(deckTitle, setDeckTitle)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Visible to everyone",
                    style = MaterialTheme.typography.h5,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = visibility,
                    onCheckedChange = setVisibility,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepOrange,
                        checkedTrackColor = LightOrange
                    )
                )
            }
            Text(text = "Other Students can find, view, and study\nthis deck")
        }
    }
}

@Composable
fun DeckTitleTextField(text: String, setText: (String) -> Unit) {
    TextField(
        value = text, onValueChange = setText,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.h4,
        placeholder = {
            Text(
                text = "Untitled deck",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold,
                color = Color.LightGray
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            cursorColor = DeepOrange,
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.LightGray,
            unfocusedIndicatorColor = Color.LightGray
        ),
        maxLines = 2
    )
}
