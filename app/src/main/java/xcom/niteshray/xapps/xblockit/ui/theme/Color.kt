package xcom.niteshray.xapps.xblockit.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// 🎨 FLOWBIT - MINIMALISTIC BLACK & WHITE THEME
// Modern, Clean, Dark-First Design with White Accents
// ═══════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────
// ⚫ PRIMARY COLORS - Pure Black & White
// ─────────────────────────────────────────────────────────────

// Pure Black (Background)
val PureBlack = Color(0xFF000000)
val NearBlack = Color(0xFF0A0A0A)
val DarkGray = Color(0xFF121212)
val MediumDarkGray = Color(0xFF1A1A1A)
val CardDark = Color(0xFF1E1E1E)

// Pure White (Accent/Primary)
val PureWhite = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFF5F5F5)
val LightGray = Color(0xFFE0E0E0)
val MediumGray = Color(0xFFB0B0B0)
val DimGray = Color(0xFF808080)
val DarkTextGray = Color(0xFF606060)

// ─────────────────────────────────────────────────────────────
// ✨ SHINE & GLOW EFFECTS
// ─────────────────────────────────────────────────────────────

val ShineWhite = Color(0xFFFFFFFF)
val GlowWhite = Color(0x40FFFFFF)  // 25% white for subtle glow
val GlowWhiteMedium = Color(0x66FFFFFF)  // 40% white
val GlowWhiteStrong = Color(0x99FFFFFF)  // 60% white
val BorderGlow = Color(0x33FFFFFF)  // 20% white for borders

// ─────────────────────────────────────────────────────────────
// 🌙 DARK THEME COLORS (Primary Theme)
// ─────────────────────────────────────────────────────────────

// Primary - White on Black
val DarkPrimary = PureWhite
val DarkPrimaryVariant = OffWhite
val DarkPrimaryLight = Color(0xFF2A2A2A)  // Slightly lighter for containers
val DarkPrimaryContainer = Color(0xFF333333)

// Secondary - Gray tones for subtle accents
val DarkSecondary = LightGray
val DarkSecondaryVariant = MediumGray
val DarkSecondaryLight = Color(0xFF252525)
val DarkSecondaryContainer = Color(0xFF2E2E2E)

// Backgrounds & Surfaces (OLED-Friendly Pure Black)
val DarkBackground = PureBlack
val DarkSurface = MediumDarkGray
val DarkSurfaceVariant = CardDark
val DarkSurfaceElevated = Color(0xFF252525)
val DarkSurfaceDim = DarkGray

// Text Colors
val DarkTextPrimary = PureWhite
val DarkTextSecondary = MediumGray
val DarkTextTertiary = DimGray
val DarkTextOnPrimary = PureBlack  // Black text on white buttons

// Borders & Dividers
val DarkBorder = Color(0xFF333333)
val DarkBorderVariant = Color(0xFF444444)
val DarkDivider = Color(0xFF1F1F1F)

// ─────────────────────────────────────────────────────────────
// ☀️ LIGHT MODE COLORS (Optional fallback)
// ─────────────────────────────────────────────────────────────

val LightPrimary = PureBlack
val LightPrimaryVariant = DarkGray
val LightPrimaryLight = Color(0xFFF0F0F0)
val LightPrimaryContainer = Color(0xFFE8E8E8)
val LightSecondary = DimGray
val LightSecondaryVariant = DarkTextGray
val LightSecondaryLight = Color(0xFFFAFAFA)
val LightSecondaryContainer = Color(0xFFF5F5F5)
val LightBackground = PureWhite
val LightSurface = PureWhite
val LightSurfaceVariant = OffWhite
val LightSurfaceElevated = PureWhite
val LightSurfaceDim = Color(0xFFEEEEEE)
val LightTextPrimary = PureBlack
val LightTextSecondary = DimGray
val LightTextTertiary = MediumGray
val LightTextOnPrimary = PureWhite
val LightBorder = LightGray
val LightBorderVariant = MediumGray
val LightDivider = OffWhite

// ─────────────────────────────────────────────────────────────
// 🎯 SEMANTIC COLORS (Minimal - Only Error for critical)
// ─────────────────────────────────────────────────────────────

