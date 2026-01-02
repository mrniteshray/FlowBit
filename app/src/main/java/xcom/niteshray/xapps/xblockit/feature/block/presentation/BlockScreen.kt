package xcom.niteshray.xapps.xblockit.feature.block.presentation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import xcom.niteshray.xapps.xblockit.R
import xcom.niteshray.xapps.xblockit.model.Appitem
import xcom.niteshray.xapps.xblockit.feature.block.viewmodels.AppViewModel
import xcom.niteshray.xapps.xblockit.feature.block.components.AccessibilityPermissionBottomSheet
import xcom.niteshray.xapps.xblockit.feature.block.components.BatteryOptimizationPermissionBottomSheet
import xcom.niteshray.xapps.xblockit.feature.block.components.NotificationPermissionBottomSheet
import xcom.niteshray.xapps.xblockit.feature.block.viewmodels.WebViewModel
import xcom.niteshray.xapps.xblockit.util.BlockAccessibility
import xcom.niteshray.xapps.xblockit.util.BlockSharedPref
import xcom.niteshray.xapps.xblockit.util.CheckPermissions.isAccessibilityServiceEnabled
import xcom.niteshray.xapps.xblockit.util.CheckPermissions.isIgnoringBatteryOptimizations
import xcom.niteshray.xapps.xblockit.util.CheckPermissions.isNotificationPermissionGranted
import xcom.niteshray.xapps.xblockit.util.PauseTimeService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockScreen(
    appViewModel: AppViewModel = viewModel(),
    webViewModel: WebViewModel = viewModel()
) {
    val context = LocalContext.current
    val blockSharedPref = BlockSharedPref(context)

    // Permission states
    var showAccessibilityPermissionSheet by remember { mutableStateOf(false) }
    var showBatteryPermissionSheet by remember { mutableStateOf(false) }
    var showNotificationPermissionSheet by remember { mutableStateOf(false) }
    
    // Bottom sheet state
    var showAddAppsBottomSheet by remember { mutableStateOf(false) }

    // Block state
    var isBlock by remember { mutableStateOf(blockSharedPref.getBlock()) }

    // Search state
    var searchQuery by remember { mutableStateOf("") }

    val apps = appViewModel.app
    val weblist = webViewModel.web
    val context2 = LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header - "Blocks" with Help button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Blocks",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // App Limits Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Block Apps",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(
                        onClick = { showAddAppsBottomSheet = true },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add App Limit",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Apps List
            if (apps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            } else {
                val filteredApps = apps.filter { app ->
                    app.name.contains(searchQuery, ignoreCase = true) && app.isBlock
                }

                items(filteredApps) { app ->
                    ModernBlockedAppItem(
                        app = app,
                        onToggleChange = { isBlocked ->
                            appViewModel.updateBlock(app.packageName, isBlocked)
                            if (isBlocked) {
                                Toast.makeText(context2, "${app.name} Blocked", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Block Shorts Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Block Shorts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // Shorts toggle control
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Shorts Blocking",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Block all short-form content",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Switch(
                            checked = isBlock,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    if (isAccessibilityServiceEnabled(context, BlockAccessibility::class.java)) {
                                        blockSharedPref.setBlock(true)
                                        blockSharedPref.setPauseEndTime(0L)
                                        val intent = Intent(context, PauseTimeService::class.java)
                                        context.stopService(intent)
                                        isBlock = true
                                        Toast.makeText(context, "Shorts Blocking Enabled", Toast.LENGTH_SHORT).show()
                                    } else {
                                        showAccessibilityPermissionSheet = true
                                    }
                                } else {
                                    blockSharedPref.setBlock(false)
                                    isBlock = false
                                    Toast.makeText(context, "Shorts Blocking Disabled", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                }
            }

            // Short-form platforms list
            item {
                val shortPlatforms = listOf(
                    ShortPlatform("YouTube Shorts", R.drawable.shorts, false),
                    ShortPlatform("IG Reels", R.drawable.reel, false),
                    ShortPlatform("Snapchat Spotlight", R.drawable.snapchat, false),
                    ShortPlatform("Facebook Reels", R.drawable.facebook, false)
                )

                Column {
                    shortPlatforms.forEach { platform ->
                        ShortPlatformItem(platform)
                    }
                }
            }

            // Other Blocks Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Other blocks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // Block Websites
            item {
                OtherBlockItem(
                    icon = Icons.Default.Home,
                    title = "Block Websites",
                    isAdded = weblist.isNotEmpty(),
                    onClick = { /* Navigate to websites screen */ }
                )
            }

            // Block Notifications
            item {
                OtherBlockItem(
                    icon = Icons.Default.Notifications,
                    title = "Block Notifications",
                    isAdded = false,
                    onClick = { /* Navigate to notifications screen */ }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Permission Bottom Sheets (keep as is)
    if (showAccessibilityPermissionSheet) {
        AccessibilityPermissionBottomSheet(
            onAllow = {
                showAccessibilityPermissionSheet = false
                context.startActivity(
                    Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
                if (!isIgnoringBatteryOptimizations(context)) {
                    showBatteryPermissionSheet = true
                } else if (!isNotificationPermissionGranted(context)) {
                    showNotificationPermissionSheet = true
                }
            },
            onDeny = {
                showAccessibilityPermissionSheet = false
                Toast.makeText(
                    context,
                    "You can enable accessibility later from settings",
                    Toast.LENGTH_SHORT
                ).show()
                if (!isIgnoringBatteryOptimizations(context)) {
                    showBatteryPermissionSheet = true
                } else if (!isNotificationPermissionGranted(context)) {
                    showNotificationPermissionSheet = true
                }
            },
            onPrivacyPolicyClick = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://mrniteshray.github.io/Privacy-Policy/BLOCKIT")
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        )
    }

    if (showBatteryPermissionSheet) {
        BatteryOptimizationPermissionBottomSheet(
            onAllow = {
                showBatteryPermissionSheet = false
                if (!isNotificationPermissionGranted(context)) {
                    showNotificationPermissionSheet = true
                }
            },
            onDeny = {
                showBatteryPermissionSheet = false
                if (!isNotificationPermissionGranted(context)) {
                    showNotificationPermissionSheet = true
                }
            }
        )
    }

    if (showNotificationPermissionSheet) {
        NotificationPermissionBottomSheet(
            onAllow = {
                showNotificationPermissionSheet = false
            },
            onDeny = {
                showNotificationPermissionSheet = false
            }
        )
    }
    
    // Add Apps Bottom Sheet
    if (showAddAppsBottomSheet) {
        AddAppsBottomSheet(
            apps = apps,
            onDismiss = { showAddAppsBottomSheet = false },
            onAppClick = { app ->
                appViewModel.updateBlock(app.packageName, true)
                Toast.makeText(context, "${app.name} Blocked", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

// Data class for short platforms
data class ShortPlatform(
    val name: String,
    val iconRes: Int,
    var isBlocked: Boolean
)

// Modern Blocked App Item (like Instagram card in image)
@Composable
fun ModernBlockedAppItem(
    app: Appitem,
    onToggleChange: (Boolean) -> Unit
) {
    val painter = remember(app.Icon) {
        BitmapPainter(app.Icon.toBitmap().asImageBitmap())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { /* Navigate to app details */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painter,
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "2h 12m spent / 2h",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                IconButton(
                    onClick = { onToggleChange(false) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Blocking status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Blocking",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ShortPlatformItem(platform: ShortPlatform) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { /* Toggle block */ }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = platform.iconRes),
                contentDescription = platform.name,
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = platform.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun OtherBlockItem(
    icon: ImageVector,
    title: String,
    isAdded: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAdded) "Manage" else "Add block",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// Add Apps Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppsBottomSheet(
    apps: List<Appitem>,
    onDismiss: () -> Unit,
    onAppClick: (Appitem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Select Apps to Block",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search apps...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )

            val filteredApps = apps.filter { app ->
                !app.isBlock && app.name.contains(searchQuery, ignoreCase = true)
            }

            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                items(filteredApps) { app ->
                    AppItemInBottomSheet(app) {
                        onAppClick(app)
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
fun AppItemInBottomSheet(app: Appitem, onAddClick: () -> Unit) {
    val painter = remember(app.Icon) {
        BitmapPainter(app.Icon.toBitmap().asImageBitmap())
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = app.name,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = app.name,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
            maxLines = 1
        )

        IconButton(
            onClick = onAddClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
