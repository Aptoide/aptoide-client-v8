package com.aptoide.android.aptoidegames.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.drawables.backgrounds.getSettingsDialogDarkBackground
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogoDev
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideLogo
import com.aptoide.android.aptoidegames.drawables.icons.getAsterisk
import com.aptoide.android.aptoidegames.drawables.icons.getAutoCompleteSuggestion
import com.aptoide.android.aptoidegames.drawables.icons.getBug
import com.aptoide.android.aptoidegames.drawables.icons.getCaretRight
import com.aptoide.android.aptoidegames.drawables.icons.getCheck
import com.aptoide.android.aptoidegames.drawables.icons.getCheckBox
import com.aptoide.android.aptoidegames.drawables.icons.getClose
import com.aptoide.android.aptoidegames.drawables.icons.getError
import com.aptoide.android.aptoidegames.drawables.icons.getErrorBug
import com.aptoide.android.aptoidegames.drawables.icons.getErrorOutlined
import com.aptoide.android.aptoidegames.drawables.icons.getGamepad
import com.aptoide.android.aptoidegames.drawables.icons.getGames
import com.aptoide.android.aptoidegames.drawables.icons.getGenericError
import com.aptoide.android.aptoidegames.drawables.icons.getGift
import com.aptoide.android.aptoidegames.drawables.icons.getHistoryOutlined
import com.aptoide.android.aptoidegames.drawables.icons.getLeftArrow
import com.aptoide.android.aptoidegames.drawables.icons.getLogout
import com.aptoide.android.aptoidegames.drawables.icons.getMoreVert
import com.aptoide.android.aptoidegames.drawables.icons.getMuted
import com.aptoide.android.aptoidegames.drawables.icons.getNoConnection
import com.aptoide.android.aptoidegames.drawables.icons.getNoConnectionSmall
import com.aptoide.android.aptoidegames.drawables.icons.getNoNetworkError
import com.aptoide.android.aptoidegames.drawables.icons.getNoWifi
import com.aptoide.android.aptoidegames.drawables.icons.getNotificationBell
import com.aptoide.android.aptoidegames.drawables.icons.getPlanetSearch
import com.aptoide.android.aptoidegames.drawables.icons.getSearch
import com.aptoide.android.aptoidegames.drawables.icons.getSingleGamepad
import com.aptoide.android.aptoidegames.drawables.icons.getUnmuted
import com.aptoide.android.aptoidegames.drawables.icons.getWifi
import com.aptoide.android.aptoidegames.drawables.icons.getWifiDialogIcon

private val darkMaterialColorPalette = darkColors(
  background = agBlack,
  onBackground = agWhite,
  primary = primary,
  secondary = secondary,
  onPrimary = agBlack,
  onSecondary = agWhite,
  surface = agBlack,
  onSurface = agWhite,
  error = error,
)

val darkColorPalette = AppColors(
  disabledButtonColor = gray3,
  disabledButtonTextColor = gray7,
  defaultButtonColor = primary,
  defaultButtonTextColor = agBlack,
  redButtonColor = pinkRed,
  redButtonTextColor = pureWhite,
  grayButtonColor = grey,
  grayButtonTextColor = agBlack,
  unselectedLabelColor = greyMedium,
  dividerColor = negro,
  moreAppsViewSeparatorColor = grey,
  moreAppsViewBackColor = gray5,
  moreAppsViewDownloadsTextColor = gray5,
  openAppButtonColor = pinkRed,
  installAppButtonColor = richOrange,
  editorialViewLabelColor = gray1,
  editorialViewTextLabelColor = pureBlack,
  materialColors = darkMaterialColorPalette,
  greyText = greyMedium,
  appCoinsColor = appCoins,
  downloadProgressBarBackgroundColor = grey,
  textFieldBackgroundColor = darkGray,
  textFieldBorderColor = pureWhite,
  textFieldPlaceholderTextColor = gray3,
  textFieldTextColor = pureWhite,
  placeholderColor = darkGray2,
  outOfSpaceDialogRequiredSpaceColor = richOrange,
  outOfSpaceDialogGoBackButtonColor = pureWhite,
  outOfSpaceDialogGoBackButtonTextColor = textBlack,
  outOfSpaceDialogGoBackButtonEnoughSpaceTextColor = pureWhite,
  outOfSpaceDialogGoBackButtonEnoughSpaceColor = richOrange,
  outOfSpaceDialogUninstallButtonColor = primary,
  outOfSpaceDialogAppNameColor = pureWhite,
  outOfSpaceDialogAppSizeColor = gray3,
  dialogBackgroundColor = greyDark,
  dialogTextColor = pureWhite,
  dialogDismissTextColor = gray3,
  searchBarTextColor = gray6,
  searchSuggestionHeaderTextColor = pureWhite,
  standardSecondaryTextColor = pureWhite,
  categoryBundleItemBackgroundColor = primary,
  categoryBundleItemIconTint = agBlack,
  categoryLargeItemTextColor = agBlack,
  switchOnStateColor = primary,
  switchOffStateColor = grey,
)

