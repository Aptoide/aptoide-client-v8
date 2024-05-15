package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.EmptyBundleView
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.getSeeMoreRouteNavigation
import com.aptoide.android.aptoidegames.installer.presentation.AppIconWProgress
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme

@Composable
fun AppsGridBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) {
  val (uiState, _) = rememberAppsByTag(bundle.tag, bundle.timestamp)

  Column(
    modifier = Modifier.padding(bottom = 28.dp)
  ) {
    BundleHeader(
      bundle = bundle,
      onClick = getSeeMoreRouteNavigation(bundle = bundle, navigate = navigate)

    )
    when (uiState) {
      is AppsListUiState.Idle -> AppsRowView(
        appsList = uiState.apps,
        navigate = navigate,
      )

      AppsListUiState.Empty,
      AppsListUiState.Error,
      AppsListUiState.NoConnection,
      -> EmptyBundleView(height = 184.dp)

      AppsListUiState.Loading -> LoadingBundleView(height = 184.dp)
    }
  }
}

@Composable
fun AppsRowView(
  appsList: List<App>,
  navigate: (String) -> Unit,
  appsNameColor: Color = Color.Unspecified,
) {
  val lazyListState = rememberLazyListState()

  LazyRow(
    modifier = Modifier
      .semantics {
        collectionInfo = CollectionInfo(1, appsList.size)
      }
      .fillMaxWidth()
      .wrapContentHeight(),
    state = lazyListState,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    itemsIndexed(appsList) { index, item ->
      AppGridView(
        app = item,
        onClick = {
          navigate(
            buildAppViewRoute(item.packageName)
          )
        },
        appsNameColor = appsNameColor
      )
    }
  }
}

@Composable
internal fun AppGridView(
  app: App,
  onClick: () -> Unit = {},
  appsNameColor: Color = Color.Unspecified,
) {
  Column(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .width(88.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    Box(
      contentAlignment = Alignment.TopEnd,
      modifier = Modifier.padding(bottom = 8.dp)
    ) {
      AppIconWProgress(
        app = app,
        contentDescription = null,
        modifier = Modifier.size(88.dp),
      )
    }
    Text(
      text = app.name,
      color = appsNameColor,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier
        .defaultMinSize(minHeight = 36.dp),
      style = AppTheme.typography.headlineTitleText
    )
  }
}

@PreviewAll
@Composable
fun AppGridViewPreview() {
  AptoideTheme {
    AppGridView(
      app = randomApp,
      onClick = {},
      appsNameColor = Color.Unspecified
    )
  }
}
