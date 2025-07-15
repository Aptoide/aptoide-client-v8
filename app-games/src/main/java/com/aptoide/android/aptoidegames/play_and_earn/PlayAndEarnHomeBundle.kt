package com.aptoide.android.aptoidegames.play_and_earn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.randomApp
import com.aptoide.android.aptoidegames.R

@Composable
fun PlayAndEarnHomeBundle(
  apps: List<App>,
  modifier: Modifier = Modifier,
  navigate: (String) -> Unit
) {
  Box(
    modifier = modifier.wrapContentHeight()
  ) {
    Image(
      modifier = Modifier.matchParentSize(),
      painter = painterResource(R.drawable.play_and_earn_bg_2),
      contentDescription = null,
      contentScale = ContentScale.Crop
    )

    Column(
      modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      PlayAndEarnCTAHeader(onClick = { navigate(playAndEarnRewardsRoute) })
      PlayAndEarnLargeAppCarousel(
        apps = apps,
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp)
      )
    }
  }
}

@Composable
fun PlayAndEarnLargeAppCarousel(
  apps: List<App>,
  modifier: Modifier = Modifier,
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
    PlayAndEarnLargeAppItem(app = apps[index], onClick = {})
  }
}

@Preview
@Composable
fun PlayAndEarnBundlePreview() {
  PlayAndEarnHomeBundle(
    apps = List(10) { randomApp },
    navigate = {}
  )
}
