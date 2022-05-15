package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun RelatedContentView(relatedContentList: List<RelatedCard>) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp)
  ) {
    items(relatedContentList) { relatedCard ->
      RelatedContentCard(relatedCard)
    }
  }

}

@Composable
fun RelatedContentCard(relatedCard: RelatedCard) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(256.dp)
  ) {
    Box(modifier = Modifier.fillMaxWidth()) {
      Image(
        painter = rememberImagePainter(relatedCard.icon,
          builder = {
            placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
            transformations(RoundedCornersTransformation())
          }),
        contentDescription = "Catappult Icon",
        modifier = Modifier
          .fillMaxWidth()
          .height(168.dp)
          .padding(bottom = 6.dp)
      )
    }
    Text(
      text = relatedCard.title,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start)
    )
    Text(
      text = relatedCard.summary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start)
    )
    Row(
      modifier = Modifier
        .height(14.dp)
        .align(Alignment.End)
    ) {
      Text(text = "" + relatedCard.date, modifier = Modifier.padding(16.dp))
      Text(text = "" + relatedCard.views + " views")
    }
  }
}

