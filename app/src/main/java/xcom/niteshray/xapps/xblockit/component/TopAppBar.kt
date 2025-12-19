package xcom.niteshray.xapps.xblockit.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppTopAppBar(){
//    var showPrivacyDialog by remember { mutableStateOf(false) }
//    TopAppBar(
//        title = { },
//        actions = {
//            IconButton(onClick = { showPrivacyDialog = true }) {
//                Icon(
//                    imageVector = Icons.Default.Info,
//                    contentDescription = "Privacy Policy",
//                    tint = AppPrimaryText
//                )
//            }
//        },
//        colors = TopAppBarDefaults.topAppBarColors(containerColor = AppCardWhite)
//    )
//
//    if (showPrivacyDialog) {
//        AlertDialog(
//            onDismissRequest = { showPrivacyDialog = false },
//            confirmButton = {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp),
//                    verticalArrangement = Arrangement.spacedBy(12.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Button(
//                        onClick = {
////                            showPrivacyDialog = false
////                            val uri = "https://buymeacoffee.com/im_nitesh"
////                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
////                            LocalContext.current.startActivity(intent)
//                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = Color(0xFFFFD700),
//                            contentColor = AppPrimaryText
//                        ),
//                        shape = RoundedCornerShape(12.dp),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(48.dp)
//                    ) {
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Favorite,
//                                contentDescription = null,
//                                tint = AppPrimaryText
//                            )
//                            Text(
//                                text = "Support Me",
//                                fontWeight = FontWeight.SemiBold
//                            )
//                        }
//                    }
//                    Button(
//                        onClick = {
////                            showPrivacyDialog = false
////                            val uri =
////                                "https://mrniteshray.github.io/Privacy-Policy/BLOCKIT"
////                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
////                            context.startActivity(intent)
//                        },
//                        colors = ButtonDefaults.buttonColors(
//                            containerColor = AppButtonSecondary,
//                            contentColor = AppCardWhite
//                        ),
//                        shape = RoundedCornerShape(12.dp),
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(48.dp)
//                    ) {
//                        Text(
//                            text = "Privacy Policy",
//                            fontWeight = FontWeight.SemiBold
//                        )
//                    }
//                }
//            },
//            dismissButton = {},
//            title = {
//                Text(
//                    text = "Support & Privacy",
//                    color = AppPrimaryText,
//                    fontWeight = FontWeight.Bold,
//                )
//            },
//            text = {
//                Text(
//                    text = "Support the app or view our Privacy Policy.",
//                    color = AppSecondaryText,
//                    textAlign = TextAlign.Center
//                )
//            },
//            containerColor = AppCardWhite,
//            titleContentColor = AppPrimaryText,
//            textContentColor = AppSecondaryText
//        )
//    }
//}