package xcom.niteshray.xapps.sliqswipe.subscription


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ProButtonNew(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderWidth = 2.dp
    val cornerRadius = 16.dp

    val infiniteTransition = rememberInfiniteTransition(label = "button")

    var showDiscount by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            showDiscount = !showDiscount
        }
    }

    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "border"
    )

    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val borderColor = Color.Black

    Box(
        modifier = modifier
            .padding(4.dp)
            .size(width = 70.dp, height = 40.dp)
            .graphicsLayer {
                compositingStrategy = CompositingStrategy.Offscreen
            }
            .drawWithContent {
                drawContent()

                // Rotating border with sweep gradient
                rotate(degrees = animatedProgress) {
                    drawRoundRect(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                borderColor,
                                Color.Transparent,
                                Color.Transparent,
                                borderColor,
                                Color.Transparent,
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2, size.height / 2)
                        ),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
                        style = Stroke(width = borderWidth.toPx())
                    )
                }

                // Shimmer effect
                val shimmerBrush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerX - 100f, 0f),
                    end = Offset(shimmerX, size.height)
                )

                drawRect(
                    brush = shimmerBrush,
                    size = size,
                    blendMode = BlendMode.Plus
                )
            }
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color(0xFFFFFFFF))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showDiscount,
                transitionSpec = {
                    slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = tween(durationMillis = 500)
                    ) + fadeIn(
                        animationSpec = tween(durationMillis = 500)
                    ) togetherWith slideOutVertically(
                        targetOffsetY = { fullHeight -> -fullHeight },
                        animationSpec = tween(durationMillis = 500)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 500)
                    )
                },
                label = "text"
            ) { isDiscount ->
                Text(
                    text = if (isDiscount) "60%\nOFF" else "PRO",
                    color = Color.Black,
                    fontSize = if (isDiscount) 12.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = if (isDiscount) 14.sp else TextUnit.Unspecified,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}