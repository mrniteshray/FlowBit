package xcom.niteshray.xapps.xblockit.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// 🎨 BLOCKIT - PRODUCTION PRODUCTIVITY APP COLOR SYSTEM
// Material 3 Design + Productivity Best Practices
// WCAG AA Compliant | 60-30-10 Rule | Psychological Color Theory
// ═══════════════════════════════════════════════════════════════

// ─────────────────────────────────────────────────────────────
// 🌞 LIGHT MODE COLORS - Calm, Professional, Focus-Oriented
// ─────────────────────────────────────────────────────────────

// PRIMARY - Serene Blue (Trust, Stability, Concentration)
val LightPrimary = Color(0xFF0066CC)              // Deep Blue - CTA buttons, links
val LightPrimaryVariant = Color(0xFF004C99)       // Darker Blue - Pressed states
val LightPrimaryLight = Color(0xFFE6F2FF)         // Light Blue - Backgrounds, highlights
val LightPrimaryContainer = Color(0xFFCCE5FF)     // Blue Container - Chips, tags

// SECONDARY - Energizing Purple (Creativity, Innovation)
val LightSecondary = Color(0xFF7C3AED)            // Vibrant Purple - Accents
val LightSecondaryVariant = Color(0xFF5B21B6)     // Deep Purple - Active states
val LightSecondaryLight = Color(0xFFF3E8FF)       // Light Purple - Subtle highlights
val LightSecondaryContainer = Color(0xFFE9D5FF)   // Purple Container

// BACKGROUNDS & SURFACES (60% of design)
val LightBackground = Color(0xFFFAFAFA)           // Soft Gray - Main background
val LightSurface = Color(0xFFFFFFFF)              // Pure White - Cards, sheets
val LightSurfaceVariant = Color(0xFFF5F5F5)       // Light Gray - Secondary cards
val LightSurfaceElevated = Color(0xFFFFFFFF)      // Elevated surfaces
val LightSurfaceDim = Color(0xFFEEEEEE)           // Dimmed surface

// TEXT (Optimal readability ratios)
val LightTextPrimary = Color(0xFF1A1A1A)          // Near Black - Body text (AAA)
val LightTextSecondary = Color(0xFF616161)        // Medium Gray - Secondary text (AA)
val LightTextTertiary = Color(0xFF9E9E9E)         // Light Gray - Disabled text
val LightTextOnPrimary = Color(0xFFFFFFFF)        // White on primary

// BORDERS & DIVIDERS
val LightBorder = Color(0xFFE0E0E0)               // Subtle borders
val LightBorderVariant = Color(0xFFBDBDBD)        // Emphasized borders
val LightDivider = Color(0xFFF0F0F0)              // Section dividers

// ─────────────────────────────────────────────────────────────
// 🌙 DARK MODE COLORS - OLED-Friendly, Eye Comfort, Premium
// ─────────────────────────────────────────────────────────────

// PRIMARY - Brighter Blue (Enhanced visibility in dark)
val DarkPrimary = Color(0xFF3D9CFF)               // Bright Blue - Stands out
val DarkPrimaryVariant = Color(0xFF1E7FCC)        // Medium Blue
val DarkPrimaryLight = Color(0xFF1A2332)          // Dark Blue - Backgrounds
val DarkPrimaryContainer = Color(0xFF1E3A5F)      // Blue Container

// SECONDARY - Vibrant Purple (Pops in dark mode)
val DarkSecondary = Color(0xFF9F7AEA)             // Light Purple - Accents
val DarkSecondaryVariant = Color(0xFF7C3AED)      // Medium Purple
val DarkSecondaryLight = Color(0xFF1F1733)        // Dark Purple - Backgrounds
val DarkSecondaryContainer = Color(0xFF2E1F47)    // Purple Container

// BACKGROUNDS & SURFACES (True Dark, OLED-optimized)
val DarkBackground = Color(0xFF0A0A0A)            // Pure Black - Battery efficient
val DarkSurface = Color(0xFF1A1A1A)               // Near Black - Cards
val DarkSurfaceVariant = Color(0xFF242424)        // Dark Gray - Elevated cards
val DarkSurfaceElevated = Color(0xFF2E2E2E)       // Highest elevation
val DarkSurfaceDim = Color(0xFF141414)            // Dimmed surface

// TEXT (Enhanced contrast for dark mode)
val DarkTextPrimary = Color(0xFFE8E8E8)           // Light Gray - Body text
val DarkTextSecondary = Color(0xFFB0B0B0)         // Medium Gray - Secondary
val DarkTextTertiary = Color(0xFF757575)          // Dark Gray - Disabled
val DarkTextOnPrimary = Color(0xFF000000)         // Black on bright primary

