package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogoDev

@Composable
fun String.getToolBarLogo(color: Color) =
  when (this) {
    "dev" -> getAptoideGamesToolbarLogoDev(color)
    else -> getAptoideGamesToolbarLogo(color)
  }
