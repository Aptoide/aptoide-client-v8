package cm.aptoide.pt.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.DateUtils
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.appVersions
import cm.aptoide.pt.theme.AppTheme

@Composable
fun OtherVersionsView(packageName: String) {
  val (uiState, _) = appVersions(packageName = packageName)
  Column(
    modifier = Modifier.padding(top = 24.dp)
  ) {
    (uiState as? AppsListUiState.Idle)?.run {
      apps.forEach { app ->
        OtherVersionRow(app)
      }
    }
  }
}

@Composable
fun OtherVersionRow(app: App) {
  Row(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
      .fillMaxWidth()
      .height(57.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(57.dp)
    ) {
      Column(
        modifier = Modifier
          .align(Alignment.TopStart)
      ) {
        Row(verticalAlignment = CenterVertically) {
          Text(
            text = app.versionName,
            modifier = Modifier
              .padding(end = 4.dp)
              .wrapContentWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = AppTheme.typography.medium_L,
          )
          Image(
            imageVector = AppTheme.icons.TrustedIcon,
            contentDescription = "Trusted icon",
            modifier = Modifier
              .size(16.dp, 16.dp)
          )
        }
        app.updateDate?.let {
          Text(
            text = DateUtils.getTimeDiffString(LocalContext.current, it),
            color = AppTheme.colors.greyText,
            style = AppTheme.typography.regular_XXS,
            modifier = Modifier.padding(bottom = 2.dp)
          )
        }
        Text(
          text = TextFormatter.withSuffix(app.downloads.toLong()) + " Downloads",
          overflow = TextOverflow.Ellipsis,
          color = AppTheme.colors.greyText,
          style = AppTheme.typography.regular_XXS
        )
      }

      Row(modifier = Modifier.align(Alignment.CenterEnd)) {
        Text(
          text = "" + app.store.subscribers?.let { TextFormatter.withSuffix(it) } + " followers",
          overflow = TextOverflow.Ellipsis,
          style = AppTheme.typography.regular_XXS,
          color = AppTheme.colors.greyText,
          modifier = Modifier
            .padding(end = 8.dp)
            .align(CenterVertically)
        )
        AptoideAsyncImage(
          data = app.store.icon,
          contentDescription = "Store Avatar",
          placeholder = ColorPainter(AppTheme.colors.placeholderColor),
          modifier = Modifier
            .width(48.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
        )
      }
    }
  }
}
