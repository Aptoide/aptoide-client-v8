package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_apps.data.App
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun AppViewScreen(appViewViewModel: AppViewViewModel = hiltViewModel()) {

  val uiState by appViewViewModel.uiState.collectAsState()

  MainAppViewView(uiState)


}

@Composable
fun MainAppViewView(uiState: AppViewUiState) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .background(color = Color.White)
  ) {
    if (!uiState.isLoading) {
      uiState.app?.let { AppViewContent(uiState = uiState, app = it) }
    }
  }
}

@Composable
fun AppViewContent(uiState: AppViewUiState, app: App) {
  Image(
    painter = rememberImagePainter(app.featureGraphic,
      builder = {
        placeholder(cm.aptoide.pt.feature_apps.R.drawable.ic_placeholder)
        transformations(RoundedCornersTransformation())
      }),
    contentDescription = "App Feature Graphic",
    modifier = Modifier
      .fillMaxWidth()
      .height(181.dp)
      .padding(bottom = 8.dp)
  )
}
