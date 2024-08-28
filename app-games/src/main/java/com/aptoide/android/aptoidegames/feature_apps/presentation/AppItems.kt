package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun AppItem(
  modifier: Modifier = Modifier,
  app: App,
  onClick: () -> Unit,
  installButton: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 96.dp)
      .padding(top = 16.dp, bottom = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(64.dp),
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }
    Column(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Text(
        text = app.name,
        style = AGTypography.DescriptionGames,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        color = Palette.White
      )
      ProgressText(app = app)
    }
    installButton()
  }
}

@Composable
fun LargeAppItem(
  app: App,
  onClick: () -> Unit,
  installButton: @Composable RowScope.() -> Unit,
) {
  Column(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .fillMaxWidth()
      .padding(top = 16.dp, bottom = 16.dp)
      .defaultMinSize(minHeight = 216.dp),
  ) {
    Box(
      contentAlignment = Alignment.TopEnd
    ) {
      AptoideFeatureGraphicImage(
        modifier = Modifier
          .padding(bottom = 8.dp)
          .fillMaxWidth()
          .height(168.dp),
        data = app.featureGraphic,
        contentDescription = null,
      )
      if (app.isAppCoins) {
        Image(
          imageVector = getBonusIconRight(
            iconColor = Palette.Primary,
            outlineColor = Palette.Black,
            backgroundColor = Palette.Secondary
          ),
          contentDescription = null,
          modifier = Modifier.size(32.dp),
        )
      }
    }
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(40.dp),
      )
      Column(
        modifier = Modifier
          .padding(start = 8.dp, end = 8.dp)
          .weight(1f),
      ) {
        Text(
          text = app.name,
          modifier = Modifier.wrapContentHeight(),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AGTypography.DescriptionGames,
          color = Palette.White
        )
        ProgressText(app = app)
      }
      installButton()
    }
  }
}

@PreviewDark
@Composable
fun AppItemPreview() {
  AptoideTheme {
    randomApp.let {
      AppItem(
        app = it,
        onClick = {}
      ) {
        InstallViewShort(app = it)
      }
    }
  }
}

@PreviewDark
@Composable
fun LargeAppItemPreview() {
  AptoideTheme {
    randomApp.let {
      LargeAppItem(
        app = it,
        onClick = {}
      ) {
        InstallViewShort(app = it)
      }
    }
  }
}
