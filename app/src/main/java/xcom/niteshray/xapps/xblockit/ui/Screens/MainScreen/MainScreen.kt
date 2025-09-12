package xcom.niteshray.xapps.xblockit.ui.Screens.MainScreen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.xapps.xblockit.model.Screen
import xcom.niteshray.xapps.xblockit.ui.Screens.Home.HomeScreen
import xcom.niteshray.xapps.xblockit.ui.Screens.Web.WebScreen
import xcom.niteshray.xapps.xblockit.ui.theme.Black
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xcom.niteshray.xapps.xblockit.ui.Screens.App.AppScreen
import xcom.niteshray.xapps.xblockit.ui.theme.lightblue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController){
    val innerNavControlller = rememberNavController()
    val screens = listOf(Screen.Home, Screen.App, Screen.Web)
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
                    IconButton(onClick = { showPrivacyDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Privacy Policy",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Black
            ){
                val navBackStackEntry by innerNavControlller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            innerNavControlller.navigate(screen.route) {
                                popUpTo(innerNavControlller.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(screen.title) },
                        icon = {
                            when (screen.icon) {
                                is ImageVector -> Icon(
                                    screen.icon, contentDescription = screen.title , modifier = Modifier.size(24.dp))
                                is Int -> Icon(painterResource(id = screen.icon), contentDescription = screen.title, modifier = Modifier.size(24.dp))
                                else -> {  }
                            }
                               } ,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = lightblue,
                            selectedTextColor = lightblue,
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        },
        containerColor = Color.Black
    ){ pd->
        Box(
            modifier = Modifier.padding(pd)
        ) {
            if (showPrivacyDialog) {
                AlertDialog(
                    onDismissRequest = { showPrivacyDialog = false },
                    confirmButton = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = {
                                    showPrivacyDialog = false
                                    val uri = "https://buymeacoffee.com/im_nitesh"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Favorite,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                    Text(
                                        text = "Support Me",
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    showPrivacyDialog = false
                                    val uri =
                                        "https://mrniteshray.github.io/Privacy-Policy/BLOCKIT"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF40C4FF),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "Privacy Policy",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    dismissButton = {},
                    title = {
                        Text(
                            text = "Support & Privacy",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    },
                    text = {
                        Text(
                            text = "Support the app or view our Privacy Policy.",
                            color = Color(0xFFB0BEC5),
                            textAlign = TextAlign.Center
                        )
                    },
                    containerColor = Color(0xFF212121),
                    titleContentColor = Color.White,
                    textContentColor = Color(0xFFB0BEC5)
                )
            }

            NavHost(navController = innerNavControlller , startDestination = "home"){
                composable("home"){
                    HomeScreen()
                }
                composable("app"){
                    AppScreen()
                }
                composable("web"){
                    WebScreen()
                }
            }

        }
    }
}
