package cm.aptoide.pt.home.appcoins

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun BonusScreen() {
  Column(modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)) {
    Text(text = "Bonus")
  }
}