val lightColorPalette = darkColorPalette

private val dmSansFontFamily = FontFamily(
  Font(R.font.dmsans_regular, FontWeight.Normal),
  Font(R.font.dmsans_medium, FontWeight.Medium),
  Font(R.font.dmsans_mediumitalic, FontWeight.Medium, FontStyle.Italic),
  Font(R.font.dmsans_italic, FontWeight.Normal, FontStyle.Italic),
  Font(R.font.dmsans_bold, FontWeight.Bold),
  Font(R.font.dmsans_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

private val robotoCondensedFontFamily = FontFamily(
  Font(R.font.roboto_condensed_light, FontWeight.Light),
  Font(R.font.roboto_condensed_lightitalic, FontWeight.Light, FontStyle.Italic),
  Font(R.font.roboto_condensed_regular, FontWeight.Normal),
  Font(R.font.roboto_condensed_italic, FontWeight.Normal, FontStyle.Italic),
  Font(R.font.roboto_condensed_medium, FontWeight.Medium),
  Font(R.font.roboto_condensed_mediumitalic, FontWeight.Medium, FontStyle.Italic),
  Font(R.font.roboto_condensed_bold, FontWeight.Bold),
  Font(R.font.roboto_condensed_bolditalic, FontWeight.Bold, FontStyle.Italic)
)

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

val darkMaterialTypography = Typography(
  h1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = Color.White,
    fontSize = 21.sp
  ),
  h2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    color = Color.White,
    fontSize = 16.sp
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
    color = Color.White,
    fontSize = 14.sp
  )
)

val darkTypography = AppTypography(
  materialTypography = darkMaterialTypography,

  title = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 20.sp,
    lineHeight = 26.sp
  ),
  inputs_L = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 20.sp
  ),
  inputs_M = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 16.sp
  ),
  inputs_S = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 14.sp
  ),
  articleText = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 24.sp
  ),
  body = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 16.sp
  ),
  bodyBold = TextStyle(
    fontFamily = chakraPetchFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 12.sp,
    lineHeight = 16.sp
  ),
  titleGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 20.sp,
    lineHeight = 26.sp
  ),
  subHeading_M = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 18.sp,
    lineHeight = 24.sp
  ),
  subHeading_S = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 24.sp
  ),
  descriptionGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 18.sp
  ),
  smallGames = TextStyle(
    fontFamily = robotoFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 18.sp
  ),
  bodyCopy = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(400),
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = gray3
  ),
  bodyCopyXS = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(400),
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = gray3
  ),
  bodyCopyBold = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(700),
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = pureWhite
  ),
  bodyCopySmall = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(400),
    fontSize = 14.sp,
    lineHeight = 22.sp,
    color = gray3
  ),
  bodyCopySmallBold = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(700),
    fontSize = 14.sp,
    lineHeight = 22.sp,
    color = gray3
  ),
  headlineTitleText = TextStyle(
    fontFamily = dmSansFontFamily,
    fontWeight = FontWeight(700),
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = pureWhite
  ),
  headlineTitleTextSecondary = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(500),
    fontSize = 16.sp,
    lineHeight = 24.sp,
    color = gray3
  ),
  buttonTextSmall = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(500),
    fontSize = 9.sp,
    lineHeight = 14.sp,
    color = pureBlack
  ),
  buttonTextLight = TextStyle(
    fontFamily = dmSansFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 16.sp,
    lineHeight = 18.sp,
    color = pureWhite
  ),
  gameTitleTextCondensedSmall = TextStyle(
    fontFamily = robotoCondensedFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = pureWhite
  ),
  gameTitleTextCondensed = TextStyle(
    fontFamily = dmSansFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 14.sp,
    lineHeight = 18.sp,
    color = pureWhite
  ),
  gameTitleTextCondensedLarge = TextStyle(
    fontFamily = robotoCondensedFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 16.sp,
    lineHeight = 21.sp,
    color = pureWhite
  ),
  gameTitleTextCondensedXL = TextStyle(
    fontFamily = robotoCondensedFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 20.sp,
    lineHeight = 26.sp,
    color = pureWhite
  ),
  buttonTextMedium = TextStyle(
    fontFamily = dmSansFontFamily,
    fontWeight = FontWeight(500),
    fontSize = 14.sp,
    lineHeight = 14.sp,
    color = pureWhite
  ),
  medium_S = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(500),
    fontSize = 14.sp,
    lineHeight = 20.sp,
    color = Color.White
  ),
  medium_XS = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(500),
    fontSize = 12.sp,
    lineHeight = 16.sp,
    color = Color.White
  ),
  button_M = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(500),
    fontSize = 11.sp,
    lineHeight = 14.sp,
    color = Color.White
  ),
  button_L = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight(700),
    fontSize = 14.sp,
    lineHeight = 13.sp,
    color = Color.White
  ),
)

