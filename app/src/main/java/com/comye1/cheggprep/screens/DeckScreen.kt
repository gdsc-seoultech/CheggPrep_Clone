package com.comye1.cheggprep.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comye1.cheggprep.models.Card
import com.comye1.cheggprep.models.DECK_CREATED
import com.comye1.cheggprep.models.Deck
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.ui.theme.Teal
import com.comye1.cheggprep.viewmodel.DeckViewModel
import com.google.accompanist.pager.ExperimentalPagerApi

@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@ExperimentalPagerApi
@Composable
fun DeckScreen(
    navController: NavController,
    key: String,
    update: () -> Unit,
    shareDeck: (String) -> Unit
) {

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
                navController.popBackStack() // 이전 화면으로 돌아가기
            }
        }
        is DeckViewModel.DeckState.Valid -> {

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
                                title = { Text(text = viewModel.deck.value!!.deckTitle) },
                                navigationIcon = {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBack,
                                            contentDescription = "navigate back"
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { shareDeck(viewModel.deck.value!!.toText()) }) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "share"
                                        )
                                    }
                                    when {
                                        viewModel.deck.value!!.deckType == DECK_CREATED -> {
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
                                                    viewModel.editCardList()
                                                    subNavController.navigate("add_card")
                                                },
                                                delete = {
                                                    navController.popBackStack()
                                                    viewModel.deleteDeck(key = viewModel.deck.value!!.key)
                                                    update() // HomeScreen 업데이트 필요
                                                }
                                            )
                                        }
                                        viewModel.deck.value!!.bookmarked -> {
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteBookmark(key = viewModel.deck.value!!.key)
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
                                                    viewModel.addBookmark(key = viewModel.deck.value!!.key) // 되어있는 경우
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
                            DeckScreenBottomBar {
                                // Deck 추가하기
                                // 업데이트 판단
                                viewModel.addDeckToMyList(key) { update() }
                                subNavController.navigate("practice")
                            }
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp, 16.dp, 16.dp, 80.dp)
                        ) {
                            viewModel.deck.value!!.cardList.size.let {
                                Text(
                                    text = it.toString() + if (it > 1) " Cards" else " Card",
                                    color = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn {
                                if (viewModel.deck.value!!.deckType == DECK_CREATED) {
                                    itemsIndexed(viewModel.deck.value!!.cardList) { index, card ->
                                        MyCardItem(
                                            card = Card(card.front, card.back),
                                            edit = {
                                                viewModel.editCardList()
                                                subNavController.navigate("edit_card/${index}")
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                } else {
                                    items(viewModel.deck.value!!.cardList) {
                                        CardItem(card = Card(it.front, it.back))
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                composable("edit_detail") {
                    val (deckTitle, setDeckTitle) = rememberSaveable {
                        mutableStateOf(viewModel.deck.value!!.deckTitle)
                    }

                    val (visibility, setVisibility) = rememberSaveable {
                        mutableStateOf(viewModel.deck.value!!.shared)
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

                        viewModel.updateDeckDetail(title = deckTitle, shared = visibility)
                        {   // 리턴 값이 true일 때 업데이트 필요
                            update()
                        }
                        subNavController.popBackStack()
                    }
                }
                composable(
                    "edit_card/{index}",
                    arguments = listOf(navArgument("index") {
                        type = NavType.IntType
                        defaultValue = 0
                    })
                ) { backStackEntry ->
                    val index = backStackEntry.arguments?.getInt("index") ?: 0
                    viewModel.edittingCardList.let { cardList ->
                        CreateCardScreen(
                            startIndex = index, // TODO 선택된 인덱스(navigation argument)로 설정해야 함
                            cardList = cardList, //SnapshotStateList<Card>가 전달된다
                            setCard = { index, card ->
                                cardList[index] = card // Card field 변경
                            },
                            addCard = { cardList.add(Card("", "")) }, // 새 Card 추가
                            removeCard = { index ->
                                cardList.removeAt(index) // Card 삭제
                                if (cardList.size == 0) cardList.add(Card("", ""))
                                // 삭제된 뒤에 cardList 사이즈가 0인 경우 새 Card 추가
                            },
                            navigateBack = {
                                subNavController.popBackStack()
                            },
                            onDone = {
                                // 뷰모델 함수 호출
                                // popBackStack
                                viewModel.updateCardList { update() }
                                subNavController.popBackStack()
                            }
                        )

                    }
                }
                composable("add_card") {
                    viewModel.edittingCardList.let { cardList ->
                        CreateCardScreen(
                            startIndex = -1,
                            cardList = cardList, //SnapshotStateList<Card>가 전달된다
                            setCard = { index, card ->
                                cardList[index] = card // Card field 변경
                            },
                            addCard = { cardList.add(Card("", "")) }, // 새 Card 추가
                            removeCard = { index ->
                                cardList.removeAt(index) // Card 삭제
                                if (cardList.size == 0) cardList.add(Card("", ""))
                                // 삭제된 뒤에 cardList 사이즈가 0인 경우 새 Card 추가
                            },
                            navigateBack = {
                                subNavController.popBackStack()
                            },
                            onDone = {
                                // 뷰모델 함수 호출
                                // popBackStack
                                viewModel.updateCardList { update() }
                                subNavController.popBackStack()
                            }
                        )

                    }
                }
                composable("practice") {
                    PracticeScreen(cardList = viewModel.deck.value!!.cardList) {
                        subNavController.popBackStack()
                    }
                }
            }

        }
        else -> {
        }
    }
}

@Composable
fun DeckScreenBottomBar(onClick: () -> Unit) {
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
                .clickable { onClick() }
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
    Dialog(onDismissRequest = { delete() }) {
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

@Composable
fun MyCardItem(card: Card, edit: () -> Unit) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = edit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "edit this card",
                    tint = Color.LightGray
                )
            }
        }
    }
}

private fun Deck.toText(): String {
    var str = "[Chegg Prep]\n"
    str += "${this.deckTitle} - "
    this.cardList.size.let {
        str += it.toString() + if (it > 1) " cards" else " card"
    }
    this.cardList.forEachIndexed { idx, card ->
        str += "\n\n▶ ${idx + 1}. ${card.front} \n${card.back}"
    }
    return str
}