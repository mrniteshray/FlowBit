package xcom.niteshray.xapps.xblockit.feature.block.presentation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import xcom.niteshray.xapps.xblockit.ui.theme.*
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
    
    // Bottom sheet states
    var showAddAppsBottomSheet by remember { mutableStateOf(false) }
    var showAddWebsiteBottomSheet by remember { mutableStateOf(false) }

    // Block state
    var isBlock by remember { mutableStateOf(blockSharedPref.getBlock()) }

    val apps = appViewModel.app
    val weblist = webViewModel.web

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            item {
                Text(
                    text = "Block What Holds You Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                )
            }

            // Block Apps Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
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
                            text = "Add Apps",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Apps List or Empty State
            val blockedApps = apps.filter { it.isBlock }
            
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
            } else if (blockedApps.isEmpty()) {
                item {
                    EmptyStateCard(
                        message = "No apps blocked yet",
                        subMessage = "Tap 'Add Apps' to block distracting apps"
                    )
                }
            } else {
                items(blockedApps) { app ->
                    ModernBlockedAppItem(
                        app = app,
                        onToggleChange = { isBlocked ->
                            appViewModel.updateBlock(app.packageName, isBlocked)
                            if (isBlocked) {
                                Toast.makeText(context, "${app.name} Blocked", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Block Shorts Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Block Shorts",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }

            // Shorts Card with logos only
            item {
                ShortsBlockCard(
                    isEnabled = isBlock,
                    onToggle = { enabled ->
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
                    }
                )
            }

            // Block Websites Section
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Block Websites",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(
                        onClick = { showAddWebsiteBottomSheet = true },
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
                            text = "Add Website",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Websites List or Empty State
            if (weblist.isEmpty()) {
                item {
                    EmptyStateCard(
                        message = "No websites blocked",
                        subMessage = "Tap 'Add Website' to block distracting sites"
                    )
                }
            } else {
                items(weblist) { website ->
                    WebsiteBlockItem(
                        url = website,
                        onRemove = { webViewModel.deleteWebsite(website) }
                    )
                }
            }

            // Privacy Policy Link
            item {
                Spacer(modifier = Modifier.height(32.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://mrniteshray.github.io/FlowBit/privacy.html")
                            )
                            context.startActivity(intent)
                        }
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Privacy Policy",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    // Permission Bottom Sheets
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
                    Uri.parse("https://mrniteshray.github.io/FlowBit/privacy.html")
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
            onAllow = { showNotificationPermissionSheet = false },
            onDeny = { showNotificationPermissionSheet = false }
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
    
    // Add Website Bottom Sheet
    if (showAddWebsiteBottomSheet) {
        AddWebsiteBottomSheet(
            onDismiss = { showAddWebsiteBottomSheet = false },
            onAddWebsite = { url ->
                webViewModel.addWebsite(url)
                Toast.makeText(context, "Website blocked", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Empty state card for when no items are blocked
 */
@Composable
private fun EmptyStateCard(
    message: String,
    subMessage: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, BorderGlow, RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = message,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subMessage,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Single card with horizontal shorts logos
 */
@Composable
private fun ShortsBlockCard(
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val shortPlatforms = listOf(
        R.drawable.shorts,
        R.drawable.reel,
        R.drawable.snapchat,
        R.drawable.facebook
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Platform logos in horizontal row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(shortPlatforms) { iconRes ->
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Toggle
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
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

/**
 * Website block item
 */
@Composable
private fun WebsiteBlockItem(
    url: String,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = url,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1
            )
            
            IconButton(
                onClick = onRemove,
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
    }
}

// Modern Blocked App Item
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
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = app.name,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = "Blocking",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.Medium
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

// Add Website Bottom Sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWebsiteBottomSheet(
    onDismiss: () -> Unit,
    onAddWebsite: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var websiteUrl by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 20.dp)
        ) {
            Text(
                text = "Block Website",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = websiteUrl,
                onValueChange = { websiteUrl = it },
                placeholder = {
                    Text(
                        text = "Enter website URL (e.g., facebook.com)",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = {
                    if (websiteUrl.isNotBlank()) {
                        onAddWebsite(websiteUrl.trim())
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = websiteUrl.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Block Website",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
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
            fontSize = 15.sp,
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