val lightTypography = darkTypography

private val lightGradientsPalette = AppGradients()

private val darkGradientsPalette = AppGradients()

private val darkIcons = AppIcons(
  LeftArrow = getLeftArrow(primary, agBlack),
  ToolBarLogo = getAptoideGamesToolbarLogo(agBlack, primary),
  ToolBarLogoDev = getAptoideGamesToolbarLogoDev(agBlack, primary),
  CaretRight = getCaretRight(),
  PlanetSearch = getPlanetSearch(),
  Gamepad = getGamepad(),
  SingleGamepad = getSingleGamepad(primary),
  ErrorBug = getErrorBug(),
  NoConnection = getNoConnection(),
  ErrorOutlined = getErrorOutlined(),
  NoConnectionSmall = getNoConnectionSmall(0.3f, 0.45f),
  WifiDialogIcon = getWifiDialogIcon(primary, greyLight, agWhite),
  NotificationBell = getNotificationBell(pureWhite),
  HistoryOutlined = getHistoryOutlined(),
  AutoCompleteSuggestion = getAutoCompleteSuggestion(),
  Asterisk = getAsterisk(primary),
  Bug = getBug(),
  Check = getCheck(primary),
  CheckBox = getCheckBox(primary),
  Close = getClose(agWhite),
  Error = getError(error),
  Games = getGames(agBlack, primary),
  Gift = getGift(),
  Logout = getLogout(error),
  NoWifi = getNoWifi(),
  Search = getSearch(agWhite),
  Wifi = getWifi(),
  RecentSearches = getAsterisk(grey),
  PopularSearches = getGames(agBlack, grey),
  SearchLens = getSearch(grey),
  MoreVert = getMoreVert(agWhite),
  Muted = getMuted(agWhite),
  Unmuted = getUnmuted(agWhite),
  GenericError = getGenericError(primary, greyLight, agWhite),
  NoNetworkError = getNoNetworkError(primary, greyLight, agWhite),
  AptoideSetting = getAptoideLogo(agWhite),
)

private val lightIcons = darkIcons

private val darkDrawables = AppDrawables(
  SettingsDialogBackground = getSettingsDialogDarkBackground(),
)

private val lightDrawables = darkDrawables

object AppTheme {
  val colors: AppColors
    @Composable
    @ReadOnlyComposable
    get() = LocalAppColors.current

  val drawables: AppDrawables
    @Composable
    @ReadOnlyComposable
    get() = LocalDrawables.current

  val typography: AppTypography
    @Composable
    @ReadOnlyComposable
    get() = LocalTypography.current

  val gradients: AppGradients
    @Composable
    @ReadOnlyComposable
    get() = LocalGradients.current

  val icons: AppIcons
    @Composable
    @ReadOnlyComposable
    get() = LocalIcons.current
}

private val LocalAppColors = staticCompositionLocalOf {
  lightColorPalette //default
}

private val LocalTypography = staticCompositionLocalOf {
  lightTypography //default
}

private val LocalGradients = staticCompositionLocalOf {
  lightGradientsPalette
}

private val LocalIcons = staticCompositionLocalOf {
  lightIcons
}

private val LocalDrawables = staticCompositionLocalOf {
  lightDrawables //default
}

@Composable
fun AptoideTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colors = if (darkTheme) darkColorPalette else lightColorPalette
  val typography = if (darkTheme) darkTypography else lightTypography
  val gradients = if (darkTheme) darkGradientsPalette else lightGradientsPalette
  val icons = if (darkTheme) darkIcons else lightIcons
  val drawables = if (darkTheme) darkDrawables else lightDrawables

  SetupStatusBarColor(colors.background, darkTheme)

  CompositionLocalProvider(
    LocalAppColors provides colors,
    LocalTypography provides typography,
    LocalGradients provides gradients,
    LocalIcons provides icons,
    LocalDrawables provides drawables
  ) {
    MaterialTheme(
      colors = colors.materialColors,
      typography = typography.materialTypography,
      content = content
    )
  }
}

@Composable
private fun SetupStatusBarColor(
  backgroundColor: Color,
  darkTheme: Boolean,
) {
  val context = LocalContext.current
  LaunchedEffect(key1 = darkTheme) {
    context.let { if (it is Activity) it else null }
      ?.window
      ?.run {
        statusBarColor = backgroundColor.toArgb()
        WindowCompat.getInsetsController(this, decorView)
          .isAppearanceLightStatusBars = !darkTheme
      }
  }
}
