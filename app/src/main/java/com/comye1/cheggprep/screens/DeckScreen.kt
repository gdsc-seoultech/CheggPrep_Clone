package com.comye1.cheggprep.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.DECK_ONLY_BOOKMARKED
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.ui.theme.Teal
import com.comye1.cheggprep.viewmodel.DeckViewModel

@Composable
fun DeckScreen(navController: NavController, key: String, update: () -> Unit) {

    val viewModel: DeckViewModel = viewModel()

    LaunchedEffect(key1 = true) {
        viewModel.getDeckByKey(key)
    }

    val deckState = viewModel.deckState.collectAsState().value


    val context = LocalContext.current
    val signInNeededToast: () -> Unit = { // 로그인 해야한다는 토스트 메시지 띄우기
        Toast.makeText(context, "You should sign in for this feature.", Toast.LENGTH_SHORT).show()
    }

    val subNavController = rememberNavController()

    when (deckState) {
        is DeckViewModel.DeckState.Deleted -> { //원본이 삭제됨
            DeletedDeckDialog {
                viewModel.deleteBookmark(key) // 북마크에서 삭제
                update() // HomeScreen 업데이트 필요
            }
        }
        is DeckViewModel.DeckState.Valid -> {
            deckState.deck.let { deck ->

                NavHost(navController = subNavController, startDestination = "main") {
                    composable("main") {
                        val (userMenuExpanded, setUserMenuExpanded) = remember {
                            mutableStateOf(false)
                        }

                        Scaffold(
                            topBar = {
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
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "share"
                                            )
                                        }
                                        when (deck.deckType) {
                                            DECK_CREATED -> {
                                                IconButton(onClick = { setUserMenuExpanded(true) }) {
                                                    Icon(
                                                        imageVector = Icons.Default.MoreVert,
                                                        contentDescription = "more"
                                                    )
                                                }
                                                UserDeckMenu(
                                                    expanded = userMenuExpanded,
                                                    dismiss = { setUserMenuExpanded(false) },
                                                    editTitle = {
                                                        subNavController.navigate("edit_detail")
                                                    },
                                                    addCard = {
                                                        subNavController.navigate("add_card")
                                                    },
                                                    delete = {
                                                        navController.popBackStack()
                                                        viewModel.deleteDeck(key = deck.key)
                                                        update() // HomeScreen 업데이트 필요
                                                    }
                                                )
                                            }
                                            DECK_ONLY_BOOKMARKED -> {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteBookmark(key = deck.key)
                                                        update() // HomeScreen 업데이트 필요
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Bookmark,
                                                        contentDescription = "delete bookmark"
                                                    )
                                                }
                                            }
                                            else -> {
                                                IconButton(onClick = {
                                                    if (viewModel.user == null) {
                                                        signInNeededToast() // 로그인 안 되어있는 경우
                                                    } else {
                                                        viewModel.addBookmark(key = deck.key) // 되어있는 경우
                                                        update() // HomeScreen 업데이트 필요
                                                    }
                                                }) {
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
                                    Divider(
                                        modifier = Modifier.height(2.dp),
                                        color = Color.LightGray
                                    )
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
                    composable("edit_detail") {
                        val (deckTitle, setDeckTitle) = rememberSaveable {
                            mutableStateOf(deck.deckTitle)
                        }

                        val (visibility, setVisibility) = rememberSaveable {
                            mutableStateOf(deck.shared)
                        }

                        EditDeck(
                            deckTitle = deckTitle,
                            setDeckTitle = setDeckTitle,
                            visibility = visibility,
                            setVisibility = setVisibility,
                            navigateBack = {
                                subNavController.popBackStack()
                            }
                        ) { // onDone

                            if (viewModel.updateDeckDetail(title = deckTitle, shared = visibility)) {
                                // 리턴 값이 true일 때 업데이트 필요
                                update()
                            }
                            subNavController.popBackStack()
                        }
                    }
                    composable("edit_card") {

                    }
                    composable("add_card") {

                    }
                }


            }
        }
        else -> {
        }
    }
}

@Composable
fun UserDeckMenu(
    expanded: Boolean,
    dismiss: () -> Unit,
    editTitle: () -> Unit,
    addCard: () -> Unit,
    delete: () -> Unit
) {
    /*
    Edit -> title, visibility 수정, Done 버튼
    Add -> card 추가
    Delete -> dialog -> HomeScreen
     */
    val showDeleteDialog = remember {
        mutableStateOf(false)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { dismiss() },
        modifier = Modifier.width(180.dp)
    ) {
        DropdownMenuItem(
            onClick = {
                editTitle()
                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Edit deck details")
        }
        DropdownMenuItem(
            onClick = {
                addCard()
                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add card")
        }
        DropdownMenuItem(
            onClick = {
                /*
                Dialog 띄워서
                 */
                showDeleteDialog.value = true
//                dismiss()
            }
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Edit")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Delete")
        }
        if (showDeleteDialog.value) {
            DeleteConfirmDialog(
                cancel = { showDeleteDialog.value = false },
                delete = { delete() }
            )
        }
    }
}

/*
북마크한 Deck의 원본이 사라졌을 때 띄우는 대화상자 - Delete 버튼을 누르면 북마크에서 사라진다.
 */
@Composable
fun DeletedDeckDialog(delete: () -> Unit) {
    Dialog(onDismissRequest = { }) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Owner deleted this deck.",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "It will be deleted from bookmark list.",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Delete",
                    modifier = Modifier
                        .clickable { delete() }
                        .padding(8.dp),
                    color = Teal,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmDialog(cancel: () -> Unit, delete: () -> Unit) {
    Dialog(onDismissRequest = { cancel() }) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Text(
                text = "Are you sure you want to delete this deck?",
                style = MaterialTheme.typography.h6,
                color = Color.Black,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .clickable { cancel() }
                        .padding(8.dp),
                    color = Teal,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Delete",
                    modifier = Modifier
                        .clickable { delete() }
                        .padding(8.dp),
                    color = Teal,
                    fontWeight = FontWeight.SemiBold
                )
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
