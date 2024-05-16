package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.installer.presentation.ProgressText
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@Composable
fun AppItem(
  app: App,
  onClick: () -> Unit,
  installButton: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = Modifier
      .clickable(onClick = onClick)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 96.dp)
      .padding(top = 16.dp, bottom = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AppIconWProgress(
      app = app,
      contentDescription = null,
      modifier = Modifier.size(64.dp),
    )
    Column(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.SpaceEvenly
    ) {
      Text(
        text = app.name,
        style = AppTheme.typography.gameTitleTextCondensedLarge,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
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
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .padding(bottom = 8.dp)
        .fillMaxWidth()
        .height(168.dp)
        .clip(RoundedCornerShape(16.dp)),
      data = app.featureGraphic,
      contentDescription = null,
    )
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
          style = AppTheme.typography.gameTitleTextCondensedLarge
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
        InstallViewShort(
          app = it,
          onInstallStarted = {}
        )
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
        InstallViewShort(
          app = it,
          onInstallStarted = {}
        )
      }
    }
  }
}
