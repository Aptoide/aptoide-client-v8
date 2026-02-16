package com.aptoide.android.aptoidegames.play_and_earn.presentation.layout

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.campaigns.domain.PaEApp

@Composable
fun PaEHorizontalCarousel(
  apps: List<PaEApp>,
  modifier: Modifier = Modifier,
  pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
  val pagerState = rememberPagerState(
    initialPage = 0,
    pageCount = { apps.size }
  )

  HorizontalPager(
    state = pagerState,
    modifier = modifier.semantics {
      collectionInfo = CollectionInfo(1, apps.size)
    },
    pageSpacing = 16.dp,
    pageSize = PageSize.Fixed(280.dp)
  ) { index ->
    pageContent(index)
  }
}
