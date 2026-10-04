package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RICColorScheme = darkColorScheme(
  primary = NeonBlue,
  onPrimary = DeepNavyDark,
  primaryContainer = ElectricBlue,
  onPrimaryContainer = Color.White,
  secondary = NeonCyan,
  onSecondary = DeepNavyDark,
  secondaryContainer = DarkBlueAccent,
  onSecondaryContainer = Color.White,
  tertiary = NeonPurple,
  onTertiary = Color.White,
  background = DeepNavyDark,
  onBackground = TextWhite,
  surface = CardNavy,
  onSurface = TextWhite,
  surfaceVariant = CardNavyVariant,
  onSurfaceVariant = TextLightGrey,
  outline = GlassBorder,
  error = UnreadRed,
  onError = Color.White
)

@Composable
fun RICFriendsTheme(
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = RICColorScheme,
    typography = Typography,
    content = content
  )
}
