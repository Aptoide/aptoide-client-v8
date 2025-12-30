package com.aptoide.android.aptoidegames.editorial

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiState
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiStateProvider
import cm.aptoide.pt.feature_editorial.presentation.rememberEditorialListState
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.AptoideFeatureGraphicImage
import com.aptoide.android.aptoidegames.analytics.presentation.SwipeListener
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun EditorialBundle(
  bundle: Bundle,
  modifier: Modifier = Modifier,
  listenForPosition: Boolean = true,
  filterId: String? = null,
  subtype: String? = null,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  LaunchedEffect(Unit) {
    if (!AptoideMMPCampaign.allowedBundleTags.keys.contains(bundle.tag)) {
      AptoideMMPCampaign.allowedBundleTags[bundle.tag] = UTMInfo(
        utmMedium = "editorial",
        utmCampaign = "editorial",
        utmContent = "home-editorial"
      )
      if (bundle.hasMoreAction) {
        AptoideMMPCampaign.allowedBundleTags["${bundle.tag}-more"] = UTMInfo(
          utmMedium = "editorial",
          utmCampaign = "editorial",
          utmContent = "editorial-seeall"
        )
      }
    }
  }

  val (uiState, reload) = rememberEditorialListState(
    tag = bundle.tag,
    subtype = subtype,
    salt = bundle.timestamp
  )
  EditorialBundleContent(
    uiState = uiState,
    bundle = bundle,
    modifier = modifier,
    listenForPosition = listenForPosition,
    filterId = filterId,
    subtype = subtype,
    navigate = navigate,
    spaceBy = spaceBy
  )
}

@Composable
private fun EditorialBundleContent(
  uiState: ArticleListUiState,
  bundle: Bundle,
  modifier: Modifier = Modifier,
  listenForPosition: Boolean = true,
  filterId: String? = null,
  subtype: String? = null,
  navigate: (String) -> Unit,
  spaceBy: Int = 0,
) {
  when (uiState) {
    ArticleListUiState.Empty,
    ArticleListUiState.Error,
    ArticleListUiState.NoConnection -> Unit

    ArticleListUiState.Loading -> {
      LoadingBundleView(height = 240.dp)
      Spacer(Modifier.size(spaceBy.dp))
    }

    is ArticleListUiState.Idle -> {
      val items = uiState.articles.filter { it.id != filterId }
      val lazyListState = rememberLazyListState()
      RealEditorialBundle(
        modifier = modifier,
        bundle = bundle,
        items = items,
        listenForPosition = listenForPosition,
        lazyListState = lazyListState,
        navigate = navigate,
        subtype = subtype,
      )
      Spacer(Modifier.size(spaceBy.dp))
    }
  }
}

@Composable
private fun RealEditorialBundle(
  modifier: Modifier = Modifier,
  bundle: Bundle,
  items: List<ArticleMeta>,
  listenForPosition: Boolean,
  lazyListState: LazyListState,
  navigate: (String) -> Unit,
  subtype: String?
) {
  Column(modifier = modifier) {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = {
        navigate(
          buildSeeMoreEditorialsRoute(
            title = bundle.title,
            bundleTag = bundle.actions.first().tag,
            subtype = subtype
          )
            .withBundleMeta(
              bundle.meta.copy(tag = bundle.actions.first().tag)
            )
        )
      },
    )
    SwipeListener(interactionSource = lazyListState.interactionSource)
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .semantics {
          collectionInfo = CollectionInfo(1, items.size)
        },
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
              buildEditorialRoute(editorialMeta.id)
                .withItemPosition(if (listenForPosition) index else null)
            )
          },
        )
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
        .width(280.dp)
        .height(136.dp),
      data = articleMeta.image,
      contentDescription = null
    )
    Text(
      text = articleMeta.caption,
      style = AGTypography.BodyBold,
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
private fun EditorialsBundlePreview(
  @PreviewParameter(ArticleListUiStateProvider::class) uiState: ArticleListUiState
) {
  AptoideTheme {
    EditorialBundleContent(
      uiState = uiState,
      bundle = randomBundle,
      navigate = { }
    )
  }
}
