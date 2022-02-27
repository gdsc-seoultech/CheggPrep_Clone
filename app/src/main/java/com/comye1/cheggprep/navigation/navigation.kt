package com.comye1.cheggprep.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.comye1.cheggprep.ui.theme.DeepOrange

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Create : Screen("create")
    object More : Screen("more")
    object Deck : Screen("deck")
}

data class BottomNavItem(
    val route: String,
    val name: String,
    val icon: ImageVector
)

object BottomNav {
    val items = listOf(
        BottomNavItem(Screen.Home.route, "Home", Icons.Outlined.Home),
        BottomNavItem(Screen.Search.route, "Search", Icons.Outlined.Search),
        BottomNavItem(Screen.Create.route, "Create", Icons.Outlined.AddBox),
        BottomNavItem(Screen.More.route, "More", Icons.Outlined.Menu)
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route

    BottomNavigation(
        backgroundColor = Color.White,
        elevation = 1.dp,
        modifier = Modifier.padding(4.dp)
    ) {
        BottomNav.items.forEach { item ->
            BottomNavigationItem(
                selected = item.route == currentRoute,
                enabled = item.route != currentRoute,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(Screen.Home.route)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selectedContentColor = DeepOrange,
                unselectedContentColor = Color.DarkGray
            )
        }
    }
}