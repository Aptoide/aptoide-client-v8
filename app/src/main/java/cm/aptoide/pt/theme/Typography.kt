
package cm.aptoide.pt.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DarkTypography = Typography(
  h1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = Color.White,
    fontSize = 28.sp
  ),
  h2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = Color.White,
    fontSize = 21.sp
  ),
  body1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    color = Color.White,
    fontSize = 14.sp
  ),
  body2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    color = white87,
    fontSize = 14.sp
  )
)

val LightTypography = Typography(
  h1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = background900,
    fontSize = 28.sp
  ),
  h2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = background900,
    fontSize = 21.sp
  ),
  body1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    color = background800,
    fontSize = 14.sp
  ),
  body2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    color = background800,
    fontSize = 14.sp
  )
)
