package com.aptoide.android.aptoidegames.feature_apps.presentation

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.animatedComposable
import cm.aptoide.pt.extensions.getAppIconDrawable
import cm.aptoide.pt.feature_apps.data.randomMyGamesApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimarySmallButton
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
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
        items(
          items = uiState.installedAppsList,
          key = { it.packageName }
        ) {
          val icon = remember(
            key1 = it.packageName,
            key2 = localContext
          ) {
            localContext.getAppIconDrawable(it.packageName)
          }
          AppItem(
            name = it.name,
            icon = icon,
            version = it.versionName,
          ) {
            PrimarySmallButton(
              onClick = { openApp(it.packageName) },
              title = stringResource(R.string.button_open_app_title),
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
  icon: Drawable?,
  version: String?,
  actionView: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .fillMaxWidth()
      .defaultMinSize(minHeight = 96.dp)
      .padding(top = 16.dp, bottom = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    AptoideAsyncImage(
      modifier = Modifier.size(64.dp),
      data = icon,
      contentDescription = null
    )
    Column(
      modifier = Modifier
        .padding(start = 16.dp, end = 16.dp)
        .weight(1f),
      verticalArrangement = Arrangement.SpaceEvenly,
    ) {
      Text(
        text = name,
        style = AGTypography.DescriptionGames,
        color = Palette.White,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2
      )
      version?.let {
        Text(
          text = it,
          style = AGTypography.InputsS,
          color = Palette.GreyLight,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1
        )
      }
    }
    actionView()
  }
}

@PreviewDark
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
