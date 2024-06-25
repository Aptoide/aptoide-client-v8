package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.domain.randomArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.rememberEditorialsCardState
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.feature_apps.presentation.SmallEmptyView
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun EditorialBundle(
  modifier: Modifier = Modifier,
  bundle: Bundle,
  navigate: (String) -> Unit,
  filterId: String? = null,
  subtype: String? = null,
  listenForPosition: Boolean = true,
) {
  val (uiState, adListId) = rememberEditorialsCardState(
    tag = bundle.tag,
    subtype = subtype,
    salt = bundle.timestamp
  )
  val items = uiState?.filter { it.id != filterId }
  val lazyListState = rememberLazyListState()

  RealEditorialBundle(
    modifier = modifier,
    bundle = bundle,
    items = items,
    adListId = adListId,
    lazyListState = lazyListState,
    navigate = navigate,
    listenForPosition = listenForPosition,
  )
}

@Composable
private fun RealEditorialBundle(
  modifier: Modifier = Modifier,
  bundle: Bundle,
  items: List<ArticleMeta>?,
  adListId: String,
  lazyListState: LazyListState,
  navigate: (String) -> Unit,
  listenForPosition: Boolean,
) {
  Column(
    modifier = modifier.padding(bottom = 16.dp)
  ) {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate),
    )
    if (items == null) {
      LoadingBundleView(height = 240.dp)
    } else if (items.isEmpty()) {
      SmallEmptyView(modifier = Modifier.height(240.dp))
    } else {
      SwipeListener(interactionSource = lazyListState.interactionSource)
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .semantics {
            collectionInfo = CollectionInfo(1, items.size)
          }
          .defaultMinSize(minHeight = 240.dp),
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        itemsIndexed(items) { index, editorialMeta ->
          EditorialsViewCard(
            modifier = Modifier.width(280.dp),
            articleMeta = editorialMeta,
            onClick = {
              navigate(
                buildEditorialRoute(
                  articleId = editorialMeta.id,
                  adListId = adListId,
                ).withItemPosition(if (listenForPosition) index else null)
              )
            },
          )
        }
      }
    }
  }
}

@Composable
fun EditorialsViewCard(
  modifier: Modifier = Modifier,
  articleMeta: ArticleMeta,
  onClick: () -> Unit,
) = Column(
  modifier = modifier.clickable(onClick = onClick)
) {
  Box(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .fillMaxWidth()
  ) {
    AptoideFeatureGraphicImage(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(ratio = 280f / 136),
      data = articleMeta.image,
      contentDescription = null
    )
    Text(
      text = articleMeta.caption,
      style = AGTypography.Body,
      color = Palette.Primary,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .padding(start = 8.dp, top = 8.dp)
        .background(color = Palette.Black)
        .padding(horizontal = 8.dp, vertical = 4.dp)
    )
  }
  Text(
    modifier = modifier,
    text = articleMeta.title,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    style = AGTypography.InputsL,
    color = Palette.White
  )
  Text(
    modifier = modifier,
    text = articleMeta.summary,
    maxLines = 3,
    overflow = TextOverflow.Ellipsis,
    style = AGTypography.SmallGames,
    color = Palette.White
  )
}

@PreviewDark
@Composable
private fun EditorialsViewCardPreview() {
  AptoideTheme {
    RealEditorialBundle(
      bundle = randomBundle,
      items = listOf(randomArticleMeta, randomArticleMeta),
      lazyListState = LazyListState(),
      adListId = "adListId",
      navigate = {},
      listenForPosition = false
    )
  }
}
