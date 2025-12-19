package xcom.niteshray.xapps.xblockit.feature.home.presentation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun HomeScreenMain() {
    var checkedTasks by remember { mutableStateOf(setOf<Int>()) }

    val quotes = listOf(
        "You are in danger of living a life so comfortable, you'll die without realizing your true potential. — David Goggins",
        "You are stopping you. You are giving up instead of getting hard. — David Goggins",
        "Everyone fails sometimes. Champions keep going when they don't feel like it. — Jocko Willink",
        "Stop telling yourself you're doing enough. You're not. Do MORE. — Andrew Tate",
        "Don't stop when you're tired. Stop when you're DONE. — David Goggins",
        "Suffer now and live the rest of your life as a champion. — Muhammad Ali",
        "Your desire to change must be GREATER than your desire to stay the same. — Anonymous",
        "Depressed? You're not doing anything with yourself. What did you expect? Start NOW. — David Goggins",
        "Life is a tug of war between mediocrity and finding your best self. Which side are you on? — David Goggins",
        "Push yourself because no one else is going to do it for you. — Anonymous"
    )


    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val next = (listState.firstVisibleItemIndex + 1) % quotes.size
            listState.animateScrollToItem(next)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 🖤 TOP SECTION - Header + Quotes
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(horizontal = 20.dp)
                    .padding(top = 40.dp, bottom = 24.dp)
            ) {
                // Header with Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Stay Hard",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = "Build discipline daily",
                            fontSize = 14.sp,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 🔥 Motivational Quotes Carousel
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 20.dp)
                ) {
                    items(quotes.size) { index ->
                        MotivationCard(quotes[index])
                    }
                }
            }

            // ⚪ MAIN CONTENT SECTION
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Non-Negotiables",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground
                    )

                    // Progress indicator
                    Text(
                        text = "${checkedTasks.size}/5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                val tasks = listOf(
                    "20-minute workout or 10k steps",
                    "1 hour focused work (no phone)",
                    "Read 10 pages of a book",
                    "Drink 3 liters of water",
                    "Sleep by 11 PM (8 hours)"
                )


                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(tasks) { index, task ->
                        ModernChecklistItem(
                            task = task,
                            isChecked = checkedTasks.contains(index),
                            onCheckedChange = { isChecked ->
                                checkedTasks = if (isChecked) {
                                    checkedTasks + index
                                } else {
                                    checkedTasks - index
                                }
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun MotivationCard(text: String) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            androidx.compose.material3.MaterialTheme.colorScheme.primary,
            androidx.compose.material3.MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = Modifier
            .width(300.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Close,  // Replace with suitable quote icon
                contentDescription = null,
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = text,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun ModernChecklistItem(
    task: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isChecked) 0.5f else 1f,
        animationSpec = tween(300)
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (isChecked) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clip(RoundedCornerShape(16.dp))
            .background(androidx.compose.material3.MaterialTheme.colorScheme.surface)
            .border(
                width = 1.5.dp,
                color = if (isChecked) androidx.compose.material3.MaterialTheme.colorScheme.tertiary else androidx.compose.material3.MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Custom Checkbox
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) androidx.compose.material3.MaterialTheme.colorScheme.tertiary else Color.Transparent)
                .border(
                    width = 2.dp,
                    color = if (isChecked) androidx.compose.material3.MaterialTheme.colorScheme.tertiary else androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(6.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = androidx.compose.material3.MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = task,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface.copy(alpha = animatedAlpha),
            textDecoration = if (isChecked) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f)
        )
    }
}

