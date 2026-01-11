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
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResult
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallResultHandler

class MainActivity : ComponentActivity() {
    
    private lateinit var paywallLauncher: PaywallActivityLauncher
    private val premiumUnlockedCallbacks = mutableListOf<() -> Unit>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize PaywallActivityLauncher before setContent
        paywallLauncher = PaywallActivityLauncher(
            this,
            object : PaywallResultHandler {
                override fun onActivityResult(result: PaywallResult) {
                    when (result) {
                        is PaywallResult.Purchased -> {
                            Log.d("RC", "Purchase successful!")
                            val customerInfo = result.customerInfo
                            if (customerInfo.entitlements.active.containsKey("premium")) {
                                Toast.makeText(this@MainActivity, "Premium unlocked! 🎉", Toast.LENGTH_LONG).show()
                                premiumUnlockedCallbacks.forEach { it() }
                            } else {
                                Toast.makeText(this@MainActivity, "Purchase completed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is PaywallResult.Restored -> {
                            Log.d("RC", "Purchase restored!")
                            val customerInfo = result.customerInfo
                            if (customerInfo.entitlements.active.containsKey("premium")) {
                                Toast.makeText(this@MainActivity, "Premium restored! 🎉", Toast.LENGTH_LONG).show()
                                premiumUnlockedCallbacks.forEach { it() }
                            } else {
                                Toast.makeText(this@MainActivity, "No purchases to restore", Toast.LENGTH_SHORT).show()
                            }
                        }
                        is PaywallResult.Cancelled -> {
                            Log.d("RC", "Paywall cancelled")
                            Toast.makeText(this@MainActivity, "Purchase cancelled", Toast.LENGTH_SHORT).show()
                        }
                        is PaywallResult.Error -> {
                            Log.e("RC", "Error: ${result.error.message}")
                            Toast.makeText(this@MainActivity, "Error: ${result.error.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        )
        
        enableEdgeToEdge()
        setContent {
            BlockitTheme {
                App()
            }
        }
    }
    
    fun getPaywallLauncher() = paywallLauncher
    
    fun registerPremiumCallback(callback: () -> Unit) {
        premiumUnlockedCallbacks.add(callback)
    }
    
    fun unregisterPremiumCallback(callback: () -> Unit) {
        premiumUnlockedCallbacks.remove(callback)
    }
    
    @Composable
    fun App(){
        val navController = rememberNavController()
        NavHost(navController = navController , startDestination = "main"){
            composable(route = "main"){
                MainScreen(navController)
            }
        }
    }
}

