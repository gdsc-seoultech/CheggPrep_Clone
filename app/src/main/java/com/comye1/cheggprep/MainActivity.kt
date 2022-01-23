package com.comye1.cheggprep

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.comye1.cheggprep.navigation.BottomNavigationBar
import com.comye1.cheggprep.navigation.Screen
import com.comye1.cheggprep.screens.*
import com.comye1.cheggprep.ui.theme.CheggPrepTheme
import com.comye1.cheggprep.viewmodel.CreateViewModel
import com.comye1.cheggprep.viewmodel.HomeViewModel
import com.comye1.cheggprep.viewmodel.MoreViewModel
import com.comye1.cheggprep.viewmodel.SearchViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// TODO : BottomNavigationBar scale / popUpTo 옵션 지정

@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

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

                val firebaseAuth = moreViewModel.firebaseAuth.collectAsState()

                if (firebaseAuth.value) { // firebaseAuth가 true일 때
                    firebaseAuthWithGoogle(moreViewModel.token.value, moreViewModel::signIn)
                    moreViewModel.completeAuth() // 로그인 후 false로 변경
                }

                val (bottomBarShown, showBottomBar) = remember {
                    mutableStateOf(true)
                }

                /*
                firebase 저장 실험
                 */
//                Firebase.database.reference.child("all").setValue("sample").addOnSuccessListener {
//                    Toast.makeText(this, "firebase", Toast.LENGTH_LONG).show()
//                }

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
                            HomeScreen(navController, homeViewModel)
                        }
                        composable(Screen.Search.route) {
                            showBottomBar(true)
                            SearchScreen(navController, searchViewModel)
                        }
                        composable(Screen.Create.route) {
                            showBottomBar(false)
                            CreateScreen(navController, createViewModel)
                        }
                        composable(Screen.More.route) {
                            showBottomBar(true)
                            MoreScreen(navController, moreViewModel)
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
                }
                else {
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


