package com.alifhasnain.unbundle.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.alifhasnain.unbundle.R

// Carrier livery: the header field and ticket strip keep these in both schemes.
val Livery = Color(0xFF1D2B5C)
val TicketRed = Color(0xFFC8102E)

private val Light = lightColorScheme(
    primary = Livery,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5D9EC),
    onPrimaryContainer = Livery,
    secondary = TicketRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF8DADC),
    onSecondaryContainer = Color(0xFF5C0614),
    // Carbon ink: every filled figure on a coupon or ticket.
    tertiary = Color(0xFF4B3F8F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE4DDF5),
    onTertiaryContainer = Color(0xFF2E2566),
    background = Color(0xFFECE9E2),
    onBackground = Color(0xFF1B2140),
    surface = Color(0xFFFBFAF7),
    onSurface = Color(0xFF1B2140),
    surfaceVariant = Color(0xFFE3E1DA),
    onSurfaceVariant = Color(0xFF555B70),
    outline = Color(0xFF7E8399),
    outlineVariant = Color(0xFFD3D3DA),
    error = TicketRed,
    onError = Color.White,
    inverseSurface = Color(0xFF1B2140),
    inverseOnSurface = Color(0xFFF1F0F6),
    inversePrimary = Color(0xFFBAC4F2),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFBAC4F2),
    onPrimary = Color(0xFF0E1A45),
    primaryContainer = Color(0xFF2B3A73),
    onPrimaryContainer = Color(0xFFDDE1FF),
    secondary = Color(0xFFFF6B78),
    onSecondary = Color(0xFF40000B),
    secondaryContainer = Color(0xFF5C1420),
    onSecondaryContainer = Color(0xFFFFDADC),
    tertiary = Color(0xFFCBBEFF),
    onTertiary = Color(0xFF2A1F63),
    tertiaryContainer = Color(0xFF342B6E),
    onTertiaryContainer = Color(0xFFE6DEFF),
    background = Color(0xFF0B1028),
    onBackground = Color(0xFFE6E7F2),
    surface = Color(0xFF151C3D),
    onSurface = Color(0xFFE6E7F2),
    surfaceVariant = Color(0xFF232B4F),
    onSurfaceVariant = Color(0xFFAEB3CC),
    outline = Color(0xFF747A99),
    outlineVariant = Color(0xFF30385C),
    error = Color(0xFFFF6B78),
    onError = Color(0xFF40000B),
    inverseSurface = Color(0xFFE6E7F2),
    inverseOnSurface = Color(0xFF1B2140),
    inversePrimary = Livery,
)

val Barlow = FontFamily(
    Font(R.font.barlow_condensed_medium, FontWeight.Medium),
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_condensed_bold, FontWeight.Bold),
)

// Airline grotesk for titles, labels and figures; body copy stays on the system face.
private val UnbundleType = Typography().run {
    copy(
        displaySmall = displaySmall.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold),
        headlineMedium = headlineMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold),
        headlineSmall = headlineSmall.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold),
        titleLarge = titleLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
        titleMedium = titleMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        labelLarge = labelLarge.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, letterSpacing = 1.2.sp),
        labelMedium = labelMedium.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 1.2.sp),
        labelSmall = labelSmall.copy(fontFamily = Barlow, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, letterSpacing = 1.4.sp),
    )
}

@Composable
fun UnbundleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Dark else Light,
        typography = UnbundleType,
        content = content,
    )
}
