package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.extensions.toAnnotatedString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_apps.presentation.rememberWalletApp
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGeneralAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.backgrounds.getMoreBonusViewHeader
import com.aptoide.android.aptoidegames.drawables.backgrounds.getWantMoreViewFooter
import com.aptoide.android.aptoidegames.drawables.backgrounds.getWantMoreViewHeader
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.error_views.NoConnectionView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.mmp.UTMContext
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

const val seeMoreBonusRoute = "seeMoreBonus/{title}/{tag}"

fun seeMoreBonusScreen() = ScreenData.withAnalytics(
  route = seeMoreBonusRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + seeMoreBonusRoute })
) { arguments, navigate, navigateBack ->
  val bundleTitle = arguments?.getString("title")!!
  val bundleTag = arguments.getString("tag")!!

  MoreBonusBundleScreen(
    title = bundleTitle,
    bundleTag = bundleTag,
    navigateBack = navigateBack,
    navigate = navigate,
  )
}

fun buildSeeMoreBonusRoute(
  title: String,
  bundleTag: String,
) = "seeMoreBonus/$title/$bundleTag"

@Composable
private fun MoreBonusBundleScreen(
  title: String,
  bundleTag: String,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberAppsByTag(bundleTag)
  val analyticsContext = AnalyticsContext.current
  val generalAnalytics = rememberGeneralAnalytics()

  MoreBonusBundleScreen(
    uiState = uiState,
    title = title,
    reload = reload,
    noNetworkReload = {
      reload()
    },
    navigateBack = {
      generalAnalytics.sendBackButtonClick(analyticsContext.copy(itemPosition = null))
      navigateBack()
    },
    navigate = navigate,
  )
}

@Composable
fun MoreBonusBundleScreen(
  uiState: AppsListUiState,
  title: String,
  reload: () -> Unit,
  noNetworkReload: () -> Unit,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    AppGamesTopBar(navigateBack = navigateBack, title = title)
    MoreBonusBundleView(
      uiState = uiState,
      navigate = navigate,
      reload = reload,
      noNetworkReload = noNetworkReload
    )
  }
}

@Composable
fun MoreBonusBundleView(
  uiState: AppsListUiState,
  navigate: (String) -> Unit,
  reload: () -> Unit,
  noNetworkReload: () -> Unit,
) {
  when (uiState) {
    AppsListUiState.Loading -> LoadingView()
    AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = noNetworkReload)
    AppsListUiState.Error -> GenericErrorView(reload)
    AppsListUiState.Empty -> MoreBonusBundleViewContent(
      appList = emptyList(),
      navigate = navigate
    )

    is AppsListUiState.Idle -> MoreBonusBundleViewContent(
      appList = uiState.apps,
      navigate = navigate,
    )
  }
}

@Composable
fun MoreBonusBundleViewContent(
  appList: List<App>,
  navigate: (String) -> Unit,
) {
  val analyticsContext = AnalyticsContext.current
  val utmContext = UTMContext.current
  val bundleAnalytics = rememberBundleAnalytics()

  val navigateToApp = { app: App, index: Int? ->
    navigate(
      buildAppViewRoute(
        appSource = app,
        utmCampaign = app.campaigns?.campaignId
      ).withItemPosition(index)
    )
  }

  LazyColumn(
    modifier = Modifier
      .semantics { collectionInfo = CollectionInfo(appList.size, 1) }
      .wrapContentSize(Alignment.TopCenter)
  ) {
    item { MoreBonusSectionView(onWalletClick = { navigateToApp(it, null) }) }
    itemsIndexed(appList) { index, app ->
      AppItem(
        modifier = Modifier.padding(horizontal = 16.dp),
        app = app,
        onClick = {
          app.campaigns?.toAptoideMMPCampaign()?.sendClickEvent(utmContext)
          bundleAnalytics.sendAppPromoClick(
            app = app,
            analyticsContext = analyticsContext.copy(itemPosition = index)
          )
          navigateToApp(app, index)
        },
      ) {
        InstallViewShort(app)
      }
    }
    item { WantMoreSectionView(onWalletClick = { navigateToApp(it, null) }) }
  }
}

