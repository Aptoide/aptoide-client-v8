package cm.aptoide.pt.app_games.editorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.AptoideFeatureGraphicImage
import cm.aptoide.pt.app_games.home.BundleHeader
import cm.aptoide.pt.app_games.home.EmptyBundleView
import cm.aptoide.pt.app_games.home.LoadingBundleView
import cm.aptoide.pt.app_games.theme.AppTheme
import cm.aptoide.pt.app_games.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.randomArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.editorialsCardViewModel
import cm.aptoide.pt.feature_home.domain.Bundle

// Containers

@Composable
fun EditorialBundle(
  bundle: Bundle,
  filterId: String? = null,
  subtype: String? = null,
) {
  val editorialsCardViewModel = editorialsCardViewModel(
    tag = bundle.tag,
    subtype = subtype,
    salt = bundle.timestamp
  )
  val uiState by editorialsCardViewModel.uiState.collectAsState()
  val items = uiState?.filter { it.id != filterId }
  val lazyListState = rememberLazyListState()

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(bottom = 16.dp)
  ) {
    BundleHeader(
      bundle = bundle,
    )
    if (items == null) {
      LoadingBundleView(height = 240.dp)
    } else if (items.isEmpty()) {
      EmptyBundleView(height = 240.dp)
    } else {
      LazyRow(
        modifier = Modifier
          .semantics {
            collectionInfo = CollectionInfo(1, items.size)
          }
          .fillMaxWidth()
          .wrapContentHeight()
          .defaultMinSize(minHeight = 240.dp),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        itemsIndexed(items) { index, editorialMeta ->
          EditorialsViewCard(
            articleMeta = editorialMeta,
            onClick = {

            },
          )
        }
      }
    }
  }
}

@Composable
fun EditorialsViewCard(
  articleMeta: ArticleMeta,
  onClick: () -> Unit,
) = Column(
  modifier = Modifier
    .width(280.dp)
    .clickable(onClick = onClick)
) {
  Box(modifier = Modifier.padding(bottom = 8.dp)) {
    EditorialImage { articleMeta.image }
    EditorialTypeLabel { articleMeta.caption.uppercase() }
  }
  EditorialTitle {
    articleMeta.title
  }
  EditorialSummary {
    articleMeta.summary
  }
}

@Composable
fun RelatedEditorialViewCard(
  articleMeta: ArticleMeta,
  onClick: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
  ) {
    Box(modifier = Modifier.padding(bottom = 8.dp)) {
      EditorialImage { articleMeta.image }
      EditorialTypeLabel { articleMeta.caption.uppercase() }
    }
    EditorialTitle {
      articleMeta.title
    }
    EditorialSummary {
      articleMeta.summary
    }
  }
}

// Components

@Composable
fun EditorialImage(getUrl: () -> String) = AptoideFeatureGraphicImage(
  modifier = Modifier
    .fillMaxWidth()
    .aspectRatio(ratio = 280f / 136)
    .clip(RoundedCornerShape(16.dp)),
  data = getUrl(),
  contentDescription = null
)

@Composable
fun EditorialTypeLabel(getLabel: () -> String) = Text(
  text = getLabel(),
  style = AppTheme.typography.bodyCopyXS,
  color = AppTheme.colors.onPrimary,
  textAlign = TextAlign.Center,
  modifier = Modifier
    .padding(start = 8.dp, top = 8.dp)
    .shadow(
      elevation = 8.dp,
      shape = RoundedCornerShape(16.dp),
      clip = true
    )
    .background(color = AppTheme.colors.primary)
    .wrapContentWidth()
    .wrapContentHeight()
    .clip(RoundedCornerShape(16.dp))
    .padding(horizontal = 10.dp, vertical = 5.dp)
)

@Composable
fun EditorialTitle(getTitle: () -> String) = Text(
  text = getTitle(),
  maxLines = 1,
  overflow = TextOverflow.Ellipsis,
  modifier = Modifier.height(24.dp),
  style = AppTheme.typography.bodyCopySmallBold
)

@Composable
fun EditorialSummary(getSummary: () -> String) {
  Text(
    text = getSummary(),
    maxLines = 3,
    overflow = TextOverflow.Ellipsis,
    modifier = Modifier.height(56.dp),
    style = AppTheme.typography.bodyCopyXS
  )
}

@PreviewAll
@Composable
fun EditorialsViewCardPreview() {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    EditorialsViewCard(
      articleMeta = randomArticleMeta,
      onClick = {}
    )
  }
}

@PreviewAll
@Composable
fun RelatedEditorialViewCardPreview() {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    RelatedEditorialViewCard(
      articleMeta = randomArticleMeta,
      onClick = {}
    )
  }
}