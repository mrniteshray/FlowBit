package xcom.niteshray.xapps.xblockit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.xapps.xblockit.ui.theme.BlockitTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument
import xcom.niteshray.xapps.xblockit.feature.MainScreen.MainScreen
import xcom.niteshray.xapps.xblockit.feature.paywall.PaywallDialog


class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        // Enable Immersive Mode (Hide System Bars)
        val windowInsetsController = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
        
        setContent {
            BlockitTheme {
                App()
            }
        }
    }
    
    @Composable
    fun App(){
        val navController = rememberNavController()
        NavHost(navController = navController , startDestination = "main"){
            composable(route = "main"){
                MainScreen(navController)
            }
            composable(route = "paywall") {
                PaywallDialog(onDismiss = { navController.popBackStack() })
            }
        }
    }
}

