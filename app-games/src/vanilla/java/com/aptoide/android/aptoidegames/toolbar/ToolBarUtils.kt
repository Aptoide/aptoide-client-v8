package com.aptoide.android.aptoidegames.toolbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cm.aptoide.pt.aptoide_ui.icons.getToolbarLogo
import cm.aptoide.pt.aptoide_ui.theme.aptoideIconBackgroundWhite
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
@Suppress("UnusedReceiverParameter")
fun String.getToolBarLogo(color: Color): ImageVector =
  getToolbarLogo(
    textColor = Palette.White,
    primaryIconColor = Palette.Primary,
    secondaryIconColor = aptoideIconBackgroundWhite,
  )
