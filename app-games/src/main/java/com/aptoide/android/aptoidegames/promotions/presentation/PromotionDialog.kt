package com.aptoide.android.aptoidegames.promotions.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.emptyApp
import cm.aptoide.pt.feature_campaigns.AptoideMMPCampaign
import cm.aptoide.pt.feature_campaigns.UTMInfo
import cm.aptoide.pt.feature_campaigns.toAptoideMMPCampaign
import cm.aptoide.pt.feature_home.domain.BundleSource
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.dto.BundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsBundleMeta
import com.aptoide.android.aptoidegames.analytics.presentation.OverrideAnalyticsScreen
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.drawables.icons.getPromotionBackground
import com.aptoide.android.aptoidegames.drawables.icons.getPromotionBonusIcon
import com.aptoide.android.aptoidegames.installer.presentation.InstallView
import com.aptoide.android.aptoidegames.promotions.analytics.rememberPromotionsAnalytics
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PromotionDialog(navigate: (String) -> Unit) {
  val promotionsAnalytics = rememberPromotionsAnalytics()
  val promotionsViewModel = hiltViewModel<PromotionsViewModel>()
  val promotionData by promotionsViewModel.uiState.collectAsState()

  LaunchedEffect(promotionData) {
    promotionData?.let { (promotion, app) ->
      AptoideMMPCampaign.allowedBundleTags["home_dialog"] = UTMInfo(
        utmMedium = "promo-card",
        utmCampaign = promotion.uid,
        utmContent = "home-promo-card"
      )
      app.campaigns?.toAptoideMMPCampaign()?.sendImpressionEvent("home_dialog")
      promotionsAnalytics.sendAhabV2DialogImpression(app.packageName)
    }
  }

  promotionData?.let { (promotion, app) ->
    OverrideAnalyticsScreen(
      currentScreen = "home_dialog",
      navigate = navigate
    ) { navigate ->
      OverrideAnalyticsBundleMeta(
        bundleMeta = BundleMeta(
          tag = "home_dialog",
          bundleSource = BundleSource.MANUAL.name
        ),
        navigate = navigate
      ) { navigate ->
        PromotionDialogView(
          onPositiveClick = {
            promotionsViewModel.dismissPromotion()
            promotionsAnalytics.sendAhabV2DialogUpdate(app.packageName)
            navigate(buildAppViewRoute(app))
          },
          onNegativeClick = {
            promotionsViewModel.dismissPromotion()
            promotionsAnalytics.sendAhabV2DialogLater(app.packageName)
          },
          app = app,
          title = promotion.title,
          description = promotion.content,
        )
      }
    }
  }
}

@Composable
fun PromotionDialogView(
  onPositiveClick: () -> Unit,
  onNegativeClick: () -> Unit,
  app: App,
  title: String,
  description: String,
) {
  val context = LocalContext.current
  Dialog(
    onDismissRequest = onNegativeClick,
    properties = DialogProperties(
      dismissOnClickOutside = false,
      dismissOnBackPress = false,
      usePlatformDefaultWidth = false
    ),
  ) {
    Box(
      modifier = Modifier
        .width(328.dp)
        .background(Palette.GreyDark)
    ) {
      Column(
        modifier = Modifier
          .padding(start = 12.dp, end = 12.dp, top = 28.dp, bottom = 8.dp)
          .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
        ) {
          Image(
            imageVector = getPromotionBackground(Palette.Primary),
            contentDescription = null,
            modifier = Modifier.padding(start = 52.dp, top = 20.dp)
          )
          AptoideAsyncImage(
            modifier = Modifier
              .padding(top = 8.dp)
              .align(Alignment.Center)
              .size(144.dp),
            data = app.icon,
            contentDescription = null
          )
          Image(
            imageVector = getPromotionBonusIcon(
              color1 = Palette.Primary,
              color2 = Palette.Black,
              color3 = Palette.White
            ),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd)
          )
        }
        Text(
          text = title,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = Palette.Primary,
          style = AGTypography.Title,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        PromotionDescriptionText(
          modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 20.dp),
          appName = app.name,
          description = description
        )
        InstallView(
          app = app,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          onInstallStarted = onPositiveClick
        )
        PrimaryTextButton(
          onClick = onNegativeClick,
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
          text = stringResource(id = R.string.later_button)
        )
      }
    }
    BackHandler {
      (context as? Activity)?.moveTaskToBack(true)
    }
  }
}

@Composable
fun PromotionDescriptionText(
  modifier: Modifier = Modifier,
  appName: String,
  description: String,
) {
  val appNameStartIndex = description.indexOf(appName, ignoreCase = true)
  if (appNameStartIndex != -1) {
    val appNameEndIndex = appNameStartIndex + appName.length
    val annotatedString = buildAnnotatedString {
      append(description)
      addStyle(
        style = AGTypography.SubHeadingM.toSpanStyle(),
        start = appNameStartIndex,
        end = appNameEndIndex
      )
    }
    Text(
      modifier = modifier,
      text = annotatedString,
      style = AGTypography.SubHeadingS,
      color = Palette.White,
      textAlign = TextAlign.Center,
    )
  } else {
    Text(
      modifier = modifier,
      text = description,
      style = AGTypography.SubHeadingS,
      color = Palette.White,
      textAlign = TextAlign.Center,
    )
  }
}

@PreviewAll
@Composable
private fun PromotionDialogPreview() {
  PromotionDialogView(
    onPositiveClick = {},
    onNegativeClick = {},
    app = emptyApp,
    title = "Up to 20% Bonus",
    description = "Update Mobile Legends Bang Bang and get more in all your purchases."
  )
}
