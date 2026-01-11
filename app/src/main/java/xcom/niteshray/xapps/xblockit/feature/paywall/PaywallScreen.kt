package xcom.niteshray.xapps.xblockit.feature.paywall

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import xcom.niteshray.xapps.xblockit.data.billing.BillingRepository

@Composable
fun PaywallDialog(
    onDismiss: () -> Unit
) {
    PaywallContent(onDismiss = onDismiss)
}

@Composable
fun PaywallContent(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var selectedPackage by remember { mutableStateOf<Package?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        BillingRepository.getOfferings(
            onSuccess = { offerings ->
                val current = offerings.current
                if (current != null) {
                    packages = current.availablePackages
                    // Auto-select Annual if available, else first
                    selectedPackage = packages.find { it.packageType == PackageType.ANNUAL } ?: packages.firstOrNull()
                }
                isLoading = false
            },
            onError = { msg ->
                error = msg
                isLoading = false
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Header Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Unlock FlowBit Pro",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Supercharge your focus",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Benefits
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BenefitItem("Unlock All Focus Sounds")
                BenefitItem("Unlimited App Blocking")
                BenefitItem("Disable Battery Restrictions")
            }

            Spacer(modifier = Modifier.weight(1f))

            // Packages
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (packages.isNotEmpty()) {
                // List of Packages
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    packages.forEach { pkg ->
                        val isSelected = selectedPackage == pkg
                        PackageCard(
                            rcPackage = pkg,
                            isSelected = isSelected,
                            onClick = { selectedPackage = pkg }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // CTA Button
                Button(
                    onClick = {
                        if (activity != null && selectedPackage != null) {
                            BillingRepository.purchase(
                                activity, 
                                selectedPackage!!,
                                onSuccess = { 
                                    Toast.makeText(context, "Welcome to Pro!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                onError = { msg, userCancelled ->
                                    if (!userCancelled) {
                                        Toast.makeText(context, "Error: $msg", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        } else if (activity == null) {
                             Toast.makeText(context, "Error: Cannot find Activity context", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Please select a plan", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Continue",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "No packages found.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Restore Purchase
            TextButton(
                onClick = {
                    BillingRepository.restorePurchases(
                         onSuccess = {
                             Toast.makeText(context, "Purchases Restored!", Toast.LENGTH_SHORT).show()
                             onDismiss()
                         },
                         onError = {
                             Toast.makeText(context, "Restore failed: $it", Toast.LENGTH_SHORT).show()
                         }
                    )
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Restore Purchases",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PackageCard(
    rcPackage: Package,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val product = rcPackage.product
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp, 
                color = borderColor, 
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = when(rcPackage.packageType) {
                        PackageType.LIFETIME -> "Lifetime Access"
                        PackageType.ANNUAL -> "Annual Plan"
                        PackageType.MONTHLY -> "Monthly Plan"
                        else -> "Standard Plan"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.price.formatted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                 Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun BenefitItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper to find Activity from Context
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
