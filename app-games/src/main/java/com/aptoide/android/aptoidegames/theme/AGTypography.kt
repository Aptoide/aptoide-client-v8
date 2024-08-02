package com.aptoide.android.aptoidegames.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aptoide.android.aptoidegames.R

private val chakraPetchFontFamily = FontFamily(
  Font(R.font.chakrapetch_light, FontWeight.Light),
  Font(R.font.chakrapetch_lightitalic, FontWeight.Light, FontStyle.Italic),
  Font(R.font.chakrapetch_regular, FontWeight.Normal),
  Font(R.font.chakrapetch_italic, FontWeight.Normal, FontStyle.Italic),
  Font(R.font.chakrapetch_medium, FontWeight.Medium),
  Font(R.font.chakrapetch_mediumitalic, FontWeight.Medium, FontStyle.Italic),
  Font(R.font.chakrapetch_semibold, FontWeight.SemiBold),
  Font(R.font.chakrapetch_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
  Font(R.font.chakrapetch_bold, FontWeight.Bold),
  Font(R.font.chakrapetch_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

private val robotoFontFamily = FontFamily(
  Font(R.font.roboto_thin, FontWeight.Thin),
  Font(R.font.roboto_thin_italic, FontWeight.Thin, FontStyle.Italic),
  Font(R.font.roboto_light, FontWeight.Light),
  Font(R.font.roboto_light_italic, FontWeight.Light, FontStyle.Italic),
  Font(R.font.roboto_regular, FontWeight.Normal),
  Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
  Font(R.font.roboto_medium, FontWeight.Medium),
  Font(R.font.roboto_medium_italic, FontWeight.Medium, FontStyle.Italic),
  Font(R.font.roboto_bold, FontWeight.Bold),
  Font(R.font.roboto_bold_italic, FontWeight.Bold, FontStyle.Italic),
  Font(R.font.roboto_black, FontWeight.Black),
  Font(R.font.roboto_black_italic, FontWeight.Black, FontStyle.Italic)
)

object AGTypography {
  val Title = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp
  )
  val InputsL = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 20.sp
  )
  val InputsM = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 16.sp
  )
  val InputsS = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 14.sp
  )
  val ArticleText = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 24.sp
  )
  val Body = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = (15.6).sp
  )
  val BodyBold = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = (15.6).sp
  )
  val TitleGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 26.sp
  )
  val SubHeadingM = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    lineHeight = (23.4).sp
  )
  val SubHeadingS = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = (23.4).sp
  )
  val DescriptionGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 18.sp
  )
  val SmallGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp
  )
}
