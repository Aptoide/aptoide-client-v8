package cm.aptoide.pt.appview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.aptoide_ui.AptoideAsyncImage
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.editorial.isNavigating
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.relatedEditorialsCardViewModel
import cm.aptoide.pt.feature_reactions.ui.ReactionsView

@Composable
fun RelatedContentView(
  packageName: String,
  onRelatedContentClick: (String) -> Unit,

) {
  val editorialsMetaViewModel = relatedEditorialsCardViewModel(packageName = packageName)
  val uiState by editorialsMetaViewModel.uiState.collectAsState()
  Column(
    modifier = Modifier.padding(top = 24.dp)
  ) {
    uiState?.forEach { editorialMeta ->
      RelatedContentCard(editorialMeta, onRelatedContentClick)
    }
  }

}

@Composable
fun RelatedContentCard(
  articleMeta: ArticleMeta,
  onRelatedContentClick: (String) -> Unit
) {
  Column(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
      .height(256.dp)
      .fillMaxWidth()
      .clickable { onRelatedContentClick(articleMeta.id) }
  ) {
    Box(contentAlignment = Alignment.TopStart, modifier = Modifier.padding(bottom = 8.dp)) {
      AptoideAsyncImage(
        data = articleMeta.image,
        contentDescription = "Article Image",
        placeholder = ColorPainter(AppTheme.colors.placeholderColor),
        modifier = Modifier
          .fillMaxWidth()
          .height(168.dp)
          .clip(RoundedCornerShape(16.dp))
      )
      Card(
        elevation = 0.dp,
        backgroundColor = AppTheme.colors.editorialLabelColor,
        modifier = Modifier
          .padding(start = 8.dp, top = 8.dp)
          .wrapContentWidth()
          .height(24.dp)
          .clip(RoundedCornerShape(16.dp))
      ) {
        Text(
          text = "App of The Week",
          style = AppTheme.typography.button_S,
          color = Color.White,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
        )
      }
    }
    Text(
      text = articleMeta.title,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.medium_M
    )
    Text(
      text = articleMeta.summary,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.align(Alignment.Start),
      style = AppTheme.typography.regular_XXS
    )
    Row(
      modifier = Modifier
        .height(32.dp), verticalAlignment = Alignment.CenterVertically
    ) {
      //bug here, isNavigating will only work once.
      ReactionsView(id = articleMeta.id, isNavigating = isNavigating)
      Text(
        text = TextFormatter.formatDateToSystemLocale(LocalContext.current, articleMeta.date),
        modifier = Modifier.padding(end = 16.dp),
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center,
        color = AppTheme.colors.editorialDateColor
      )
      Image(
        imageVector = AppTheme.icons.ViewsIcon,
        contentDescription = "Editorial views",
        modifier = Modifier
          .padding(end = 8.dp)
          .width(14.dp)
          .height(8.dp)
      )
      Text(
        text = TextFormatter.withSuffix(articleMeta.views) + " views",
        style = AppTheme.typography.regular_XXS,
        textAlign = TextAlign.Center, color = AppTheme.colors.greyText
      )
    }
  }
}
