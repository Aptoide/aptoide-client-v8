package cm.aptoide.pt.feature_report_app.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_report_app.domain.ReportApp
import cm.aptoide.pt.feature_report_app.R
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun ReportAppScreen(reportAppViewModel: ReportAppViewModel = hiltViewModel()) {

  val uiState by reportAppViewModel.uiState.collectAsState()

  MainReportAppView(uiState = uiState)
}

@Composable
fun MainReportAppView(uiState: ReportAppUiState) {

  Column(modifier = Modifier.padding(16.dp, 27.dp, 16.dp, 32.dp)) {
    AppInfoRow(uiState.app)
    ReportOptionsList(uiState.reportAppOptionsList)
    ReportAdditionalInformation()
    SubmitButton()
  }
}

@Composable
fun SubmitButton() {
  TODO("Not yet implemented")
}

@Composable
fun ReportAdditionalInformation() {


}

@Composable
fun AppInfoRow(app: ReportApp) {
  Row(
    modifier = Modifier
      .padding(16.dp, 28.dp, 16.dp, 32.dp)
      .height(80.dp)
  ) {
    Image(
      painter = rememberImagePainter(app.appIcon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }), contentDescription = "App icon",
      modifier = Modifier
        .size(64.dp, 64.dp)
        .padding(end = 8.dp)
    )
    Column(modifier = Modifier.width(200.dp)) {
      Text(
        text = app.appName,
        maxLines = 1,
        fontSize = MaterialTheme.typography.subtitle2.fontSize,
        overflow = TextOverflow.Ellipsis
      )
      Text(
        text = "Version: " + app.versionName,
        maxLines = 1,
        fontSize = MaterialTheme.typography.overline.fontSize
      )
    }
    if (app.malwareStatus == "TRUSTED") {
      MalwareBadgeView()
    }
  }
}

@Composable
fun MalwareBadgeView() {
  Row {
    Text(
      text = "Trusted",
      color = Color.Green,
      modifier = Modifier.padding(end = 6.dp),
      fontSize = MaterialTheme.typography.caption.fontSize
    )
    Image(
      painter = painterResource(id = R.drawable.ic_trusted_app),
      contentDescription = "Trusted icon",
      modifier = Modifier
        .size(10.dp, 13.dp)
        .wrapContentHeight(Alignment.CenterVertically)
    )
  }
}

@Composable
fun ReportOptionsList(reportOptionsList: List<String>) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    items(reportOptionsList) { reportOption ->
      ReportOptionsItem(reportOption)
    }
  }
}

@Composable
fun ReportOptionsItem(reportOption: String) {

  Card(
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier.size(344.dp, 48.dp)
  ) {
    Column(modifier = Modifier.clickable(onClick = { TODO("Missing implementation") })){

    Text(
      text = reportOption,
      color = Color.Black,
      modifier = Modifier.padding(end = 6.dp),
      fontSize = MaterialTheme.typography.caption.fontSize
    )
    }
  }

}






