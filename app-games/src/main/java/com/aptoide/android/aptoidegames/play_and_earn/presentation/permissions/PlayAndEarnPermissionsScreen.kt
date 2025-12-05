package com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.toAnnotatedString
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentSmallButton
import com.aptoide.android.aptoidegames.drawables.figures.getCheckCircle
import com.aptoide.android.aptoidegames.drawables.figures.getPermissionAllowFigure
import com.aptoide.android.aptoidegames.drawables.icons.getTrustedIcon
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.service.PaEForegroundService
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar
import kotlinx.coroutines.launch

const val playAndEarnPermissionsRoute = "playAndEarnPermissions"

fun playAndEarnPermissionsScreen() = ScreenData.withAnalytics(
  route = playAndEarnPermissionsRoute,
  screenAnalyticsName = "PlayAndEarnPermissions",
) { _, navigate, navigateBack ->

  PlayAndEarnPermissionsScreen(navigateBack)
}

@Composable
private fun PlayAndEarnPermissionsScreen(
  navigateBack: () -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  val paeAnalytics = rememberPaEAnalytics()

  var allowedRestrictedSettings by remember { mutableStateOf(false) }

  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        if (context.hasOverlayPermission() && context.hasUsageStatsPermissionStatus()) {
          PaEForegroundService.start(context)
          navigateBack()
        }

        allowedRestrictedSettings =
          context.hasOverlayPermission() || context.hasUsageStatsPermissionStatus()
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
    }
  }

  val onPermissionClick: () -> Unit = {
    paeAnalytics.sendPaEFinalPermissionsClick()
    coroutineScope.launch {
      context.startActivity(Intent(context, OverlayPermissionActivity::class.java))
    }
  }

  val onRestrictedSettingsClick: () -> Unit = {
    paeAnalytics.sendPaEPermissionRestrictedSettingsClick()
    coroutineScope.launch {
      val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        "package:${context.packageName}".toUri()
      )
      context.startActivity(intent)
    }
  }

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    AppGamesTopBar(
      navigateBack = navigateBack,
      title = stringResource(R.string.play_and_earn_start_earning_title)
    )
    PaERestrictedPermissionsScreenContent(
      onRestrictedSettingsClick = onRestrictedSettingsClick,
      onFinalPermissionsClick = onPermissionClick,
      allowedRestrictedSettings = allowedRestrictedSettings
    )
  }
}

@Composable
private fun PaERestrictedPermissionsScreenContent(
  onRestrictedSettingsClick: () -> Unit,
  onFinalPermissionsClick: () -> Unit,
  allowedRestrictedSettings: Boolean
) {
  val composition by rememberLottieComposition(
    LottieCompositionSpec.RawRes(R.raw.play_and_earn_permissions_animation)
  )

  val progress by animateLottieCompositionAsState(
    composition = composition,
    iterations = LottieConstants.IterateForever
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(vertical = 28.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(24.dp)
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(120.dp),
        contentScale = ContentScale.Crop,
      )
      TrustedAppBadge(
        modifier = Modifier
          .zIndex(-1f)
          .offset(y = (-5).dp)
      )
    }

    Column(
      modifier = Modifier.padding(horizontal = 24.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_permissions_google_drama_title),
        style = AGTypography.Title,
        color = Palette.Yellow100,
        textAlign = TextAlign.Center,
      )

      Text(
        text = stringResource(id = R.string.play_and_earn_permissions_allow_permissions_body).toAnnotatedString(
          AGTypography.InputsL.toSpanStyle()
        ),
        style = AGTypography.InputsL.copy(fontWeight = FontWeight.Normal),
        color = Palette.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
      )
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      if (allowedRestrictedSettings) {
        RestrictedSettingsAllowedSection()
      } else {
        RestrictedSettingsNotAllowedSection(
          onButtonClick = onRestrictedSettingsClick
        )
      }
      FinalPermissionsSection(
        headerNumber = 2,
        onButtonClick = onFinalPermissionsClick
      )
    } else {
      FinalPermissionsSection(
        headerNumber = null,
        onButtonClick = onFinalPermissionsClick
      )
    }
  }
}

@Composable
private fun RestrictedSettingsNotAllowedSection(
  onButtonClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    PermissionSectionHeader(
      number = 1,
      title = stringResource(R.string.play_and_earn_permissions_restricted_settings_title)
    )

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_permissions_restricted_settings_body),
        style = AGTypography.InputsS,
        color = Palette.White,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = stringResource(R.string.play_and_earn_permissions_settings_path),
        style = AGTypography.InputsS,
        color = Palette.Yellow100,
        fontWeight = FontWeight.Bold
      )
    }

    Image(
      painter = painterResource(id = R.drawable.restricted_settings),
      contentDescription = null,
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth
    )

    AccentSmallButton(
      title = stringResource(R.string.play_and_earn_lets_do_it_button),
      onClick = onButtonClick,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun RestrictedSettingsAllowedSection() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .border(width = 2.dp, color = Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    PermissionSectionHeader(
      number = 1,
      title = stringResource(R.string.play_and_earn_permissions_restricted_settings_title)
    )

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Palette.Secondary.copy(alpha = 0.2f))
        .padding(9.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        imageVector = getCheckCircle(Palette.SecondaryLight),
        contentDescription = null
      )
      Text(
        text = stringResource(R.string.play_and_earn_permissions_allowed),
        style = AGTypography.InputsS,
        color = Palette.SecondaryLight
      )
    }
  }
}

@Composable
private fun FinalPermissionsSection(
  headerNumber: Int? = null,
  onButtonClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .background(Palette.GreyDark)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    headerNumber?.let {
      PermissionSectionHeader(
        number = it,
        title = stringResource(R.string.play_and_earn_permissions_final_permissions_title)
      )
    }

    PermissionAllowItem(
      title = stringResource(R.string.play_and_earn_permissions_display_over_apps),
      description = stringResource(R.string.play_and_earn_permissions_display_description)
    )

    PermissionAllowItem(
      title = stringResource(R.string.play_and_earn_permissions_app_usage_data),
      description = stringResource(R.string.play_and_earn_permissions_usage_description)
    )

    AccentSmallButton(
      title = stringResource(R.string.play_and_earn_permissions_go_button),
      onClick = onButtonClick,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

@Composable
private fun PermissionSectionHeader(number: Int, title: String) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .background(Palette.SecondaryLight),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number.toString(),
        style = AGTypography.InputsL,
        color = Palette.Black,
        fontWeight = FontWeight.Bold
      )
    }

    Text(
      text = title,
      style = AGTypography.InputsL,
      color = Palette.SecondaryLight,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun PermissionAllowItem(
  title: String,
  description: String
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Column(
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = title,
        style = AGTypography.InputsL,
        color = Palette.White,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = description,
        style = AGTypography.InputsXSRegular,
        color = Palette.White
      )
    }

    Image(
      imageVector = getPermissionAllowFigure(),
      contentDescription = null,
      modifier = Modifier.fillMaxWidth(),
      contentScale = ContentScale.FillWidth
    )
  }
}

@Composable
private fun TrustedAppBadge(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .background(Palette.Green)
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = getTrustedIcon(Palette.White),
        contentDescription = null,
        tint = Palette.White,
        modifier = Modifier.size(12.dp)
      )
      Text(
        text = stringResource(R.string.play_and_earn_permissions_trusted_app),
        style = AGTypography.InputsXS,
        color = Palette.White
      )
    }
  }
}

@Preview
@Composable
private fun PlayAndEarnPermissionsScreenPreview() {
  PlayAndEarnPermissionsScreen(navigateBack = {})
}