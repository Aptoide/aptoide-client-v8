package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.runtime.Composable
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogoDev
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun String.getToolBarLogo() =
  when (this) {
    "dev" -> getAptoideGamesToolbarLogoDev(Palette.Primary)
    else -> getAptoideGamesToolbarLogo(Palette.Primary)
  }
