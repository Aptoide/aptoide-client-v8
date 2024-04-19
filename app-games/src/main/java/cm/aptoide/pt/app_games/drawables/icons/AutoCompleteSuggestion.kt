package cm.aptoide.pt.app_games.drawables.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.app_games.theme.gray4

@Preview
@Composable
fun TestAutoCompleteSuggestion() {
  Image(
    imageVector = getAutoCompleteSuggestion(),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getAutoCompleteSuggestion(): ImageVector = ImageVector.Builder(
  name = "AutoCompleteSuggestion",
  defaultWidth = 18.dp,
  defaultHeight = 19.dp,
  viewportWidth = 18f,
  viewportHeight = 19f,
  tintColor = gray4,
).apply {
  path(
    fill = SolidColor(gray4),
  ) {
    moveTo(16.35f, 18.425f)
    lineTo(10.325f, 12.4f)
    curveTo(9.825f, 12.8333f, 9.24201f, 13.1708f, 8.57603f, 13.4125f)
    curveTo(7.91004f, 13.6541f, 7.20137f, 13.775f, 6.45f, 13.775f)
    curveTo(4.6473f, 13.775f, 3.12163f, 13.15f, 1.87298f, 11.9f)
    curveTo(0.624325f, 10.65f, 0f, 9.14164f, 0f, 7.37498f)
    curveTo(0f, 5.60831f, 0.625f, 4.09998f, 1.875f, 2.84998f)
    curveTo(3.125f, 1.59998f, 4.6375f, 0.974976f, 6.4125f, 0.974976f)
    curveTo(8.1875f, 0.974976f, 9.69583f, 1.59998f, 10.9375f, 2.84998f)
    curveTo(12.1792f, 4.09998f, 12.8f, 5.60956f, 12.8f, 7.37873f)
    curveTo(12.8f, 8.09289f, 12.6833f, 8.78331f, 12.45f, 9.44998f)
    curveTo(12.2167f, 10.1166f, 11.8667f, 10.7416f, 11.4f, 11.325f)
    lineTo(17.475f, 17.35f)
    curveTo(17.625f, 17.4907f, 17.7f, 17.6634f, 17.7f, 17.868f)
    curveTo(17.7f, 18.0727f, 17.6174f, 18.2576f, 17.4522f, 18.4228f)
    curveTo(17.3007f, 18.5743f, 17.1148f, 18.65f, 16.8945f, 18.65f)
    curveTo(16.6741f, 18.65f, 16.4926f, 18.575f, 16.35f, 18.425f)
    close()
    moveTo(6.425f, 12.275f)
    curveTo(7.77917f, 12.275f, 8.93021f, 11.7958f, 9.87813f, 10.8375f)
    curveTo(10.826f, 9.87914f, 11.3f, 8.72498f, 11.3f, 7.37498f)
    curveTo(11.3f, 6.02498f, 10.826f, 4.87081f, 9.87813f, 3.91248f)
    curveTo(8.93021f, 2.95414f, 7.77917f, 2.47498f, 6.425f, 2.47498f)
    curveTo(5.05695f, 2.47498f, 3.8941f, 2.95414f, 2.93645f, 3.91248f)
    curveTo(1.97882f, 4.87081f, 1.5f, 6.02498f, 1.5f, 7.37498f)
    curveTo(1.5f, 8.72498f, 1.97882f, 9.87914f, 2.93645f, 10.8375f)
    curveTo(3.8941f, 11.7958f, 5.05695f, 12.275f, 6.425f, 12.275f)
    close()
  }
}.build()
