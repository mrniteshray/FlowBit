package xcom.niteshray.xapps.xblockit.feature.MainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.xapps.xblockit.component.FloatingBottomNav
import xcom.niteshray.xapps.xblockit.feature.block.presentation.BlockScreen
import xcom.niteshray.xapps.xblockit.feature.focus.FocusScreen
import xcom.niteshray.xapps.xblockit.feature.focus.service.FocusManager
import xcom.niteshray.xapps.xblockit.model.Screen

/**
 * Main screen with floating bottom navigation
 * Currently showing Focus and Block screens only
 */
@Composable
fun MainScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val screens = listOf(Screen.Focus, Screen.Block)
    
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Observe Focus State
    val focusState by FocusManager.state.collectAsState()
    val isTimerRunning = focusState.isRunning

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content
        NavHost(
            navController = innerNavController,
            startDestination = "focus",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("focus") {
                FocusScreen(onNavigateToPaywall = { navController.navigate("paywall") })
            }
            composable("block") {
                BlockScreen(onNavigateToPaywall = { navController.navigate("paywall") })
            }
        }
        
        // Floating Bottom Navigation
        AnimatedVisibility(
            visible = !isTimerRunning,
            enter = slideInVertically(
                initialOffsetY = { it }, // Slide up from bottom
                animationSpec = tween( durationMillis = 300)
            ) + fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = slideOutVertically(
                targetOffsetY = { it }, // Slide down to bottom
                animationSpec = tween( durationMillis = 300)
            ) + fadeOut(animationSpec = tween(durationMillis = 300)),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingBottomNav(
                screens = screens,
                currentRoute = currentRoute,
                onNavigate = { screen ->
                    if (currentRoute != screen.route) {
                        innerNavController.navigate(screen.route) {
                            popUpTo(innerNavController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
