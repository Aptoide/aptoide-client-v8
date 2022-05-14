package cm.aptoide.pt.feature_appview.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
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
  ) {
    if (!uiState.isLoading) {
      uiState.app?.let { AppViewContent(uiState = uiState, app = it) }
    }
  }
}

@Composable
fun AppViewContent(uiState: AppViewUiState, app: App) {
  Column(modifier = Modifier.fillMaxSize()) {
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
    Box(modifier = Modifier.padding(start = 16.dp, top = 19.dp, bottom = 19.dp, end = 16.dp)) {
      Column {
        AppPresentationView(app)
      }
    }
  }
}

@Composable
fun AppPresentationView(app: App) {
  Row(modifier = Modifier.height(88.dp)) {
    Image(
      painter = rememberImagePainter(app.icon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }), contentDescription = "App icon",
      modifier = Modifier
        .size(88.dp, 88.dp)
        .padding(end = 16.dp)
    )
    Column(
      modifier = Modifier.height(88.dp),
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Text(
        text = app.name,
        maxLines = 1,
        fontSize = MaterialTheme.typography.subtitle2.fontSize,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
          .padding(top = 12.dp)
      )
      if (app.malware == "TRUSTED") {
        Row {
          /*  Image(
          painter = painterResource(id = R.drawable.ic_trusted_app),
          contentDescription = "Trusted icon",
          modifier = Modifier
            .size(10.dp, 13.dp)
            .wrapContentHeight(Alignment.CenterVertically)
        )*/
          Text(
            text = "Trusted",
            color = Color.Green,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = MaterialTheme.typography.caption.fontSize
          )
        }
      }
    }

  }
}

