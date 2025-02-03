package com.aptoide.android.aptoidegames.feature_promotional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_editorial.domain.ArticleMeta
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiState
import cm.aptoide.pt.feature_editorial.presentation.ArticleListUiStateProvider
import cm.aptoide.pt.feature_editorial.presentation.rememberEditorialListState
import cm.aptoide.pt.feature_home.domain.Bundle
import cm.aptoide.pt.feature_home.domain.randomBundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.editorial.buildEditorialRoute
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun EventPromotionalView(
  bundle: Bundle,
  modifier: Modifier = Modifier,
  navigate: (String) -> Unit,
) {
  val (uiState) = rememberEditorialListState(
    tag = bundle.tag,
    salt = bundle.timestamp
  )
  EventBundleContent(
    uiState = uiState,
    bundle = bundle,
    modifier = modifier,
    navigate = navigate
  )
}

@Composable
private fun EventBundleContent(
  uiState: ArticleListUiState,
  bundle: Bundle,
  modifier: Modifier = Modifier,
  navigate: (String) -> Unit,
) {
  when (uiState) {
    ArticleListUiState.Empty,
    ArticleListUiState.Error,
    ArticleListUiState.NoConnection -> Unit

    ArticleListUiState.Loading -> LoadingBundleView(height = 240.dp)

    is ArticleListUiState.Idle -> {
      val editorialMeta = uiState.articles[0]
      EventCard(
        bundle = bundle,
        modifier = modifier,
        articleMeta = editorialMeta,
        onClick = {
          navigate(
            buildEditorialRoute(editorialMeta.id)
          )
        },
      )
    }
  }
}

@Composable fun EventCard(
  bundle: Bundle,
  modifier: Modifier,
  articleMeta: ArticleMeta,
  onClick: () -> Unit
) = Column(
  modifier = modifier
    .padding(horizontal = 16.dp)
    .clickable(onClick = onClick)
) {
  Box(
    modifier = Modifier
      .padding(bottom = 8.dp)
      .fillMaxWidth()
  ) {
    AptoidePromotionalFeatureGraphicImage(
      featureGraphic = articleMeta.image,
      label = bundle.title,
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
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      modifier = modifier,
      text = stringResource(
        id = R.string.editorial_card_views_short, articleMeta.views
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AGTypography.InputsXS,
      color = Palette.GreyLight
    )
    Text(
      modifier = modifier,
      text = stringResource(id = R.string.promotional_on_going_event),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      style = AGTypography.InputsXS,
      color = Palette.Primary
    )
  }
}

@PreviewDark
@Composable
private fun EventPreview(
  @PreviewParameter(ArticleListUiStateProvider::class) uiState: ArticleListUiState
) {
  AptoideTheme {
    EventBundleContent(
      bundle = randomBundle,
      uiState = uiState,
      modifier = Modifier,
      navigate = { }
    )
  }
}