package com.aptoide.android.aptoidegames.apkfy.presentation

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cm.aptoide.pt.extensions.toAnnotatedString
import cm.aptoide.pt.feature_apps.data.App
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.drawables.icons.getAptoideGamesToolbarLogo
import com.aptoide.android.aptoidegames.feature_apps.presentation.AppItem
import com.aptoide.android.aptoidegames.installer.presentation.InstallViewShort
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

@Composable
internal fun ApkfyRewardScreenContent(
  app: App,
  @DrawableRes headerImage: Int,
  @DrawableRes rewardIcon: Int,
  paERewardType: PaERewardType,
  rewardAmount: String,
  navigateBack: () -> Unit,
  onAppClick: () -> Unit,
  onCollect: () -> Unit,
  onDismiss: () -> Unit,
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Palette.Black),
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      Box {
        Image(
          painter = painterResource(headerImage),
          contentDescription = null,
          contentScale = ContentScale.Crop,
          colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }),
          modifier = Modifier
            .matchParentSize()
            .align(Alignment.TopCenter),
        )
        Box(
          modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.8f))
        )
        Column(modifier = Modifier.statusBarsPadding()) {
          AppGamesTopBar(
            navigateBack = navigateBack,
            title = stringResource(R.string.play_and_earn_title),
          )
          AppItem(
            modifier = Modifier.padding(horizontal = 24.dp),
            app = app,
            onClick = onAppClick,
          ) {
            InstallViewShort(app = app)
          }
        }
      }
      RewardSection(
        rewardIcon = rewardIcon,
        paERewardType = paERewardType,
        rewardAmount = rewardAmount,
        onCollect = onCollect,
        onDismiss = onDismiss,
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Palette.Secondary.copy(alpha = 0.2f),
                Palette.Secondary.copy(alpha = 0.6f),
              )
            )
          ),
      )
    }
  }
}

@Composable
private fun RewardSection(
  @DrawableRes rewardIcon: Int,
  paERewardType: PaERewardType,
  rewardAmount: String,
  onCollect: () -> Unit,
  onDismiss: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
      .padding(top = 24.dp)
      .padding(horizontal = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    ExclusiveOfferBanner()
    Column(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(vertical = 64.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      Image(
        painter = painterResource(rewardIcon),
        contentDescription = null,
      )
      Spacer(modifier = Modifier.height(24.dp))
      RewardEarnedText(rewardAmount = rewardAmount, paERewardType = paERewardType)
      Spacer(modifier = Modifier.height(24.dp))
      AccentButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onCollect,
        title = stringResource(R.string.play_and_earn_collect_button),
      )
      PrimaryTextButton(
        onClick = onDismiss,
        color = Palette.GreyLight,
        text = stringResource(R.string.play_and_earn_dismiss_button),
      )
    }
  }
}

@Composable
private fun ExclusiveOfferBanner() {
  Row(
    modifier = Modifier.padding(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = stringResource(R.string.play_and_earn_exclusive_offer),
      color = Palette.Yellow,
      style = AGTypography.InputsM,
    )
    Image(
      imageVector = getAptoideGamesToolbarLogo(Palette.Yellow),
      contentDescription = null,
      modifier = Modifier.size(width = 63.dp, height = 14.dp),
    )
  }
}

@Composable
private fun RewardEarnedText(
  rewardAmount: String,
  paERewardType: PaERewardType,
) {
  val template = stringResource(
    R.string.play_and_earn_reward_earned_message,
    rewardAmount,
    stringResource(paERewardType.displayNameRes),
  )
  val annotatedString = template.toAnnotatedString(SpanStyle(color = Palette.Yellow))
  Text(
    modifier = Modifier.fillMaxWidth(),
    text = annotatedString,
    color = Palette.White,
    style = AGTypography.Title,
    textAlign = TextAlign.Center,
  )
}

@Composable
internal fun TransparentStatusBarEffect() {
  val context = LocalContext.current
  DisposableEffect(Unit) {
    val window = (context as? Activity)?.window
    val originalStatusBarColor = window?.statusBarColor
    window?.run {
      WindowCompat.setDecorFitsSystemWindows(this, false)
      statusBarColor = Color.Transparent.toArgb()
    }
    onDispose {
      window?.run {
        WindowCompat.setDecorFitsSystemWindows(this, true)
        originalStatusBarColor?.let { statusBarColor = it }
      }
    }
  }
}