@Composable
fun MoreBonusSectionView(
  onWalletClick: (app: App) -> Unit,
) {
  Column(
    modifier = Modifier.padding(vertical = 24.dp)
  ) {
    val configuration = LocalConfiguration.current
    val startPadding = (configuration.screenWidthDp * 0.15).dp
    val splitText = stringResource(id = R.string.bonus_page_body_1, "%s", 20).split("%s")
    val annotatedString = buildAnnotatedString {
      append(splitText[0])
      appendInlineContent(id = "%s")
      append(splitText[1])
    }
    val inlineContent = mapOf(
      "%s" to InlineTextContent(
        placeholder = Placeholder(
          width = 16.sp,
          height = 16.sp,
          placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
        ),
        children = {
          Image(
            imageVector = getBonusIcon(
              outlineColor = Palette.Black,
              giftColor = Palette.Primary,
            ),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
        }
      )
    )
    Box {
      Image(
        imageVector = getMoreBonusViewHeader(),
        contentDescription = null,
        modifier = Modifier
          .fillMaxWidth()
          .padding(end = 98.dp)
          .offset(y = 1.dp),
        contentScale = ContentScale.FillWidth,
      )

      Row(
        modifier = Modifier
          .matchParentSize()
          .padding(start = startPadding, top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AptoideOutlinedText(
          text = stringResource(
            id = R.string.bonus_banner_title,
            "20" //TODO Hardcoded value (should come from backend in the future)
          ),
          style = AGTypography.InputsM,
          outlineWidth = 10f,
          outlineColor = Palette.Black,
          textColor = Palette.Primary,
        )
      }
    }

    Column(
      modifier = Modifier
        .background(Palette.GreyDark)
        .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
      BonusBannerText(
        title = stringResource(id = R.string.bonus_page_title_1),
        annotatedString = annotatedString,
        inlineContent = inlineContent
      )
      WalletAppItem(onWalletClick = onWalletClick)
    }
  }
}

@Composable
private fun BonusBannerText(
  modifier: Modifier = Modifier,
  title: String,
  annotatedString: AnnotatedString,
  inlineContent: Map<String, InlineTextContent> = mapOf(),
) {
  Column(modifier = modifier) {
    AptoideOutlinedText(
      text = title,
      style = AGTypography.Title,
      outlineWidth = 17f,
      outlineColor = Palette.Black,
      textColor = Palette.Primary,
      modifier = Modifier
        .padding(bottom = 8.dp)
        .fillMaxWidth()
    )
    Text(
      text = annotatedString,
      inlineContent = inlineContent,
      style = AGTypography.InputsM,
      color = Palette.White,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
fun WantMoreSectionView(onWalletClick: (app: App) -> Unit) {
  Column(
    modifier = Modifier.padding(bottom = 40.dp)
  ) {
    val originalString = stringResource(id = R.string.bonus_page_body_2, "10")
    val annotatedString = originalString.toAnnotatedString(SpanStyle(color = Palette.Primary))

    Image(
      imageVector = getWantMoreViewHeader(),
      contentDescription = null,
      modifier = Modifier
        .fillMaxWidth()
        .offset(y = 1.dp),
      contentScale = ContentScale.FillWidth,
    )

    BonusBannerText(
      modifier = Modifier
        .background(Palette.Secondary)
        .padding(horizontal = 16.dp),
      title = stringResource(id = R.string.bonus_page_title_2),
      annotatedString = annotatedString,
    )

    Image(
      imageVector = getWantMoreViewFooter(),
      contentDescription = null,
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth,
    )

    WalletAppItem(
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .offset(y = (-1).dp),
      onWalletClick = onWalletClick
    )
  }
}

@Composable
private fun WalletAppItem(
  modifier: Modifier = Modifier,
  onWalletClick: (app: App) -> Unit,
) {
  val (uiState, _) = rememberWalletApp()
  val walletApp = (uiState as? AppUiState.Idle)?.app
  walletApp?.let {
    AppItem(
      modifier = modifier,
      app = it,
      onClick = {
        onWalletClick(it)
      }
    ) {
      InstallViewShort(app = it)
    }
  }
}

@PreviewDark
@Composable
private fun RealBonusBundlePreview(
  @PreviewParameter(AppsListUiStateProvider::class) uiState: AppsListUiState,
) {
  AptoideTheme {
    MoreBonusBundleScreen(
      uiState = uiState,
      title = getRandomString(range = 1..5, capitalize = true),
      reload = {},
      noNetworkReload = {},
      navigateBack = {},
      navigate = {},
    )
  }
}
