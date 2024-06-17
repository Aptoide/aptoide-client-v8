package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.MyGamesApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.drawables.icons.getSingleGamepad
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun MyGamesBundleView(
  title: String,
  icon: String?,
  navigate: (String) -> Unit,
) {
  val (uiState, retry, openApp) = rememberMyGamesBundleUIState()

  MyGamesBundleViewContent(
    title = title,
    icon = icon,
    uiState = uiState,
    onSeeMoreClick = {
      navigate(buildSeeAllMyGamesRoute(title))
    },
    onAppClick = { packageName ->
      openApp(packageName)
    },
    onRetryClick = retry,
  )
}

@Composable
fun MyGamesBundleViewContent(
  title: String,
  icon: String?,
  uiState: MyGamesBundleUiState,
  onSeeMoreClick: () -> Unit,
  onAppClick: (String) -> Unit,
  onRetryClick: () -> Unit,
) {
  val localContext = LocalContext.current
  when (uiState) {
    MyGamesBundleUiState.Empty -> MyGamesEmptyView {
      BundleHeader(
        title = title,
        icon = icon,
        hasMoreAction = true,
        titleColor = Palette.White,
        iconColor = Palette.White
      )
      MyGamesEmptyListView(onRetryClick = onRetryClick)
    }

    MyGamesBundleUiState.Loading -> MyGamesLoadingView {
      MyGamesLoadingListView()
    }

    is MyGamesBundleUiState.AppsList -> MyGamesAppsListView {
      BundleHeader(
        title = title,
        icon = icon,
        hasMoreAction = true,
        onClick = onSeeMoreClick,
        titleColor = Palette.White,
        iconColor = Palette.White,
      )
      MyGamesListView(size = uiState.installedAppsList.size) {
        itemsIndexed(
          items = uiState.installedAppsList,
          key = { _, it -> it.packageName }
        ) { _, it ->
          val appIcon = rememberAppIconDrawable(packageName = it.packageName, localContext)
          MyGameView(
            icon = appIcon,
            name = it.name,
            onClick = { onAppClick(it.packageName) }
          )
        }
      }
    }
  }
}

@Composable
fun rememberAppIconDrawable(
  packageName: String,
  context: Context,
): Drawable =
  runPreviewable(preview = {
    remember(
      key1 = packageName,
      key2 = context
    ) {
      getDrawable(context, R.mipmap.ic_launcher)!!
    }
  }, real = {
    remember(
      key1 = packageName,
      key2 = context
    ) {
      context.getAppIconDrawable(packageName)!!
    }
  })

@Composable
fun MyGamesAppsListView(content: @Composable ColumnScope.() -> Unit) {
  MyGamesWrapper {
    Column {
      content()
    }
  }
}

@Composable
fun MyGamesLoadingView(content: @Composable BoxScope.() -> Unit) {
  MyGamesWrapper {
    content()
  }
}

@Composable
fun MyGamesWrapper(content: @Composable BoxScope.() -> Unit) {
  Box(
    modifier = Modifier
      .padding(start = 16.dp)
      .fillMaxWidth()
      .defaultMinSize(minHeight = 208.dp)
      .background(Palette.Secondary),
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

@Composable
fun MyGamesEmptyView(content: @Composable ColumnScope.() -> Unit) {
  MyGamesWrapper {
    Column {
      content()
    }
  }
}

@Composable
fun MyGameView(
  icon: Drawable,
  name: String,
  onClick: () -> Unit = {},
) {
  Column(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .width(88.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    AptoideAsyncImage(
      modifier = Modifier
        .padding(bottom = 8.dp)
        .size(88.dp)
        .clip(RoundedCornerShape(16.dp)),
      data = icon,
      contentDescription = null,
    )
    Text(
      text = name,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.defaultMinSize(minHeight = 36.dp),
      style = AGTypography.DescriptionGames
    )
  }
}

@Composable
fun MyGamesListView(
  size: Int,
  content: LazyListScope.() -> Unit,
) {
  val lazyListState = rememberLazyListState()

  LazyRow(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(1, size) }
      .padding(bottom = 20.dp)
      .fillMaxWidth()
      .wrapContentHeight(),
    state = lazyListState,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    content()
  }
}

@Composable
fun MyGamesLoadingListView() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    CircularProgressIndicator(modifier = Modifier.padding(bottom = 12.dp))
    Text(
      text = stringResource(R.string.my_games_progress_message),
      style = AGTypography.SubHeadingM,
      color = Palette.White,
      textAlign = TextAlign.Center
    )
  }
}

@Composable
fun MyGamesEmptyListView(onRetryClick: () -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Image(
      imageVector = getSingleGamepad(Palette.White),
      contentDescription = null,
    )
    Text(
      text = stringResource(R.string.my_games_empty),
      style = AGTypography.SubHeadingS,
      color = Palette.White,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(horizontal = 40.dp)
    )
    PrimarySmallButton(
      onClick = onRetryClick,
      modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
      title = stringResource(R.string.button_retry_title),
    )
  }
}

@PreviewDark
@Composable
fun GMInstallationPreview(
  @PreviewParameter(GamesMatchInstallationUiStateProvider::class)
  state: MyGamesBundleUiState,
) {
  AptoideTheme(darkTheme = isSystemInDarkTheme()) {
    Box(contentAlignment = Alignment.Center) {
      MyGamesBundleViewContent(
        title = "My Games",
        icon = "random",
        uiState = state,
        onSeeMoreClick = {},
        onAppClick = {},
        onRetryClick = {},
      )
    }
  }
}

class GamesMatchInstallationUiStateProvider
  : PreviewParameterProvider<MyGamesBundleUiState> {
  override val values: Sequence<MyGamesBundleUiState> = sequenceOf(
    MyGamesBundleUiState.Empty,
    MyGamesBundleUiState.Loading,
    MyGamesBundleUiState.AppsList(
      listOf(
        MyGamesApp("App1 game", "1", "version1"),
        MyGamesApp("App2 game", "2", "version2"),
        MyGamesApp("App2 game", "3", "version3"),
        MyGamesApp("App4 game", "4", "version4")
      )
    ),
  )
}
