package cm.aptoide.pt.feature_editorial.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cm.aptoide.pt.feature_editorial.R
import cm.aptoide.pt.feature_editorial.data.ArticleType
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun EditorialView(
  title: String,
  image: String,
  subtype: ArticleType,
  summary: String,
  date: String,
  views: Long,
  navController: NavController,
) {
  Column(
    modifier = Modifier
      .height(256.dp)
      .clickable {
        navController.navigate("editorial")
      }
      .fillMaxWidth()
  ) {
    Box {
      Image(
        painter = rememberImagePainter(image,
          builder = {
            placeholder(R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Background Image",
        modifier = Modifier
          .height(168.dp)
          .fillMaxWidth()
      )
      Text(text = subtype.label)
    }
    Text(
      text = title,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      fontSize = MaterialTheme.typography.subtitle1.fontSize,
      modifier = Modifier.align(Alignment.Start)
    )
    Text(
      text = summary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      fontSize = MaterialTheme.typography.overline.fontSize,
      modifier = Modifier.align(Alignment.Start)
    )
    Row(
      modifier = Modifier
        .height(14.dp)
        .align(Alignment.End)
    ) {
      Text(
        text = "" + date,
        modifier = Modifier.padding(end = 16.dp),
        fontSize = MaterialTheme.typography.overline.fontSize,
      )
      Text(
        text = "$views views",
        fontSize = MaterialTheme.typography.overline.fontSize,
      )
    }
  }
}