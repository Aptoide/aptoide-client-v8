package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.home.SeeMoreView
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.extensions.runPreviewable
import cm.aptoide.pt.feature_apps.data.MyGamesApp

@Composable
fun MyGamesBundleView(
  title: String,
  icon: String?,
) {
  val (uiState, retry, openApp) = rememberMyGamesBundleUIState()

  MyGamesBundleViewContent(
    title = title,
    icon = icon,
    uiState = uiState,
    onSeeMoreClick = {},
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
      MyGamesBundleHeader(title, icon)
      MyGamesEmptyListView(onRetryClick = onRetryClick)
    }

    MyGamesBundleUiState.Loading -> MyGamesLoadingView {
      MyGamesLoadingListView()
    }

    is MyGamesBundleUiState.AppsList -> MyGamesAppsListView {
      MyGamesBundleHeader(title, icon, onSeeMoreClick)
      MyGamesListView(size = uiState.installedAppsList.size) {
        itemsIndexed(
          items = uiState.installedAppsList,
          key = { _, it -> it.packageName }
        ) { index, it ->
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
  context: Context
): Drawable =
  runPreviewable(preview = {
    remember(
      key1 = packageName,
      key2 = context
    ) {
      getDrawable(context, R.drawable.ic_launcher_background)!!
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
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 216.dp)
  ) {
    Image(
      modifier = Modifier
        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        .matchParentSize()
        .clearAndSetSemantics { }
        .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
      painter = painterResource(AppTheme.drawables.MyGamesBundleBackground),
      contentScale = ContentScale.Crop,
      contentDescription = null,
    )
    Column(
      modifier = Modifier
        .wrapContentHeight()
    ) {
      content()
    }
  }
}

@Composable
fun MyGamesLoadingView(content: @Composable BoxScope.() -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 216.dp),
    contentAlignment = Alignment.Center
  ) {
    Image(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .matchParentSize()
        .clearAndSetSemantics { }
        .clip(RoundedCornerShape(20.dp)),
      painter = painterResource(AppTheme.drawables.MyGamesBundleBackground),
      contentScale = ContentScale.Crop,
      contentDescription = null,
    )
    content()
  }
}

@Composable
fun MyGamesEmptyView(content: @Composable ColumnScope.() -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 216.dp)
  ) {
    Image(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .matchParentSize()
        .clearAndSetSemantics { }
        .clip(RoundedCornerShape(20.dp)),
      painter = painterResource(AppTheme.drawables.MyGamesBundleBackground),
      contentScale = ContentScale.Crop,
      contentDescription = null,
    )
    Column(
      modifier = Modifier
        .wrapContentHeight()
    ) {
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
      style = AppTheme.typography.gameTitleTextCondensed
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
      .padding(start = 16.dp, top = 16.dp, bottom = 10.dp)
      .fillMaxWidth()
      .wrapContentHeight(),
    state = lazyListState,
    contentPadding = PaddingValues(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(24.dp)
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
      style = AppTheme.typography.gameTitleTextCondensedXL,
      color = AppTheme.colors.myGamesMessageTextColor,
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
      modifier = Modifier.padding(all = 4.dp),
      imageVector = AppTheme.icons.SingleGamepad,
      contentDescription = null,
      colorFilter = ColorFilter.tint(AppTheme.colors.myGamesIconTintColor)
    )
    Text(
      text = stringResource(R.string.my_games_empty),
      style = AppTheme.typography.gameTitleTextCondensedXL,
      color = AppTheme.colors.myGamesMessageTextColor,
      textAlign = TextAlign.Center,
      modifier = Modifier.padding(vertical = 4.dp, horizontal = 40.dp)
    )
    Button(
      onClick = onRetryClick,
      shape = RoundedCornerShape(30.dp),
      modifier = Modifier
        .padding(top = 6.dp, bottom = 16.dp)
        .defaultMinSize(minWidth = 72.dp)
        .wrapContentWidth()
        .requiredHeight(32.dp),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
      elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.installAppButtonColor)
    ) {
      Text(
        text = stringResource(R.string.button_retry_title),
        color = Color.White,
        maxLines = 1,
        style = AppTheme.typography.buttonTextMedium
      )
    }
  }
}

@Composable
fun MyGamesBundleHeader(
  title: String,
  icon: String?,
  onSeeMoreClick: (() -> Unit)? = null,
) {
  val label = stringResource(R.string.button_see_all_title)
  Row(
    modifier = Modifier
      .clearAndSetSemantics {
        heading()
        contentDescription = "$title bundle"
        onSeeMoreClick?.let {
          onClick(label = label) {
            it()
            true
          }
        }
      }
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(top = 24.dp, start = 32.dp, end = 16.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f, fill = false)
    ) {
      icon?.let {
        AptoideAsyncImage(
          modifier = Modifier
            .padding(end = 8.dp)
            .size(24.dp),
          data = it,
          contentDescription = null,
        )
      }
      Text(
        modifier = Modifier.clearAndSetSemantics { },
        text = title,
        style = AppTheme.typography.headlineTitleText,
        maxLines = 2
      )
    }
    onSeeMoreClick?.let {
      SeeMoreView(actionColor = AppTheme.colors.myGamesSeeAllViewColor)
    }
  }
}

@PreviewAll
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
