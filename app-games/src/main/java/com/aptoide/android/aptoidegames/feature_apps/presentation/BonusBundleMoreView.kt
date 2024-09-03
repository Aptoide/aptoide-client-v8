package com.aptoide.android.aptoidegames.feature_apps.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.navDeepLink
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.getRandomString
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.presentation.AppUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiState
import cm.aptoide.pt.feature_apps.presentation.AppsListUiStateProvider
import cm.aptoide.pt.feature_apps.presentation.rememberAppsByTag
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import com.aptoide.android.aptoidegames.AptoideOutlinedText
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.AnalyticsContext
import com.aptoide.android.aptoidegames.analytics.presentation.rememberGenericAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.analytics.presentation.withItemPosition
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.drawables.icons.getBonusIcon
import com.aptoide.android.aptoidegames.drawables.icons.getBonusOnEveryPurchase
import com.aptoide.android.aptoidegames.drawables.icons.getWantMore
import com.aptoide.android.aptoidegames.home.GenericErrorView
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.home.NoConnectionView
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.AptoideTheme
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import com.aptoide.android.aptoidegames.wallet.rememberWalletApp

const val seeMoreBonusRoute = "seeMoreBonus/{title}/{tag}"

fun seeMoreBonusScreen() = ScreenData.withAnalytics(
  route = seeMoreBonusRoute,
  screenAnalyticsName = "SeeAll",
  deepLinks = listOf(navDeepLink { uriPattern = BuildConfig.DEEP_LINK_SCHEMA + seeMoreRoute })
) { arguments, navigate, navigateBack ->
  val bundleTitle = arguments?.getString("title")!!
  val bundleTag = arguments.getString("tag")!!

  MoreBonusBundleView(
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
fun MoreBonusBundleView(
  title: String,
  bundleTag: String,
  navigateBack: () -> Unit,
  navigate: (String) -> Unit,
) {
  val (uiState, reload) = rememberAppsByTag(bundleTag)
  val analyticsContext = AnalyticsContext.current
  val genericAnalytics = rememberGenericAnalytics()

  RealMoreBonusBundleView(
    uiState = uiState,
    title = title,
    bundleTag = bundleTag,
    reload = reload,
    noNetworkReload = {
      reload()
    },
    navigateBack = {
      genericAnalytics.sendBackButtonClick(analyticsContext.copy(itemPosition = null))
      navigateBack()
    },
    navigate = navigate,
  )
}

@Composable
private fun RealMoreBonusBundleView(
  uiState: AppsListUiState,
  title: String,
  bundleTag: String,
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
    when (uiState) {
      AppsListUiState.Loading -> LoadingView()
      AppsListUiState.NoConnection -> NoConnectionView(onRetryClick = noNetworkReload)
      AppsListUiState.Error -> GenericErrorView(reload)
      AppsListUiState.Empty -> MoreBonusBundleViewContent(
        appList = emptyList(),
        navigate = navigate
      )

      is AppsListUiState.Idle -> MoreBonusBundleViewContent(
        appList = uiState.apps.onEach {
          it.campaigns?.run {
            if (AptoideMMPCampaign.allowedBundleTags.keys.contains(bundleTag)) {
              placementType = "see_all"
            }
          }
        },
        navigate = navigate,
      )
    }
  }
}

@Composable
fun MoreBonusBundleViewContent(
  appList: List<App>,
  navigate: (String) -> Unit,
) {
  val navigateToApp = { app: App, index: Int? ->
    navigate(
      buildAppViewRoute(app.packageName).withItemPosition(index)
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
        onClick = { navigateToApp(app, index) },
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
  Box(
    modifier = Modifier.padding(vertical = 24.dp)
  ) {
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
    Image(
      imageVector = getBonusOnEveryPurchase(
        iconColor = Palette.Primary,
        outlineColor = Palette.Black,
        backgroundColor = Palette.Secondary,
        themeColor = Palette.Black
      ),
      contentDescription = "Bonus Section",
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth,
    )
    AptoideOutlinedText(
      text = stringResource(id = R.string.bonus_banner_title, "20"), //TODO Hardcoded value (should come from backend in the future)
      style = AGTypography.InputsM,
      outlineWidth = 10f,
      outlineColor = Palette.Black,
      textColor = Palette.Primary,
      modifier = Modifier.padding(start = 56.dp, top = 14.dp)
    )
    Column(
      modifier = Modifier.padding(horizontal = 16.dp)
    ) {
      BonusBannerText(
        modifier = Modifier.padding(top = 56.dp, bottom = 24.dp),
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
      modifier = Modifier.width(328.dp)
    )
    Text(
      text = annotatedString,
      inlineContent = inlineContent,
      style = AGTypography.BodyBold,
      color = Palette.White,
      maxLines = 4,
      modifier = Modifier.width(328.dp)
    )
  }
}

@Composable
fun WantMoreSectionView(onWalletClick: (app: App) -> Unit) {
  Column(
    modifier = Modifier.padding(bottom = 40.dp)
  ) {
    Box(
      modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
    ) {
      val splitText = stringResource(id = R.string.bonus_page_body_2, "10")

      val bonusStartIndex = splitText.indexOf("10%")
      val bonusEndIndex = bonusStartIndex + "10%".length + "bonus".length + 1

      val annotatedString = buildAnnotatedString {
        append(splitText.substring(0, bonusStartIndex))

        withStyle(style = SpanStyle(color = Palette.Primary)) {
          append(splitText.substring(bonusStartIndex, bonusEndIndex))
        }

        append(splitText.substring(bonusEndIndex))
      }

      Image(
        imageVector = getWantMore(
          iconColor = Palette.Primary,
          outlineColor = Palette.Black,
          backgroundColor = Palette.Secondary,
          themeColor = Palette.Black
        ),
        contentDescription = "Bonus Section",
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.FillWidth,
      )

      BonusBannerText(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp),
        title = stringResource(id = R.string.bonus_page_title_2),
        annotatedString = annotatedString,
      )
    }

    WalletAppItem(
      modifier = Modifier.padding(horizontal = 16.dp),
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
    RealMoreBonusBundleView(
      uiState = uiState,
      title = getRandomString(range = 1..5, capitalize = true),
      bundleTag = "",
      reload = {},
      noNetworkReload = {},
      navigateBack = {},
      navigate = {},
    )
  }
}
