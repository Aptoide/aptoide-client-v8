package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.runtime.Composable
import com.aptoide.android.aptoidegames.theme.AppTheme

@Composable
fun String.getToolBarLogo() =
  when (this) {
    "dev" -> AppTheme.icons.ToolBarLogoDev
    else -> AppTheme.icons.ToolBarLogo
  }
