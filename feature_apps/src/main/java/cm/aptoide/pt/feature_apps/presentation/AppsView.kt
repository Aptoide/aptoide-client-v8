package cm.aptoide.pt.feature_apps.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun AppsScreen() {
  Column(modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)) {
    Text(text = "Apps")
  }
}