package cm.aptoide.pt.home.updates

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun UpdatesScreen() {
  Column(modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)) {
    Text(text = "Updates")
  }
}