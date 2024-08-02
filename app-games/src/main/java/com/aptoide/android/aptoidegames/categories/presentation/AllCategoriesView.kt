package com.aptoide.android.aptoidegames.categories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.feature_categories.domain.randomCategory
import cm.aptoide.pt.feature_categories.presentation.AllCategoriesUiState
import cm.aptoide.pt.feature_categories.presentation.AllCategoriesUiStateType
import cm.aptoide.pt.feature_categories.presentation.AllCategoriesViewModel
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.categories.presentation.CategoriesGridConstants.GRID_COLUMNS
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlin.random.Random

private const val titleArg = "title"
private const val allCategoriesRoute = "allCategories"
private val allCategoriesArguments = listOf(
  navArgument(titleArg) {
    type = NavType.StringType
    nullable = true
  },
)

fun allCategoriesScreen() = ScreenData.withAnalytics(
  route = "$allCategoriesRoute?$titleArg={$titleArg}",
  screenAnalyticsName = "SeeAll",
  arguments = allCategoriesArguments
) { arguments, navigate, navigateBack ->
  val categoriesBundleTitle = arguments?.getString(titleArg)
  val viewModel = hiltViewModel<AllCategoriesViewModel>()
  val uiState by viewModel.uiState.collectAsState()

  AllCategoriesView(
    title = categoriesBundleTitle,
    uiState = uiState,
    onError = viewModel::reload,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildAllCategoriesRoute(categoriesBundleTitle: String? = null) = when {
  categoriesBundleTitle.isNullOrEmpty() -> allCategoriesRoute
  else -> "$allCategoriesRoute?$titleArg=$categoriesBundleTitle"
}

@Composable
fun AllCategoriesView(
  title: String?,
  uiState: AllCategoriesUiState,
  onError: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()

  AllCategoriesViewContent {
    AppGamesTopBar(
      navigateBack = {
        genericAnalytics.sendBackButtonClick(analyticsContext)
        navigateBack()
      },
      title = title
    )
    when (uiState.type) {
      AllCategoriesUiStateType.LOADING -> LoadingView()
      AllCategoriesUiStateType.NO_CONNECTION -> NoConnectionView(onRetryClick = onError)
      AllCategoriesUiStateType.ERROR -> GenericErrorView(onError)
      AllCategoriesUiStateType.IDLE -> CategoryList(
        size = uiState.categoryList.size,
      ) {
        itemsIndexed(
          uiState.categoryList,
          key = { _, category -> category.name }
        ) { index, category ->
          CategoryLargeItem(
            title = category.title,
            icon = category.icon,
            onClick = {
              genericAnalytics.sendCategoryClick(category.name, analyticsContext)
              navigate(
                buildCategoryDetailRoute(category.title, category.name)
                  .withItemPosition(index)
              )
            }
          )
        }
      }
    }
  }
}

@Composable
fun AllCategoriesViewContent(
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    content()
  }
}

@Composable
fun CategoryList(
  size: Int,
  content: LazyGridScope.() -> Unit,
) {
  LazyVerticalGrid(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(size, GRID_COLUMNS) }
      .wrapContentSize(Alignment.TopCenter),
    columns = GridCells.Fixed(2),
    contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 28.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    content()
  }
}

@Composable
fun CategoryLargeItem(
  title: String,
  icon: String?,
  onClick: () -> Unit = {},
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .semantics(mergeDescendants = true) { }
      .clickable(onClick = onClick)
      .aspectRatio(160f / 184f)
      .background(color = Palette.Primary)
  ) {
    Spacer(modifier = Modifier.weight(1f))
    AptoideAsyncImage(
      modifier = Modifier
        .size(72.dp),
      data = icon ?: R.drawable.category_default_icon,
      placeholder = false,
      contentDescription = null,
      colorFilter = ColorFilter.tint(Palette.Black)
    )
    Text(
      modifier = Modifier
        .defaultMinSize(minHeight = 24.dp)
        .weight(1f)
        .padding(top = 16.dp),
      text = title,
      textAlign = TextAlign.Center,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      color = Palette.Black,
      style = AGTypography.InputsM
    )
  }
}

@PreviewDark
@Composable
fun LandscapePaymentView() {
  val uiStateFake =
    AllCategoriesUiState(List(Random.nextInt(until = 20)) {
      randomCategory
    }, AllCategoriesUiStateType.IDLE)
  AptoideTheme(darkTheme = false) {
    AllCategoriesView(
      title = "Categories",
      uiState = uiStateFake,
      onError = {},
      navigateBack = { },
      navigate = {}
    )
  }
}

private object CategoriesGridConstants {
  const val GRID_COLUMNS = 2
}
