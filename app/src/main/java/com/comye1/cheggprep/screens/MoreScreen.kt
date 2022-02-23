package com.comye1.cheggprep.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.comye1.cheggprep.ui.theme.DeepOrange
import com.comye1.cheggprep.ui.theme.LightOrange
import com.comye1.cheggprep.ui.theme.Teal
import com.comye1.cheggprep.utils.getGoogleSignInClient
import com.comye1.cheggprep.viewmodel.MoreState
import com.comye1.cheggprep.viewmodel.MoreViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

fun Modifier.moreModifier(onClick: () -> Unit) = this
    .fillMaxWidth()
    .clickable(onClick = onClick)
    .padding(horizontal = 8.dp, vertical = 12.dp)

@ExperimentalMaterialApi
@Composable
fun MoreScreen(viewModel: MoreViewModel) {

    val context = LocalContext.current

    val (notified, setNotified) = remember {
        mutableStateOf(true)
    }

    val notClickableModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 12.dp)

    when (viewModel.moreScreenState.value) {
        MoreState.MainScreen -> {
            Scaffold(
                topBar = { //HomeScreen에서 가져오기
                    Column(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 4.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    ) {
                        Text(
                            "CheggPrep",
                            style = MaterialTheme.typography.h5,
                            color = DeepOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            ) {
                Column {
                    if (viewModel.user.value != null) {
                        AccountSection(
                            name = viewModel.user.value!!.displayName,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                            signText = "Sign out",
                            signFunction = {
                                Firebase.auth.signOut() // 로그아웃
                                getGoogleSignInClient(context).signOut()
                                viewModel.signOut()
                            }
                        )
                    } else {
                        AccountSection(
                            name = "Guest",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
                            signText = "Sign in",
                            signFunction = viewModel::toLogInScreen
                        )
                    }
                    Divider()
                    Row(
                        modifier = notClickableModifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoreItem(
                            icon = Icons.Outlined.Notifications,
                            iconDesc = "notification",
                            text = "Push notifications"
                        )
                        Switch(
                            checked = notified,
                            onCheckedChange = setNotified,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DeepOrange,
                                checkedTrackColor = LightOrange
                            )
                        )
                    }
                    Divider()

                    Row(modifier = Modifier.moreModifier { }) {
                        MoreItem(
                            icon = Icons.Outlined.Feedback,
                            iconDesc = "give feedback",
                            text = "Give feedback",
                        )
                    }
                    Divider()
                    Row(modifier = notClickableModifier) {
                        Text(
                            text = "Other Chegg services",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.moreModifier { },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoreItem(
                            icon = Icons.Outlined.Biotech,
                            iconDesc = "Chegg Study",
                            text = "Chegg Study"
                        )
                        Icon(
                            imageVector = Icons.Outlined.FileDownload,
                            contentDescription = "download",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                    }
                    Row(
                        modifier = Modifier.moreModifier { },
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MoreItem(
                            icon = Icons.Outlined.Calculate,
                            iconDesc = "Chegg Math",
                            text = "Chegg Math"
                        )
                        Icon(
                            imageVector = Icons.Outlined.FileDownload,
                            contentDescription = "download",
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                    }
                    Divider()
                    Row(modifier = Modifier.moreModifier { }) {
                        MoreItem(
                            icon = Icons.Outlined.HelpOutline,
                            iconDesc = "help",
                            text = "Help"
                        )
                    }
                    Row(modifier = Modifier.moreModifier { }) {
                        MoreItem(
                            icon = Icons.Outlined.Info,
                            iconDesc = "info",
                            text = "About the app"
                        )
                    }
                }
            }
        }
        MoreState.LogInScreen -> {
            SignInScreen(viewModel = viewModel)
        }
    }


}

@Composable
fun AccountSection(
    name: String,
    modifier: Modifier = Modifier,
    signText: String,
    signFunction: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.AccountCircle,
            contentDescription = "account image",
            Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Hello $name",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = signText,
                color = Teal,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = signFunction)
            )
        }
    }
}

@Composable
fun MoreItem(
    icon: ImageVector,
    iconDesc: String,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = iconDesc)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontWeight = FontWeight.Bold)
    }
}
