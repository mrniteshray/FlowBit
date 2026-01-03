package xcom.niteshray.xapps.xblockit.feature.MainScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import xcom.niteshray.xapps.xblockit.model.Screen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import xcom.niteshray.xapps.xblockit.feature.home.presentation.HomeScreenMain
import xcom.niteshray.xapps.xblockit.feature.block.presentation.BlockScreen
import xcom.niteshray.xapps.xblockit.feature.focus.FocusScreen
import xcom.niteshray.xapps.xblockit.feature.tasks.TasksScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController){
    val innerNavControlller = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Focus, Screen.Block, Screen.Tasks)
    var showPrivacyDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 3.dp
            ){
                val navBackStackEntry by innerNavControlller.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                screens.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    
                    // Smooth scale animation for icon
                    val iconScale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "iconScale"
                    )
                    
                    // Color animation for smooth transitions
                    val iconColor by animateColorAsState(
                        targetValue = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        animationSpec = tween(durationMillis = 200),
                        label = "iconColor"
                    )
                    
                    val labelColor by animateColorAsState(
                        targetValue = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        animationSpec = tween(durationMillis = 200),
                        label = "labelColor"
                    )

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                innerNavControlller.navigate(screen.route) {
                                    popUpTo(innerNavControlller.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { 
                            Text(
                                text = screen.title,
                                color = labelColor,
                                fontSize = if (isSelected) 12.sp else 11.sp
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(iconScale),
                                tint = iconColor
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ){ pd->
        Box(
            modifier = Modifier.padding(pd)
        ) {
            NavHost(navController = innerNavControlller , startDestination = "home"){
                composable("home"){
                    HomeScreenMain()
                }
                composable("focus"){
                    FocusScreen()
                }
                composable("block"){
                    BlockScreen()
                }
                composable("tasks"){
                    TasksScreen()
                }
            }

        }
    }
}
