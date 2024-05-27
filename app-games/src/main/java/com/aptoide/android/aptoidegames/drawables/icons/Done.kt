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
import cm.aptoide.pt.aptoide_ui.theme.green

@Preview
@Composable
fun TestDoneIcon() {
  Image(
    imageVector = getDoneIcon(),
    contentDescription = null,
    modifier = Modifier.size(64.dp)
  )
}

fun getDoneIcon(): ImageVector =
  ImageVector.Builder(
    name = "DoneIcon",
    defaultWidth = 64.dp,
    defaultHeight = 64.dp,
    viewportWidth = 64f,
    viewportHeight = 64f,
  ).apply {
    path(fill = SolidColor(Color(0xFF25B47E))) {
      moveTo(27.329f, 46.707f)
      lineTo(49.64f, 24.396f)
      lineTo(45.24f, 19.995f)
      lineTo(27.329f, 37.906f)
      lineTo(18.451f, 29.028f)
      lineTo(14.128f, 33.428f)
      lineTo(27.329f, 46.707f)
      close()
      moveTo(31.961f, 64f)
      curveTo(27.587f, 64f, 23.456f, 63.164f, 19.571f, 61.491f)
      curveTo(15.685f, 59.818f, 12.288f, 57.528f, 9.38f, 54.62f)
      curveTo(6.472f, 51.712f, 4.182f, 48.328f, 2.509f, 44.468f)
      curveTo(0.836f, 40.608f, 0f, 36.465f, 0f, 32.039f)
      curveTo(0f, 27.612f, 0.836f, 23.456f, 2.509f, 19.571f)
      curveTo(4.182f, 15.685f, 6.459f, 12.301f, 9.341f, 9.419f)
      curveTo(12.224f, 6.536f, 15.608f, 4.246f, 19.493f, 2.548f)
      curveTo(23.379f, 0.849f, 27.535f, 0f, 31.961f, 0f)
      curveTo(36.388f, 0f, 40.556f, 0.849f, 44.468f, 2.548f)
      curveTo(48.38f, 4.246f, 51.764f, 6.524f, 54.62f, 9.38f)
      curveTo(57.477f, 12.236f, 59.754f, 15.608f, 61.452f, 19.493f)
      curveTo(63.151f, 23.379f, 64f, 27.561f, 64f, 32.039f)
      curveTo(64f, 36.465f, 63.151f, 40.621f, 61.452f, 44.507f)
      curveTo(59.754f, 48.392f, 57.464f, 51.776f, 54.581f, 54.659f)
      curveTo(51.699f, 57.541f, 48.328f, 59.818f, 44.468f, 61.491f)
      curveTo(40.608f, 63.164f, 36.439f, 64f, 31.961f, 64f)
      close()
      moveTo(31.961f, 57.901f)
      curveTo(39.167f, 57.901f, 45.279f, 55.379f, 50.297f, 50.335f)
      curveTo(55.315f, 45.292f, 57.824f, 39.193f, 57.824f, 32.039f)
      curveTo(57.824f, 24.833f, 55.315f, 18.721f, 50.297f, 13.703f)
      curveTo(45.279f, 8.685f, 39.167f, 6.176f, 31.961f, 6.176f)
      curveTo(24.807f, 6.176f, 18.708f, 8.685f, 13.665f, 13.703f)
      curveTo(8.621f, 18.721f, 6.099f, 24.833f, 6.099f, 32.039f)
      curveTo(6.099f, 39.193f, 8.621f, 45.292f, 13.665f, 50.335f)
      curveTo(18.708f, 55.379f, 24.807f, 57.901f, 31.961f, 57.901f)
      close()
    }

  }.build()
