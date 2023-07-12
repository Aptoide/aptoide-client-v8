package cm.aptoide.pt.aptoide_ui.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.theme.grey

@Preview
@Composable
fun TestViewsIcon() {
  Image(
    imageVector = getViewsIcon(grey),
    contentDescription = null,
    modifier = Modifier.size(240.dp)
  )
}

fun getViewsIcon(color: Color): ImageVector = ImageVector.Builder(
  name = "ViewsIcon",
  defaultWidth = 14.dp,
  defaultHeight = 8.dp,
  viewportWidth = 14f,
  viewportHeight = 8f,
).apply {
  path(fill = SolidColor(color)) {
    moveTo(6.99996f, 0f)
    curveTo(5.64634f, 0.0446123f, 4.32108f, 0.39965f, 3.12645f, 1.03771f)
    curveTo(1.93182f, 1.67577f, 0.899768f, 2.57979f, 0.109956f, 3.68f)
    curveTo(0.0369049f, 3.77065f, -0.00292969f, 3.88358f, -0.00292969f, 4f)
    curveTo(-0.00292969f, 4.11643f, 0.0369049f, 4.22935f, 0.109956f, 4.32f)
    curveTo(0.899768f, 5.42021f, 1.93182f, 6.32423f, 3.12645f, 6.96229f)
    curveTo(4.32108f, 7.60035f, 5.64634f, 7.95539f, 6.99996f, 8f)
    curveTo(8.35357f, 7.95539f, 9.67883f, 7.60035f, 10.8735f, 6.96229f)
    curveTo(12.0681f, 6.32423f, 13.1001f, 5.42021f, 13.89f, 4.32f)
    curveTo(13.963f, 4.22935f, 14.0028f, 4.11643f, 14.0028f, 4f)
    curveTo(14.0028f, 3.88358f, 13.963f, 3.77065f, 13.89f, 3.68f)
    curveTo(13.1001f, 2.57979f, 12.0681f, 1.67577f, 10.8735f, 1.03771f)
    curveTo(9.67883f, 0.39965f, 8.35357f, 0.0446123f, 6.99996f, 0f)
    verticalLineTo(0f)
    close()
    moveTo(7.18996f, 6.82f)
    curveTo(6.42831f, 6.88106f, 5.67334f, 6.63906f, 5.08912f, 6.1466f)
    curveTo(4.50491f, 5.65413f, 4.13865f, 4.951f, 4.06996f, 4.19f)
    curveTo(4.06996f, 4.06f, 4.06996f, 3.94f, 4.06996f, 3.82f)
    curveTo(4.13172f, 3.12358f, 4.443f, 2.47292f, 4.94647f, 1.98782f)
    curveTo(5.44995f, 1.50272f, 6.11172f, 1.21584f, 6.80996f, 1.18f)
    curveTo(7.5716f, 1.11894f, 8.32657f, 1.36094f, 8.91079f, 1.8534f)
    curveTo(9.49501f, 2.34587f, 9.86126f, 3.049f, 9.92996f, 3.81f)
    curveTo(9.92996f, 3.94f, 9.92996f, 4.06f, 9.92996f, 4.18f)
    curveTo(9.86819f, 4.87642f, 9.55692f, 5.52708f, 9.05344f, 6.01218f)
    curveTo(8.54996f, 6.49728f, 7.88819f, 6.78416f, 7.18996f, 6.82f)
    close()
    moveTo(7.09996f, 5.52f)
    curveTo(6.69072f, 5.55336f, 6.28484f, 5.42403f, 5.97034f, 5.16008f)
    curveTo(5.65584f, 4.89612f, 5.45808f, 4.51882f, 5.41996f, 4.11f)
    verticalLineTo(3.9f)
    curveTo(5.46372f, 3.51159f, 5.64903f, 3.15292f, 5.94047f, 2.89247f)
    curveTo(6.23192f, 2.63202f, 6.60909f, 2.48804f, 6.99996f, 2.48804f)
    curveTo(7.39082f, 2.48804f, 7.76799f, 2.63202f, 8.05944f, 2.89247f)
    curveTo(8.35089f, 3.15292f, 8.53619f, 3.51159f, 8.57996f, 3.9f)
    curveTo(8.58994f, 3.96964f, 8.58994f, 4.04036f, 8.57996f, 4.11f)
    curveTo(8.54402f, 4.48278f, 8.37433f, 4.82994f, 8.10227f, 5.0873f)
    curveTo(7.8302f, 5.34466f, 7.47415f, 5.49481f, 7.09996f, 5.51f)
    verticalLineTo(5.52f)
    close()
  }
}.build()
