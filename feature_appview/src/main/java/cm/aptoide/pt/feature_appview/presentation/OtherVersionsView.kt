package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.R
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun OtherVersionsView(otherVersionsList: List<App>) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    items(otherVersionsList) { otherVersionApp ->
      OtherVersionRow(otherVersionApp)
    }
  }
}

@Composable
fun OtherVersionRow(app: App) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(54.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
    ) {
      Column(modifier = Modifier.align(Alignment.TopStart)) {
        Row {
          Text(text = app.versionName, modifier = Modifier.padding(end = 6.dp))
          Image(
            painter = painterResource(id = cm.aptoide.pt.feature_appview.R.drawable.ic_icon_trusted),
            contentDescription = "Trusted icon",
            modifier = Modifier
              .size(20.dp, 24.dp)
              .wrapContentHeight(CenterVertically)
          )
        }
        app.updateDate?.let { Text(text = it) }
        Text(text = "" + app.downloads + " Downloads")
      }

      Row(modifier = Modifier.align(Alignment.CenterEnd)) {
        Text(
          text = "" + app.store.subscribers + " followers",
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
