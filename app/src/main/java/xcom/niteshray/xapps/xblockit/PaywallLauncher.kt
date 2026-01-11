package xcom.niteshray.xapps.xblockit


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.revenuecat.purchases.ui.revenuecatui.activity.PaywallActivityLauncher

@Composable
fun rememberPaywallLauncher(
    onPremiumUnlocked: () -> Unit
): PaywallActivityLauncher {

    val activity = LocalContext.current as? MainActivity
        ?: throw IllegalStateException("PaywallLauncher must be used within MainActivity")

    DisposableEffect(onPremiumUnlocked) {
        activity.registerPremiumCallback(onPremiumUnlocked)
        onDispose {
            activity.unregisterPremiumCallback(onPremiumUnlocked)
        }
    }

    return remember {
        activity.getPaywallLauncher()
    }
}