// Success - Soft white/gray (not green - minimalistic)
val Success = PureWhite
val SuccessLight = Color(0xFFF5F5F5)
val SuccessDark = Color(0xFF2A2A2A)

// Warning - Slightly warm gray
val Warning = Color(0xFFE0D0C0)  // Warm off-white
val WarningLight = Color(0xFFFAF5F0)
val WarningDark = Color(0xFF3A3530)

// Error - Keep subtle red for critical actions only
val Error = Color(0xFFFF6B6B)  // Softer red
val ErrorLight = Color(0xFFFFE5E5)
val ErrorDark = Color(0xFF3A2020)

// Info - Light gray
val Info = MediumGray
val InfoLight = OffWhite
val InfoDark = CardDark

// ─────────────────────────────────────────────────────────────
// 🎯 STATUS INDICATORS (Grayscale hierarchy)
// ─────────────────────────────────────────────────────────────

val StatusActive = PureWhite  // Brightest = Active
val StatusPaused = MediumGray  // Medium = Paused
val StatusBlocked = Color(0xFFFF6B6B)  // Subtle red = Blocked
val StatusInactive = DimGray  // Dim = Inactive
val StatusCompleted = LightGray  // Light = Completed

// ─────────────────────────────────────────────────────────────
// ✨ GRADIENTS (White to Gray for subtle effects)
// ─────────────────────────────────────────────────────────────

val GradientWhiteStart = PureWhite
val GradientWhiteEnd = LightGray
val GradientDarkStart = Color(0xFF2A2A2A)
val GradientDarkEnd = Color(0xFF1A1A1A)
val GradientMixedStart = PureWhite
val GradientMixedEnd = MediumGray

// Legacy gradient names for compatibility
val GradientBlueStart = PureWhite
val GradientBlueEnd = LightGray
val GradientPurpleStart = MediumGray
val GradientPurpleEnd = LightGray

// ─────────────────────────────────────────────────────────────
// 🔲 OVERLAYS & EFFECTS
// ─────────────────────────────────────────────────────────────

val OverlayLight = Color(0x40000000)  // 25% Black
val OverlayMedium = Color(0x80000000)  // 50% Black
val OverlayDark = Color(0xB3000000)  // 70% Black

// Shimmer (Loading states)
val ShimmerLight = LightGray
val ShimmerDark = CardDark
val ShimmerHighlight = OffWhite

// ─────────────────────────────────────────────────────────────
// 🔧 LEGACY COMPATIBILITY
// ─────────────────────────────────────────────────────────────

val Primary = DarkPrimary
val PrimaryVariant = DarkPrimaryVariant
val PrimaryLight = DarkPrimaryLight
val Secondary = DarkSecondary
val SecondaryVariant = DarkSecondaryVariant
val SecondaryLight = DarkSecondaryLight
val Background = DarkBackground
val BackgroundVariant = DarkSurfaceVariant
val Surface = DarkSurface
val SurfaceVariant = DarkSurfaceVariant
val SurfaceElevated = DarkSurfaceElevated
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextTertiary = DarkTextTertiary
val TextInverse = DarkTextOnPrimary
val Border = DarkBorder
val BorderFocused = DarkPrimary
val Divider = DarkDivider
val Overlay = OverlayMedium
val OverlayLight_Legacy = OverlayLight
val Shimmer = ShimmerDark
val Shadow = Color(0x1A000000)
val Highlight = GlowWhite
val Active = StatusActive
val Inactive = StatusInactive
val Blocked = StatusBlocked
val Paused = StatusPaused
val GradientStart = GradientWhiteStart
val GradientEnd = GradientWhiteEnd
val Blue = PureWhite  // Legacy - now white
val lightblue = LightGray  // Legacy - now light gray
val Black = PureBlack
val PurpleGrey80 = MediumGray
val Pink80 = LightGray
val gray = CardDark
val PurpleGrey40 = DimGray
val Pink40 = DimGray
val SuccessLight_Legacy = SuccessLight
val WarningLight_Legacy = WarningLight
val ErrorLight_Legacy = ErrorLight