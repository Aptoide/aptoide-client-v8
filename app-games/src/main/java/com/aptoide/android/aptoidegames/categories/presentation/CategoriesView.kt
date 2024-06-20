package com.aptoide.android.aptoidegames.categories.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.feature_categories.domain.Category
import cm.aptoide.pt.feature_categories.presentation.rememberCategoriesState
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.feature_apps.presentation.SmallEmptyView
import com.aptoide.android.aptoidegames.home.BundleHeader
import com.aptoide.android.aptoidegames.home.LoadingBundleView
import com.aptoide.android.aptoidegames.home.analytics.meta
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun CategoriesBundle(
  bundle: Bundle,
  navigate: (String) -> Unit,
) = bundle.view?.let {
  val uiState = rememberCategoriesState(requestUrl = it)

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .wrapContentHeight()
      .padding(bottom = 16.dp)
  ) {
    BundleHeader(
      title = bundle.title,
      icon = bundle.bundleIcon,
      hasMoreAction = bundle.hasMoreAction,
      onClick = {
        navigate(
          buildAllCategoriesRoute(bundle.title)
            .withBundleMeta(bundle.meta.copy(tag = "${bundle.tag}-more"))
        )
      }
    )
    CategoriesListView(
      loading = uiState.loading,
      categories = uiState.categories,
      navigate = navigate
    )
  }
}

@Composable
fun CategoriesListView(
  loading: Boolean,
  categories: List<Category>,
  navigate: (String) -> Unit,
) {
  val lazyListState = rememberLazyListState()

  if (loading) {
    LoadingBundleView(height = 132.dp)
  } else if (categories.isEmpty()) {
    SmallEmptyView(modifier = Modifier.height(132.dp))
  } else {
    LazyRow(
      modifier = Modifier
        .semantics {
          collectionInfo = CollectionInfo(1, categories.size)
        }
        .fillMaxWidth()
        .wrapContentHeight(),
      state = lazyListState,
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      itemsIndexed(categories) { index, category ->
        CategoryGridView(
          title = category.title,
          icon = category.icon,
          onClick = {
            navigate(
              buildCategoryDetailRoute(category.title, category.name).withItemPosition(index)
            )
          }
        )
      }
    }
  }
}

@Composable
fun CategoryGridView(
  title: String,
  icon: String?,
  onClick: () -> Unit = {},
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
      modifier = Modifier
        .padding(bottom = 8.dp)
        .size(88.dp)
        .background(color = Palette.Primary)
    ) {
      AptoideAsyncImage(
        modifier = Modifier
          .size(56.dp)
          .align(Alignment.Center),
        data = icon ?: R.drawable.category_default_icon,
        placeholder = false,
        contentDescription = null,
        colorFilter = ColorFilter.tint(Palette.Black)
      )
    }
    Text(
      text = title,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.defaultMinSize(minHeight = 36.dp),
      style = AGTypography.DescriptionGames
    )
  }
}

@PreviewDark
@Composable
fun CategoriesViewPreview() {
  CategoryGridView(title = "Action", icon = "", onClick = {})
}
