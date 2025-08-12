package xcom.niteshray.xapps.xblockit.ui.Screens.Home

import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import xcom.niteshray.xapps.xblockit.R
import com.airbnb.lottie.compose.*
import kotlinx.coroutines.delay
import xcom.niteshray.xapps.xblockit.util.BlockAccessibility
import xcom.niteshray.xapps.xblockit.util.BlockSharedPref
import xcom.niteshray.xapps.xblockit.util.CheckPermissions.isAccessibilityServiceEnabled
import xcom.niteshray.xapps.xblockit.util.CheckPermissions.isIgnoringBatteryOptimizations
import xcom.niteshray.xapps.xblockit.util.NotificationHelper
import xcom.niteshray.xapps.xblockit.util.PauseTimeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val blockSharedPref = BlockSharedPref(context)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )


    var showAccessibilityPermissionSheet by remember { mutableStateOf(false) }
    var showBatteryPermissionSheet by remember { mutableStateOf(false) }

    Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Blockit",
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Stay Focused To Your Goals",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            var isBlock by remember { mutableStateOf(blockSharedPref.getBlock()) }
            BlockitControlButtons(
                isBlock = isBlock,
                onActiveClick = {
                    if (isAccessibilityServiceEnabled(context, BlockAccessibility::class.java)) {
                        blockSharedPref.setBlock(true)
                        blockSharedPref.setPauseEndTime(0L)
                        val intent = Intent(context, PauseTimeService::class.java)
                        context.stopService(intent)
                        isBlock = true
                    } else {
                        showAccessibilityPermissionSheet = true
                    }
                    if (!isIgnoringBatteryOptimizations(context) && !showAccessibilityPermissionSheet){
                        showBatteryPermissionSheet = true
                    }
                },
                onPauseClick = { minutes ->
                    val intent = Intent(context, PauseTimeService::class.java)
                    intent.putExtra("pause_time", minutes * 60 * 1000L)
                    context.startService(intent)
                    blockSharedPref.setBlock(false)
                    isBlock = false
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            SupportedApps()
        }

        // Permission Bottom Sheet (demo)
        if (showAccessibilityPermissionSheet) {
            AccessibilityPermissionBottomSheet(
                onAllow = {
                    showAccessibilityPermissionSheet = false
                    context.startActivity(
                        Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                    )
                },
                onDeny = {
                    showAccessibilityPermissionSheet = false
                    Toast.makeText(context, "You can enable accessibility later from settings", Toast.LENGTH_SHORT).show()
                },
                onPrivacyPolicyClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://mrniteshray.github.io/Blockit/PRIVACY_POLICY")
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            )
        }

    if (showBatteryPermissionSheet){
        BatteryOptimizationPermissionBottomSheet(
            onAllow = {
                showBatteryPermissionSheet = false
            },
            onDeny = {
                showBatteryPermissionSheet = false
            }
        )
    }
}




