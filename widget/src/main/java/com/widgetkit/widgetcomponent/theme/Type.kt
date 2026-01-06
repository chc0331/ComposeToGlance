package com.widgetkit.widgetcomponent.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.widgetkit.widgetcomponent.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val bodyFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Abhaya Libre"),
        fontProvider = provider
    )
)

val displayFontFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Abhaya Libre"),
        fontProvider = provider
    )
)

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge,
    displayMedium = baseline.displayMedium,
    displaySmall = baseline.displaySmall,
    headlineLarge = baseline.headlineLarge,
    headlineMedium = baseline.headlineMedium,
    headlineSmall = baseline.headlineSmall,
    titleLarge = baseline.titleLarge,
    titleMedium = baseline.titleMedium,
    titleSmall = baseline.titleSmall,
    bodyLarge = baseline.bodyLarge,
    bodyMedium = baseline.bodyMedium,
    bodySmall = baseline.bodySmall,
    labelLarge = baseline.labelLarge,
    labelMedium = baseline.labelMedium,
    labelSmall = baseline.labelSmall
)
