package com.aptoide.android.aptoidegames.feature_oos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cm.aptoide.pt.aptoide_ui.textformatter.TextFormatter
import cm.aptoide.pt.aptoide_ui.textformatter.getMultiStyleString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_oos.presentation.InstalledAppsUiState
import cm.aptoide.pt.feature_oos.presentation.rememberAvailableSpaceState
import cm.aptoide.pt.feature_oos.presentation.rememberInstalledAppsListState
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryButton
import com.aptoide.android.aptoidegames.design_system.SecondaryButton
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun OutOfSpaceDialog(
  app: App,
  onDismiss: () -> Unit,
) {
  val requiredSpace = rememberAvailableSpaceState(app = app)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false
    ),
  ) {
    Box(
      modifier = Modifier
        .padding(horizontal = 16.dp, vertical = 24.dp)
        .fillMaxWidth()
        .defaultMinSize(minHeight = 520.dp)
        .background(Palette.GreyDark),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(
          start = 16.dp,
          top = 31.dp,
          end = 16.dp,
        )
      ) {
        Title()
        Message(requiredSpace)
        OutOfSpaceAppsList(app.packageName)
      }
      val buttonModifier = Modifier
        .align(Alignment.BottomCenter)
        .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
        .fillMaxWidth()
      if (requiredSpace > 0) {
        SecondaryButton(
          onClick = onDismiss,
          modifier = buttonModifier,
          enabled = true,
          title = stringResource(R.string.go_back_button)
        )
      } else {
        PrimaryButton(
          onClick = onDismiss,
          modifier = buttonModifier,
          enabled = true,
          title = stringResource(R.string.go_back_button)
        )
      }
    }
  }
}

@Suppress("MoveVariableDeclarationIntoWhen")
@Composable fun OutOfSpaceAppsList(packageName: String) {
  val installedAppsListState = rememberInstalledAppsListState(packageName)
  when (installedAppsListState) {
    InstalledAppsUiState.Loading -> SkeletonList()
    is InstalledAppsUiState.Idle -> AppsList(installedAppsListState.apps)
  }
}

@Composable private fun AppsList(apps: List<String>) {
  LazyColumn {
    items(apps) {
      OutOfSpaceAppItem(packageName = it)
    }
    item {
      Spacer(modifier = Modifier.padding(36.dp))
    }
  }
}

@Composable private fun SkeletonList() {
  Column {
    SkeletonView()
    SkeletonView()
    SkeletonView()
    SkeletonView()
    SkeletonView()
  }
}

@Composable
private fun SkeletonView() {
  Row(
    modifier = Modifier
      .height(80.dp)
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(Palette.Grey)
        .padding(vertical = 8.dp)
    )
    Column(
      modifier = Modifier.padding(start = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .width(96.dp)
          .height(8.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Palette.Grey)
      )
      Box(
        modifier = Modifier
          .width(52.dp)
          .height(8.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Palette.Grey)
      )
    }
  }
}

@Composable fun Message(requiredSpace: Long) {
  if (requiredSpace > 0) {
    Text(
      text = getMultiStyleString(
        string = R.string.out_of_space_body,
        placeholder = TextFormatter.formatBytes(requiredSpace),
        style = SpanStyle(
          fontWeight = FontWeight.Bold
        )
      ),
      textAlign = TextAlign.Center,
      style = AGTypography.SubHeadingS,
      modifier = Modifier.padding(bottom = 8.dp)
    )
  } else {
    Text(
      text = stringResource(R.string.out_of_space_enough_body),
      style = AGTypography.SubHeadingS,
      modifier = Modifier.padding(bottom = 32.dp)
    )
  }
}

@Composable fun Title() {
  Text(
    text = stringResource(id = R.string.out_of_space_title),
    style = AGTypography.Title,
    modifier = Modifier.padding(bottom = 8.dp),
  )
}
