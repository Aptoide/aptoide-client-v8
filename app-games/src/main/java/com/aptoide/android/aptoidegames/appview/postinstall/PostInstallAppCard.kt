package com.aptoide.android.aptoidegames.appview.postinstall

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIconRight
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PostInstallAppCard(
  app: App,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .semantics(mergeDescendants = true) { }
      .width(88.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    Column {
      Box(contentAlignment = Alignment.TopEnd) {
        AppIconWProgress(
          app = app,
          contentDescription = null,
          modifier = Modifier.size(88.dp),
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
      Text(
        text = app.name,
        color = Palette.White,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .padding(top = 8.dp)
          .defaultMinSize(minHeight = 36.dp),
        style = AGTypography.DescriptionGames
      )
      Spacer(modifier = Modifier.height(8.dp))
      Box(
        modifier = Modifier.fillMaxWidth(),
        propagateMinConstraints = true
      ) {
        PrimarySmallButton(
          onClick = {},
          title = stringResource(R.string.search_sponsored_install),
        )
      }
    }
    Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
  }
}

@PreviewDark
@Composable
private fun PostInstallAppCardPreview() {
  AptoideTheme {
    PostInstallAppCard(
      app = randomApp,
      onClick = {},
    )
  }
}
