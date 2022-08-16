package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_appview.domain.model.RelatedCard
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Composable
fun RelatedContentView(relatedContentList: List<RelatedCard>, listScope: LazyListScope?) {
  listScope?.item { Box(modifier = Modifier.padding(top = 26.dp)) }
  listScope?.items(relatedContentList) { relatedCard ->
    RelatedContentCard(relatedCard)
  }
}

@Composable
fun RelatedContentCard(relatedCard: RelatedCard) {
  Column(
    modifier = Modifier
      .height(256.dp)
      .padding(start = 16.dp, end = 16.dp, bottom = 28.dp)
      .fillMaxWidth()
  ) {
    Image(
      painter = rememberImagePainter(relatedCard.icon,
        builder = {
          placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
          transformations(RoundedCornersTransformation())
        }),
      contentDescription = "Background Image",
      modifier = Modifier
        .height(168.dp)
        .fillMaxWidth()
    )
    Text(
      text = relatedCard.title,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      fontSize = MaterialTheme.typography.subtitle1.fontSize,
      modifier = Modifier.align(Alignment.Start)
    )
    Text(
      text = relatedCard.summary,
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
        text = "" + relatedCard.date,
        fontSize = MaterialTheme.typography.overline.fontSize,
      )
      Text(
        text = "" + relatedCard.views + " views",
        fontSize = MaterialTheme.typography.overline.fontSize,
      )
    }
  }
}

