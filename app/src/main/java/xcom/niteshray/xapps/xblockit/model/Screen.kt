package xcom.niteshray.xapps.xblockit.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector){
    object Home : Screen("home","Home", Icons.Default.Home)
    object Block : Screen("block","Block", Icons.Default.Shield)
}