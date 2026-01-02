# ═══════════════════════════════════════════════════════════════════════════
# 🚀 BLOCKIT - PRODUCTION PROGUARD RULES
# Optimized for productivity app with Jetpack Compose
# ═══════════════════════════════════════════════════════════════════════════

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# ═══ DEBUGGING & LINE NUMBERS ═══
# Preserve line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ═══ JETPACK COMPOSE ═══
# Keep Compose runtime classes
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.foundation.** { *; }

# Keep Composable functions
-keep @androidx.compose.runtime.Composable class ** { *; }

# ═══ KOTLIN ═══
# Keep Kotlin metadata for reflection
-keepattributes *Annotation*, Signature, Exception
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ═══ DATA CLASSES & SERIALIZATION ═══
# Keep data classes used in your app
-keep class xcom.niteshray.xapps.xblockit.model.** { *; }

# ═══ ANDROID COMPONENTS ═══
# Keep Android lifecycle components
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep Services and Accessibility Services
-keep class * extends android.app.Service
-keep class * extends android.accessibilityservice.AccessibilityService
-keep class xcom.niteshray.xapps.xblockit.util.BlockAccessibility { *; }
-keep class xcom.niteshray.xapps.xblockit.util.PauseTimeService { *; }

# ═══ NAVIGATION ═══
# Keep Navigation components
-keep class androidx.navigation.** { *; }

# ═══ FIREBASE (if using) ═══
# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ═══ LOTTIE ANIMATIONS ═══
# Keep Lottie animation classes
-keep class com.airbnb.lottie.** { *; }

# ═══ MEDIA3 / EXOPLAYER ═══
# Keep Media3/ExoPlayer classes
-keep class androidx.media3.** { *; }

# ═══ ENUMS ═══
# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ═══ PARCELABLE ═══
# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# ═══ REMOVE LOGGING IN RELEASE ═══
# Remove Log calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# ═══ OPTIMIZATION ═══
# Allow aggressive optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# ═══ WARNINGS ═══
# Suppress warnings
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**