package cm.aptoide.pt.app_games.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.feature_home.domain.Type
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiState
import cm.aptoide.pt.feature_home.presentation.BundlesViewUiStateType.LOADING

@Composable
fun BundlesView(
  viewState: BundlesViewUiState,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.TopCenter)
  ) {
    if (viewState.type == LOADING) {
      CircularProgressIndicator()
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .wrapContentSize(Alignment.TopCenter)
          .padding(start = 16.dp),
      ) {
        items(viewState.bundles) {
          when (it.type) {
            Type.APP_GRID -> {}

            Type.FEATURE_GRAPHIC -> {}

            Type.ESKILLS -> {
            }

            Type.FEATURED_APPC -> {}

            Type.EDITORIAL -> {}

            else -> {}
          }
        }
      }
    }
  }
}
