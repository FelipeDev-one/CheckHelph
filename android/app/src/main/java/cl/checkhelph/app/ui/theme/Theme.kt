package cl.checkhelph.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Paleta de marca (kit/brand/README.md)
val Petroleo = Color(0xFF16333B)
val Teal = Color(0xFF0F6B63)
val Menta = Color(0xFFDDF1EA)
val Marfil = Color(0xFFF5F3ED)
val Ambar = Color(0xFFE2A43A)
val Grafito = Color(0xFF263A40)
val Rojo = Color(0xFFB84B3E)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Menta,
    onPrimaryContainer = Petroleo,
    secondary = Petroleo,
    onSecondary = Marfil,
    secondaryContainer = Color(0xFFE3ECEB),
    onSecondaryContainer = Petroleo,
    tertiary = Ambar,
    onTertiary = Petroleo,
    tertiaryContainer = Color(0xFFFBEBCB),
    onTertiaryContainer = Petroleo,
    background = Marfil,
    onBackground = Petroleo,
    surface = Color.White,
    onSurface = Petroleo,
    surfaceVariant = Color(0xFFEDF0EC),
    onSurfaceVariant = Color(0xFF64777A),
    surfaceContainer = Color.White,
    outline = Color(0xFFC9D1CD),
    outlineVariant = Color(0xFFE5E9E5),
    error = Rojo,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF6CC3B6),
    onPrimary = Color(0xFF003731),
    primaryContainer = Teal,
    onPrimaryContainer = Menta,
    secondary = Color(0xFFB4CCD2),
    onSecondary = Petroleo,
    tertiary = Ambar,
    onTertiary = Petroleo,
    tertiaryContainer = Color(0xFF5C4413),
    onTertiaryContainer = Color(0xFFFBEBCB),
    background = Color(0xFF0E1F23),
    onBackground = Color(0xFFE4ECEA),
    surface = Color(0xFF16292E),
    onSurface = Color(0xFFE4ECEA),
    surfaceVariant = Grafito,
    onSurfaceVariant = Color(0xFFB0C0C2),
    surfaceContainer = Color(0xFF16292E),
    outline = Color(0xFF5B6F73),
    outlineVariant = Color(0xFF2E4348),
    error = Color(0xFFFFB4A8),
)

private val AppTypography = Typography().run {
    copy(
        headlineMedium = headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp),
        titleLarge = titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = titleMedium.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, letterSpacing = 0.1.sp),
    )
}

/** Sin color dinámico a propósito: la app mantiene la identidad de marca de CheckHelph. */
@Composable
fun CheckHelphTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
