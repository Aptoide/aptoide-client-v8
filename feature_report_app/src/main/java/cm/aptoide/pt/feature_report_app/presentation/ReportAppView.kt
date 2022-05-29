package cm.aptoide.pt.feature_report_app.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.feature_report_app.domain.ReportApp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun ReportAppScreen(reportAppViewModel: ReportAppViewModel = hiltViewModel()) {

  val uiState by reportAppViewModel.uiState.collectAsState()

  MainReportAppView(
    uiState = uiState,
    onSubmitReport = { reportAppViewModel.submitReport() },
    onAdditionalInfoChanged = {
      reportAppViewModel.onAdditionalInfoChanged(it)
    }, onSelectReportOption = { reportAppViewModel.onSelectReportOption(it) })
}

@Composable
fun MainReportAppView(
  uiState: ReportAppUiState,
  onSubmitReport: () -> Unit,
  onAdditionalInfoChanged: (String) -> Unit, onSelectReportOption: (ReportOption) -> Unit
) {

  Column(
    modifier = Modifier
      .padding(16.dp, 27.dp, 16.dp, 32.dp)
    //.verticalScroll(rememberScrollState())
    //compose bug does not allow nested scrolling
  ) {
    AppInfoRow(uiState.app)
    ReportOptionsList(uiState.reportAppOptionsList, onSelectReportOption)
    ReportAdditionalInformation(uiState.additionalInfo, onAdditionalInfoChanged)
    SubmitButton(onSubmitReport)
  }
}

@Composable
fun SubmitButton(onSubmitReport: () -> Unit) {
  Button(
    onClick = { onSubmitReport() },
    shape = CircleShape,
    modifier = Modifier
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("Submit", maxLines = 1)
  }
}

@Composable
fun ReportAdditionalInformation(
  aditionalInfo: String,
  onAdditionalInfoChanged: (String) -> Unit
) {
  OutlinedTextField(
    modifier = Modifier
      .fillMaxWidth()
      .defaultMinSize(minHeight = 40.dp),
    shape = RoundedCornerShape(16.dp),
    value = aditionalInfo,
    onValueChange = {
      onAdditionalInfoChanged(it)
    },
    placeholder = {
      Text(
        modifier = Modifier
          .alpha(ContentAlpha.medium)
          .wrapContentHeight(),
        text = "Additional information (optional)",
        fontSize = MaterialTheme.typography.button.fontSize
      )
    },
    textStyle = TextStyle(fontSize = MaterialTheme.typography.body2.fontSize)
  )

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
    /* Image(
       painter = painterResource(id = R.drawable.ic_trusted_app),
       contentDescription = "Trusted icon",
       modifier = Modifier
         .size(10.dp, 13.dp)
         .wrapContentHeight(Alignment.CenterVertically)
     )*/
  }
}

@Composable
fun ReportOptionsList(
  reportOptionsList: List<ReportOption>,
  onSelectReportOption: (ReportOption) -> Unit
) {
  LazyColumn(
    modifier = Modifier.padding(top = 26.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp)
  ) {
    items(reportOptionsList) { reportOption ->
      ReportOptionsItem(reportOption, onSelectReportOption)
    }
  }
}

@Composable
fun ReportOptionsItem(reportOption: ReportOption, onSelectReportOption: (ReportOption) -> Unit) {
  Card(
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier.size(344.dp, 48.dp)
  ) {
    Column(
      modifier = Modifier.clickable(onClick = { onSelectReportOption(reportOption) }),
      verticalArrangement = Arrangement.Center
    ) {
      Text(
        text = reportOption.option,
        color = Color.White,
        modifier = Modifier.padding(start = 17.dp, end = 6.dp),
        fontSize = MaterialTheme.typography.caption.fontSize
      )
    }
  }

}






