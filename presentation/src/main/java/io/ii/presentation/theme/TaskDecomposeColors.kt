package io.ii.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

internal val FocusBlue = Color(0xFF1967D2)
internal val InsightTeal = Color(0xFF00796B)
internal val WarmSignal = Color(0xFFB45309)
internal val TaskInk = Color(0xFF111827)
internal val CalmPaper = Color(0xFFEFF4FA)
internal val CleanSurface = Color(0xFFFFFFFF)
internal val MistSurface = Color(0xFFE8EEF7)
internal val SoftLine = Color(0xFFC5D0E0)

internal val TaskDecomposeLightColorScheme = lightColorScheme(
    primary = FocusBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E6FF),
    onPrimaryContainer = Color(0xFF001B3F),
    secondary = InsightTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB9ECE5),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = WarmSignal,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB7),
    onTertiaryContainer = Color(0xFF2E1500),
    background = CalmPaper,
    onBackground = TaskInk,
    surface = CleanSurface,
    onSurface = TaskInk,
    surfaceVariant = MistSurface,
    onSurfaceVariant = Color(0xFF4B5563),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF8FAFD),
    surfaceContainerHigh = Color(0xFFE1E9F4),
    surfaceContainerHighest = Color(0xFFD3DEEC),
    outline = Color(0xFF7B8798),
    outlineVariant = SoftLine,
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

internal val TaskDecomposeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA9C7FF),
    onPrimary = Color(0xFF003063),
    primaryContainer = Color(0xFF004A96),
    onPrimaryContainer = Color(0xFFD9E6FF),
    secondary = Color(0xFF7DD8CC),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFB9ECE5),
    tertiary = Color(0xFFFFB86B),
    onTertiary = Color(0xFF4C2700),
    tertiaryContainer = Color(0xFF783900),
    onTertiaryContainer = Color(0xFFFFDDB7),
    background = Color(0xFF10151E),
    onBackground = Color(0xFFE7EAF0),
    surface = Color(0xFF171D27),
    onSurface = Color(0xFFE7EAF0),
    surfaceVariant = Color(0xFF3F4857),
    onSurfaceVariant = Color(0xFFC7CFDD),
    surfaceContainerLowest = Color(0xFF0C1118),
    surfaceContainerLow = Color(0xFF141A23),
    surfaceContainer = Color(0xFF1B222D),
    surfaceContainerHigh = Color(0xFF252D39),
    surfaceContainerHighest = Color(0xFF303947),
    outline = Color(0xFF929CAA),
    outlineVariant = Color(0xFF444E5D),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
)
