package cm.aptoide.pt.feature_report_app.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.aptoide_ui.R
import cm.aptoide.pt.aptoide_ui.theme.AptoideTheme
import cm.aptoide.pt.feature_report_app.domain.ReportApp
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation

@Preview
@Composable
fun ReportAppScreen(
  reportAppViewModel: ReportAppViewModel = hiltViewModel(),
  appName: String? = null,
  appIcon: String? = null,
  versionName: String? = null,
  malwareRank: String? = null
) {

  val uiState by reportAppViewModel.uiState.collectAsState()

  AptoideTheme {
    MainReportAppView(
      uiState = uiState,
      onSubmitReport = { reportAppViewModel.submitReport() },
      onAdditionalInfoChanged = {
        reportAppViewModel.onAdditionalInfoChanged(it)
      }, onSelectReportOption = { reportAppViewModel.onSelectReportOption(it) })
  }
}

@Composable
fun MainReportAppView(
  uiState: ReportAppUiState,
  onSubmitReport: () -> Unit,
  onAdditionalInfoChanged: (String) -> Unit, onSelectReportOption: (ReportOption) -> Unit
) {

  LazyColumn(
    modifier = Modifier
      .padding(start = 16.dp, end = 16.dp)
  ) {
    item { AppInfoRow(uiState.app) }

    items(uiState.reportAppOptionsList) { reportOption ->
      if (reportOption.isSelected) {
        ReportOptionsItemChecked(
          reportOption = reportOption,
          onSelectReportOption = onSelectReportOption
        )
      } else {
        ReportOptionsItem(reportOption, onSelectReportOption)
      }
    }

    item {
      ReportAdditionalInformation(uiState.additionalInfo, onAdditionalInfoChanged)
    }
    item {
      SubmitButton(onSubmitReport)
    }
  }
}

@Composable
fun SubmitButton(onSubmitReport: () -> Unit) {
  Button(
    onClick = { onSubmitReport() },
    shape = CircleShape,
    modifier = Modifier
      .padding(bottom = 24.dp)
      .height(56.dp)
      .fillMaxWidth()
  ) {
    Text("Submit", maxLines = 1)
  }
}

@Composable
fun ReportAdditionalInformation(
  additionalInfo: String,
  onAdditionalInfoChanged: (String) -> Unit
) {
  OutlinedTextField(
    modifier = Modifier
      .padding(top = 12.dp, bottom = 16.dp)
      .fillMaxWidth()
      .height(160.dp)
      .defaultMinSize(minHeight = 40.dp),
    shape = RoundedCornerShape(16.dp),
    value = additionalInfo,
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
      .padding(bottom = 32.dp, top = 24.dp)
      .height(80.dp)
  ) {
    Image(
      painter = rememberImagePainter(app.appIcon,
        builder = {
          transformations(RoundedCornersTransformation(16f))
        }), contentDescription = "App icon",
      modifier = Modifier
        .size(80.dp, 80.dp), contentScale = ContentScale.Inside
    )
    Column(
      modifier = Modifier
        .wrapContentWidth()
        .padding(start = 16.dp)
    ) {
      app.appName?.let {
        Text(
          text = it,
          maxLines = 1,
          fontSize = MaterialTheme.typography.h6.fontSize,
          overflow = TextOverflow.Ellipsis
        )
      }
      Text(
        buildAnnotatedString {
          append("Version ")
          withStyle(style = SpanStyle(color = MaterialTheme.colors.primary)) {
            app.versionName?.let { append(it) }
          }
        },
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        fontSize = MaterialTheme.typography.subtitle2.fontSize
      )
      if (app.malwareStatus == "TRUSTED") {
        MalwareBadgeView()
      }
    }

  }
}

@Composable
fun MalwareBadgeView() {
  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
    Image(
      painter = painterResource(id = R.drawable.ic_icon_trusted),
      contentDescription = "Trusted icon",
      modifier = Modifier
        .size(16.dp, 16.dp)
        .wrapContentHeight(Alignment.CenterVertically)
    )
    Text(
      text = "Trusted",
      color = Color.Green,
      modifier = Modifier.padding(start = 8.dp),
      fontSize = MaterialTheme.typography.caption.fontSize
    )
  }
}

@Composable
fun ReportOptionsItem(reportOption: ReportOption, onSelectReportOption: (ReportOption) -> Unit) {
  Card(
    modifier = Modifier
      .padding(bottom = 20.dp)
      .fillMaxWidth()
      .height(48.dp)
      .clickable(onClick = { onSelectReportOption(reportOption) }),
  ) {
    Text(
      text = reportOption.option,
      color = Color.White,
      modifier = Modifier.padding(start = 18.dp, top = 14.dp, bottom = 14.dp),
      overflow = Ellipsis,
      fontSize = MaterialTheme.typography.subtitle2.fontSize
    )
  }
}

@Composable
fun ReportOptionsItemChecked(
  reportOption: ReportOption,
  onSelectReportOption: (ReportOption) -> Unit
) {
  Card(
    border = BorderStroke(1.dp, MaterialTheme.colors.primary),
    shape = MaterialTheme.shapes.medium,
    modifier = Modifier
      .padding(bottom = 20.dp)
      .fillMaxWidth()
      .height(48.dp)
  ) {
    Row(
      modifier = Modifier
        .clickable(onClick = { onSelectReportOption(reportOption) }),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = reportOption.option,
        color = MaterialTheme.colors.primary,
        overflow = Ellipsis,
        modifier = Modifier
          .padding(start = 17.dp, end = 6.dp)
          .weight(1f),
        fontSize = MaterialTheme.typography.subtitle2.fontSize
      )
      Image(
        painter = rememberImagePainter(R.drawable.ic_check),
        contentDescription = "Check icon",
        modifier = Modifier
          .padding(start = 18.dp, end = 18.dp)
          .size(16.dp, 16.dp),
        contentScale = ContentScale.Inside,
        alignment = Alignment.BottomEnd
      )
    }
  }
}






