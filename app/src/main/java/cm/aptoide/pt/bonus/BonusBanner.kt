package cm.aptoide.pt.bonus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BonusBanner() {
  Text(
    text = "up to\n20%\nBONUS",
    textAlign = TextAlign.Center,
    fontSize = 12.sp,
    color = MaterialTheme.colors.primary,
    modifier = Modifier
      .background(
        Color.White,
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp)
      )
      .size(64.dp)
      .padding(8.dp)
  )
}
