package com.comye1.cheggprep

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.comye1.cheggprep.navigation.BottomNavigationBar
import com.comye1.cheggprep.navigation.Screen
import com.comye1.cheggprep.screens.*
import com.comye1.cheggprep.ui.theme.CheggPrepTheme
import com.comye1.cheggprep.utils.DotsTyping
import com.comye1.cheggprep.viewmodel.CreateViewModel
import com.comye1.cheggprep.viewmodel.HomeViewModel
import com.comye1.cheggprep.viewmodel.MoreViewModel
import com.comye1.cheggprep.viewmodel.SearchViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

// TODO : BottomNavigationBar scale / popUpTo 옵션 지정

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    private fun shareText(text: String) {
        val shareIntent = Intent.createChooser(getSendIntent(text), null)
        // ACTION_SEND나 ACTION_SEND_MULTIPLE가 아닐 때에만 title을 지정할 수 있다.
        startActivity(shareIntent)
    }

    private fun getSendIntent(text: String) =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {

            CheggPrepTheme {
                val navController = rememberNavController()

                val moreViewModel: MoreViewModel = viewModel()
                val createViewModel: CreateViewModel = viewModel()
                val homeViewModel: HomeViewModel = viewModel()
                val searchViewModel: SearchViewModel = viewModel()

                auth.currentUser?.let {  // currentUser가 null이 아니면 뷰모델 안의 user로 설정
                    LaunchedEffect(key1 = true) {
                        moreViewModel.signIn(it.email!!, it.displayName!!)
                        user = FirebaseAuth.getInstance().currentUser!!
                    }
                }

                val updateNeeded = remember {
                    mutableStateOf(false)
                }

                /*
                event flow를 보내서 signIn / signOut을 전달받고
                각 viewModel의 함수를 호출해서 새롭게 로드할 수 있을까?!
                 */
                lifecycleScope.launchWhenCreated {
                    moreViewModel.userEvent.collect { event ->
                        when (event) {
                            MoreViewModel.Companion.UserEvent.SignIn -> {
                                updateNeeded.value = true
                            }
                            MoreViewModel.Companion.UserEvent.SignOut -> {
                                homeViewModel.clearDecks()
                            }
                        }
                    }
                }


                if (moreViewModel.firebaseAuth.value) { // firebaseAuth가 true일 때
                    firebaseAuthWithGoogle(moreViewModel.token.value, moreViewModel::signIn)
                    moreViewModel.completeAuth() // 로그인 후 false로 변경
                }

                val (bottomBarShown, showBottomBar) = remember {
                    mutableStateOf(true)
                }

                Scaffold(
                    bottomBar = {
                        if (bottomBarShown) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = Screen.Home.route) {
                        composable(Screen.Home.route) {
                            showBottomBar(true)
                            if (updateNeeded.value) {
                                Log.d("homerefresh", "called")
                                DotsTyping()
                                LaunchedEffect(key1 = true) {
                                    homeViewModel.refresh()
                                    delay(800L)
                                    updateNeeded.value = false
                                }
                            } else {
                                HomeScreen(navController, homeViewModel)
                            }
                        }
                        composable(Screen.Search.route) {
                            showBottomBar(true)
                            SearchScreen(navController, searchViewModel)
                        }
                        composable(Screen.Create.route) {
                            showBottomBar(false)
                            CreateScreen(navController, createViewModel) {
                                updateNeeded.value = true
                            }
                        }
                        composable(Screen.More.route) {
                            showBottomBar(true)
                            MoreScreen(moreViewModel)
                        }
                        composable(
                            Screen.Deck.route + "/{key}",
                            arguments = listOf(navArgument("key") {
                                type = NavType.StringType
                                defaultValue = ""
                            })
                        ) { backStackEntry ->
                            val key = backStackEntry.arguments?.getString("key") ?: ""
                            showBottomBar(false)
                            DeckScreen(
                                navController = navController,
                                key = key,
                                update = { updateNeeded.value = true },
                                shareDeck = { shareText(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        signIn: (String, String) -> Unit
    ) { // 로그인 할 때
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential) // Firebase 에 구글 계정으로 인증
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Firebase", "signInWithCredential:success")
                    signIn(auth.currentUser!!.email!!, auth.currentUser!!.displayName!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.d("Firebase", "signInWithCredential:failure", task.exception)

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


