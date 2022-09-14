package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun OtherVersionsView(otherVersionsList: List<App>, listScope: LazyListScope?) {
  listScope?.item { Box(modifier = Modifier.padding(top = 26.dp)) }
  listScope?.items(otherVersionsList) { otherVersionApp ->
    OtherVersionRow(otherVersionApp)
  }
}

@Composable
fun OtherVersionRow(app: App) {
  Row(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
      .fillMaxWidth()
      .height(54.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
    ) {
      Column(
        modifier = Modifier
          .align(Alignment.TopStart)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = app.versionName,
            modifier = Modifier
              .padding(end = 6.dp)
              .width(160.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontSize = MaterialTheme.typography.h6.fontSize,
          )
          Image(
            painter = painterResource(id = cm.aptoide.pt.aptoide_ui.R.drawable.ic_icon_trusted),
            contentDescription = "Trusted icon",
            modifier = Modifier
              .size(16.dp, 16.dp)
              .wrapContentHeight(CenterVertically)
          )
        }
        app.updateDate?.let {
          Text(
            text = it,
            fontSize = MaterialTheme.typography.overline.fontSize,
            modifier = Modifier.padding(bottom = 2.dp)
          )
        }
        Text(
          text = TextFormatter.withSuffix(app.downloads.toLong()) + " Downloads",
          overflow = TextOverflow.Ellipsis,
          fontSize = MaterialTheme.typography.overline.fontSize
        )
      }

      Row(modifier = Modifier.align(Alignment.CenterEnd)) {
        Text(
          text = "" + app.store.subscribers?.let { TextFormatter.withSuffix(it) } + " followers",
          overflow = TextOverflow.Ellipsis,
          modifier = Modifier
            .padding(end = 8.dp)
            .align(CenterVertically)
        )
        Image(
          painter = rememberImagePainter(app.store.icon,
            builder = {
              placeholder(R.drawable.ic_placeholder)
              transformations(RoundedCornersTransformation())
            }),
          contentDescription = "Store Avatar",
          modifier = Modifier
            .width(48.dp)
            .height(48.dp)
        )
      }
    }
  }
}
