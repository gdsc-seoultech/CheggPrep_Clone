package com.comye1.cheggprep.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.comye1.cheggprep.ui.theme.DeepOrange

enum class SearchState {
    ButtonScreen,
    QueryScreen,
    ResultScreen
}

@Composable
fun SearchScreen(navController: NavHostController) {

    val (screenState, setScreenState) = remember {
        mutableStateOf(SearchState.ButtonScreen)
    }

    val (queryString, setQueryString) = remember {
        mutableStateOf("")
    }

    when (screenState) {
        SearchState.ButtonScreen -> {
            SearchButtonScreen {
                if(queryString.isNotBlank()){
                    setScreenState(SearchState.ResultScreen)
                }else {
                    setScreenState(SearchState.QueryScreen)
                }
            }
        }
        SearchState.QueryScreen -> {
            SearchQueryScreen(
                queryString = queryString,
                setQueryString = setQueryString,
                toButtonScreen = { setScreenState(SearchState.ButtonScreen) },
                toResultScreen = { setScreenState(SearchState.ResultScreen) }
            )
        }
        SearchState.ResultScreen -> {
            SearchResultScreen(
                queryString = queryString,
                setQueryString = setQueryString,
                toButtonScreen = { setScreenState(SearchState.ButtonScreen) },
                onSearchKey = { /*검색 결과 업데이트*/ }
            )
        }
    }
}

@Composable
fun SearchResultScreen(
    queryString: String,
    setQueryString: (String) -> Unit,
    toButtonScreen: () -> Unit,
    onSearchKey: () -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                queryString = queryString,
                setQueryString = setQueryString,
                onBackButtonClick = toButtonScreen,
                onSearchKey = onSearchKey/*Todo 검색 결과 업데이트*/
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.25f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$queryString 검색 결과",
                style = MaterialTheme.typography.body1,
                fontSize = 20.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SearchQueryScreen(
    queryString: String,
    setQueryString: (String) -> Unit,
    toButtonScreen: () -> Unit,
    toResultScreen: () -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopBar(
                queryString = queryString,
                setQueryString = setQueryString,
                onBackButtonClick = toButtonScreen,
                onSearchKey = toResultScreen
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.25f),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "what are you learning today?",
                style = MaterialTheme.typography.body1,
                fontSize = 20.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SearchTopBar(
    queryString: String,
    setQueryString: (String) -> Unit,
    onBackButtonClick: () -> Unit,
    onSearchKey: () -> Unit
) {
    TopAppBar(
        elevation = 0.dp,
        backgroundColor = Color.White,
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
        },
        title = {
            TextField(
                value = queryString,
                onValueChange = setQueryString,
                placeholder = {
                    Text(
                        text = "Find flashcards",
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = DeepOrange,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onSearchKey()
                    }
                ),
            )
        },
        actions = {
            if (queryString.isNotBlank()) {
                IconButton(onClick = { setQueryString("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "delete")
                }
            }
        }
    )
}

@Composable
fun SearchButtonScreen(onButtonClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                elevation = 0.dp,
                backgroundColor = Color.Transparent,
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Find flashcards",
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            FindFlashCards(onClick = onButtonClick)
            Spacer(modifier = Modifier.height(24.dp))
            Divider(
                Modifier
                    .fillMaxWidth(.15f)
                    .height(4.dp),
                color = DeepOrange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose your subject",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Jump into studying with free flashcards that are right for you",
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(16.dp))
            repeat(7) {
                SubjectItem()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FindFlashCards(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CircleShape)
            .border(width = 1.dp, color = Color.LightGray, shape = CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "search flashcards",
            tint = Color.Gray
        )
        Text(text = " Find flashcards", style = MaterialTheme.typography.body1, color = Color.Gray)
    }
}

@Composable
fun SubjectItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                shape = RoundedCornerShape(size = 8.dp),
                width = 2.dp,
                color = Color.LightGray
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Computer,
            contentDescription = "bookmark",
            tint = DeepOrange,
            modifier = Modifier.size(36.dp)
        )
        Text(
            text = "  Computer Science",
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )
    }
}