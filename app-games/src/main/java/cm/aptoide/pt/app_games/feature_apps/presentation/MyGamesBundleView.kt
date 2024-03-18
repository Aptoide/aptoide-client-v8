package cm.aptoide.pt.app_games.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.AptoideAsyncImage
import cm.aptoide.pt.app_games.R
import cm.aptoide.pt.app_games.home.SeeMoreView
import cm.aptoide.pt.app_games.theme.AppTheme

@Composable
fun MyGamesBundleView(
  title: String,
  icon: String?,
) {
  MyGamesBundleViewContent(
    title = title,
    icon = icon,
  )
}

@Composable
fun MyGamesBundleViewContent(
  title: String,
  icon: String?,
) {
  MyGamesBundleHeader(title, icon)
  MyGamesEmptyListView()
}

@Composable
fun MyGamesEmptyListView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Image(
      modifier = Modifier.padding(all = 4.dp),
      imageVector = AppTheme.icons.SingleGamepad,
      contentDescription = null,
      colorFilter = ColorFilter.tint(AppTheme.colors.myGamesIconTintColor)
    )
    Text(
      text = stringResource(R.string.my_games_empty),
      style = AppTheme.typography.bodyCopyXS,
      color = AppTheme.colors.myGamesMessageTextColor,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp)
    )
    Button(
      onClick = {},
      shape = RoundedCornerShape(30.dp),
      modifier = Modifier
        .padding(top = 6.dp, bottom = 16.dp)
        .defaultMinSize(minWidth = 72.dp)
        .wrapContentWidth()
        .requiredHeight(32.dp),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
      elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.installAppButtonColor)
    ) {
      Text(
        text = stringResource(R.string.button_retry_title),
        color = Color.White,
        maxLines = 1,
        style = AppTheme.typography.bodyCopySmall
      )
    }
  }
}

@Composable
fun MyGamesBundleHeader(
  title: String,
  icon: String?,
  onSeeMoreClick: (() -> Unit)? = null,
) {
  val label = stringResource(R.string.button_see_all_title)
  Row(
    modifier = Modifier
      .clearAndSetSemantics {
        heading()
        contentDescription = "$title bundle"
        onSeeMoreClick?.let {
          onClick(label = label) {
            it()
            true
          }
        }
      }
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(top = 24.dp, start = 32.dp, end = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f, fill = false)
    ) {
      icon?.let {
        AptoideAsyncImage(
          modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp),
          data = it,
          contentDescription = null,
        )
      }
      Text(
        modifier = Modifier.clearAndSetSemantics { },
        text = title,
        style = AppTheme.typography.headlineTitleText,
        maxLines = 2
      )
    }
    onSeeMoreClick?.let {
      SeeMoreView(
        actionColor = AppTheme.colors.myGamesSeeAllViewColor
      )
    }
  }
}
