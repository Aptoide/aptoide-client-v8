package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import cm.aptoide.pt.aptoide_ui.animations.animatedComposable
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.feature_apps.data.randomMyGamesApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.theme.AppTheme
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlin.random.Random

const val seeAllMyGamesRoute = "seeMoreMine/{title}"

fun buildSeeAllMyGamesRoute(
  title: String,
) = "seeMoreMine/$title"

fun NavGraphBuilder.seeAllMyGamesScreen(navigateBack: () -> Unit) =
  animatedComposable(seeAllMyGamesRoute) {
    val title = it.arguments?.getString("title")!!

    val viewModel = hiltViewModel<SeeAllMyGamesViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    SeeAllMyGamesViewContent(
      uiState = uiState,
      title = title,
      navigateBack = {
        navigateBack()
      },
      openApp = { packageName ->
        viewModel.openApp(packageName)
      },
    )
  }

@Composable
fun SeeAllMyGamesViewContent(
  uiState: SeeAllMyGamesUiState,
  title: String,
  navigateBack: () -> Unit,
  openApp: (String) -> Unit,
) {
  val localContext = LocalContext.current
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = title
    )
    when (uiState) {
      is SeeAllMyGamesUiState.Loading -> LoadingView()
      is SeeAllMyGamesUiState.AppsList -> AppsList(
        size = uiState.installedAppsList.size,
      ) {
        itemsIndexed(
          items = uiState.installedAppsList,
          key = { _, it -> it.packageName }
        ) { index, app ->
          val icon = remember(
            key1 = app.packageName,
            key2 = localContext
          ) {
            localContext.getAppIconDrawable(app.packageName)!!
          }
          AppItem(
            name = app.name,
            icon = icon,
            version = app.versionName,
          ) {
            OpenAppButton(
              openApp = openApp,
              packageName = app.packageName
            )
          }
        }
      }
    }
  }
}

@Composable
fun AppsList(
  size: Int,
  content: LazyListScope.() -> Unit,
) {
  Spacer(modifier = Modifier.fillMaxWidth())
  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(size, 1) }
      .padding(start = 16.dp, end = 16.dp)
      .wrapContentSize(Alignment.TopCenter)
  ) {
    content()
  }
}

@Composable
fun AppItem(
  name: String,
  icon: Drawable,
  version: String?,
  actionView: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .fillMaxWidth()
      .defaultMinSize(minHeight = 96.dp)
      .padding(top = 16.dp, bottom = 16.dp)
  ) {
    AptoideAsyncImage(
      modifier = Modifier
        .padding(end = 16.dp)
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp)),
      data = icon,
      contentDescription = null
    )
    Column(
      modifier = Modifier
        .wrapContentHeight()
        .width(176.dp)
        .weight(1f)
    ) {
      Text(
        text = name,
        style = AppTheme.typography.gameTitleTextCondensedLarge,
        modifier = Modifier.padding(bottom = 4.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
      )
      version?.let {
        Text(
          text = it,
          style = AppTheme.typography.gameTitleTextCondensedSmall,
          color = AppTheme.colors.moreAppsViewDownloadsTextColor,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1
        )
      }
    }
    actionView()
  }
}

@Composable
fun OpenAppButton(
  openApp: (String) -> Unit,
  packageName: String,
) {
  Button(
    onClick = { openApp(packageName) },
    shape = RoundedCornerShape(30.dp),
    modifier = Modifier
      .padding(top = 8.dp, bottom = 8.dp)
      .height(32.dp)
      .width(72.dp),
    elevation = ButtonDefaults.elevation(defaultElevation = 0.dp),
    contentPadding = PaddingValues(),
    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.openAppButtonColor)
  ) {
    Text(
      text = stringResource(R.string.button_open_app_title),
      maxLines = 1,
      style = AppTheme.typography.buttonTextMedium,
      color = Color.White
    )
  }
}

@PreviewAll
@Composable
fun SeeAllMyGamesPreview(
  @PreviewParameter(SeeAllMyGamesUiStateProvider::class) state: SeeAllMyGamesUiState,
) {
  AptoideTheme {
    SeeAllMyGamesViewContent(
      uiState = state,
      title = "My games",
      navigateBack = {},
      openApp = {},
    )
  }
}

class SeeAllMyGamesUiStateProvider : PreviewParameterProvider<SeeAllMyGamesUiState> {
  override val values: Sequence<SeeAllMyGamesUiState> = sequenceOf(
    SeeAllMyGamesUiState.Loading,
    SeeAllMyGamesUiState.AppsList(List(Random.nextInt(15)) { randomMyGamesApp }),
  )
}
