package xcom.niteshray.xapps.xblockit.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════
// 🎨 BLOCKIT - PRODUCTIVITY APP COLOR SYSTEM
// Based on UI/UX principles: 60-30-10 rule, accessibility, and visual hierarchy
// ═══════════════════════════════════════════════════════════════

// ═══ PRIMARY BRAND COLORS ═══
val Primary = Color(0xFF4A90E2)              // Calm Blue - Trust, Focus, Productivity
val PrimaryVariant = Color(0xFF357ABD)       // Darker Blue - Hover states
val PrimaryLight = Color(0xFF7DB3F5)         // Light Blue - Backgrounds

// ═══ SECONDARY ACCENT COLORS ═══
val Secondary = Color(0xFF7C4DFF)            // Vibrant Purple - Energy, Creativity
val SecondaryVariant = Color(0xFF5E35B1)     // Deep Purple - Active states
val SecondaryLight = Color(0xFFB085FF)       // Light Purple - Subtle accents

// ═══ SEMANTIC COLORS ═══
val Success = Color(0xFF10B981)              // Emerald Green - Success, Achievement
val SuccessLight = Color(0xFFD1FAE5)         // Light Green - Success backgrounds
val Warning = Color(0xFFF59E0B)              // Amber - Warnings, Caution
val WarningLight = Color(0xFFFEF3C7)         // Light Amber - Warning backgrounds
val Error = Color(0xFFEF4444)                // Soft Red - Errors, Danger
val ErrorLight = Color(0xFFFEE2E2)           // Light Red - Error backgrounds

// ═══ NEUTRAL BACKGROUNDS (60% of design) ═══
val Background = Color(0xFFF8F9FA)           // Very Light Gray - Main background, reduces eye strain
val BackgroundVariant = Color(0xFFF1F3F5)    // Light Gray - Subtle sections

// ═══ SURFACE COLORS (30% of design) ═══
val Surface = Color(0xFFFFFFFF)              // Pure White - Cards, dialogs
val SurfaceVariant = Color(0xFFF5F7FA)       // Off-White - Secondary cards
val SurfaceElevated = Color(0xFFFFFFFF)      // White with shadow - Elevated components

// ═══ TEXT COLORS ═══
val TextPrimary = Color(0xFF1A202C)          // Almost Black - Main text, high contrast
val TextSecondary = Color(0xFF64748B)        // Slate Gray - Secondary text
val TextTertiary = Color(0xFF94A3B8)         // Light Gray - Disabled, placeholder
val TextInverse = Color(0xFFFFFFFF)          // White - Text on dark backgrounds

// ═══ BORDER & DIVIDER COLORS ═══
val Border = Color(0xFFE2E8F0)               // Light Border - Subtle separators
val BorderFocused = Color(0xFF4A90E2)        // Primary - Active input borders
val Divider = Color(0xFFF1F5F9)              // Very Light Gray - Section dividers

// ═══ OVERLAY COLORS ═══
val Overlay = Color(0x80000000)              // 50% Black - Modal overlays
val OverlayLight = Color(0x40000000)         // 25% Black - Light overlays

// ═══ SPECIAL UI ELEMENTS ═══
val Shimmer = Color(0xFFE5E7EB)              // Light Gray - Loading skeletons
val Shadow = Color(0x1A000000)               // 10% Black - Soft shadows
val Highlight = Color(0xFFFAFBFC)            // Almost White - Hover highlights

// ═══ STATUS COLORS ═══
val Active = Color(0xFF10B981)               // Green - Active/Online status
val Inactive = Color(0xFF94A3B8)             // Gray - Inactive status
val Blocked = Color(0xFFEF4444)              // Red - Blocked status
val Paused = Color(0xFFF59E0B)               // Amber - Paused status

// ═══ GRADIENT COLORS ═══
val GradientStart = Color(0xFF4A90E2)        // Primary Blue
val GradientEnd = Color(0xFF7C4DFF)          // Secondary Purple

// ═══ LEGACY COMPATIBILITY (for gradual migration) ═══
val Blue = Color(0xFF4A90E2)
val lightblue = Color(0xFF4A90E2)
val Black = Color(0xFF000000)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val gray = Color(0xFF2C2C2D)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)