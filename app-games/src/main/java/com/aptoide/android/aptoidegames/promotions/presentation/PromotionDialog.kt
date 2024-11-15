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
import cm.aptoide.pt.extensions.PreviewAll
import cm.aptoide.pt.feature_apps.data.App
import cm.aptoide.pt.feature_apps.data.emptyApp
import com.aptoide.android.aptoidegames.AptoideAsyncImage
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimaryTextButton
import com.aptoide.android.aptoidegames.drawables.icons.getPromotionBackground
import com.aptoide.android.aptoidegames.drawables.icons.getPromotionBonusIcon
import com.aptoide.android.aptoidegames.installer.presentation.InstallView
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette

@Composable
fun PromotionDialog(
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
  PromotionDialog(
    onPositiveClick = {},
    onNegativeClick = {},
    app = emptyApp,
    title = "Up to 20% Bonus",
    description = "Update Mobile Legends Bang Bang and get more in all your purchases."
  )
}