// BORDERS & DIVIDERS
val DarkBorder = Color(0xFF2E2E2E)                // Subtle borders
val DarkBorderVariant = Color(0xFF424242)         // Emphasized borders
val DarkDivider = Color(0xFF1F1F1F)               // Section dividers

// ─────────────────────────────────────────────────────────────
// 🎨 SEMANTIC COLORS (Same for both modes, accessible)
// ─────────────────────────────────────────────────────────────

// SUCCESS
val Success = Color(0xFF10B981)                   // Emerald - Achievement
val SuccessLight = Color(0xFFD1FAE5)              // Light Green bg (light mode)
val SuccessDark = Color(0xFF064E3B)               // Dark Green bg (dark mode)

// WARNING
val Warning = Color(0xFFFB923C)                   // Orange - Caution
val WarningLight = Color(0xFFFED7AA)              // Light Orange bg
val WarningDark = Color(0xFF7C2D12)               // Dark Orange bg

// ERROR
val Error = Color(0xFFEF4444)                     // Red - Danger
val ErrorLight = Color(0xFFFEE2E2)                // Light Red bg
val ErrorDark = Color(0xFF7F1D1D)                 // Dark Red bg

// INFO
val Info = Color(0xFF3B82F6)                      // Blue - Information
val InfoLight = Color(0xFFDBEAFE)                 // Light Blue bg
val InfoDark = Color(0xFF1E3A8A)                  // Dark Blue bg

// ─────────────────────────────────────────────────────────────
// 🎯 STATUS INDICATORS (Productivity specific)
// ─────────────────────────────────────────────────────────────

val StatusActive = Color(0xFF10B981)              // Green - Active/Running
val StatusPaused = Color(0xFFFB923C)              // Orange - Paused
val StatusBlocked = Color(0xFFEF4444)             // Red - Blocked
val StatusInactive = Color(0xFF6B7280)            // Gray - Inactive
val StatusCompleted = Color(0xFF8B5CF6)           // Purple - Completed

// ─────────────────────────────────────────────────────────────
// ✨ SPECIAL EFFECTS
// ─────────────────────────────────────────────────────────────

// GRADIENTS (Modern, eye-catching)
val GradientBlueStart = Color(0xFF0066CC)
val GradientBlueEnd = Color(0xFF3D9CFF)
val GradientPurpleStart = Color(0xFF7C3AED)
val GradientPurpleEnd = Color(0xFF9F7AEA)
val GradientMixedStart = Color(0xFF0066CC)
val GradientMixedEnd = Color(0xFF7C3AED)

// OVERLAYS
val OverlayLight = Color(0x40000000)              // 25% Black
val OverlayMedium = Color(0x80000000)             // 50% Black
val OverlayDark = Color(0xB3000000)               // 70% Black

// SHIMMER (Loading states)
val ShimmerLight = Color(0xFFE0E0E0)
val ShimmerDark = Color(0xFF2E2E2E)
val ShimmerHighlight = Color(0xFFF5F5F5)

// ─────────────────────────────────────────────────────────────
// 🔧 LEGACY COMPATIBILITY (For gradual migration)
// ─────────────────────────────────────────────────────────────

// Kept for backward compatibility
val Primary = LightPrimary
val PrimaryVariant = LightPrimaryVariant
val PrimaryLight = LightPrimaryLight
val Secondary = LightSecondary
val SecondaryVariant = LightSecondaryVariant
val SecondaryLight = LightSecondaryLight
val Background = LightBackground
val BackgroundVariant = LightSurfaceVariant
val Surface = LightSurface
val SurfaceVariant = LightSurfaceVariant
val SurfaceElevated = LightSurfaceElevated
val TextPrimary = LightTextPrimary
val TextSecondary = LightTextSecondary
val TextTertiary = LightTextTertiary
val TextInverse = LightTextOnPrimary
val Border = LightBorder
val BorderFocused = LightPrimary
val Divider = LightDivider
val Overlay = OverlayMedium
val OverlayLight_Legacy = OverlayLight
val Shimmer = ShimmerLight
val Shadow = Color(0x1A000000)
val Highlight = Color(0xFFFAFBFC)
val Active = StatusActive
val Inactive = StatusInactive
val Blocked = StatusBlocked
val Paused = StatusPaused
val GradientStart = GradientBlueStart
val GradientEnd = GradientBlueEnd
val Blue = LightPrimary
val lightblue = LightPrimary
val Black = Color(0xFF000000)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val gray = Color(0xFF2C2C2D)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val SuccessLight_Legacy = SuccessLight
val WarningLight_Legacy = WarningLight
val ErrorLight_Legacy = ErrorLight