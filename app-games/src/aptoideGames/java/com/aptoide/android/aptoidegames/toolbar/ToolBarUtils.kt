package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogoDev

@Composable
fun String.getToolBarLogo(color: Color): ImageVector =
  if (endsWith("Dev", ignoreCase = true)) getAptoideGamesToolbarLogoDev(color)
  else getAptoideGamesToolbarLogo(color)
