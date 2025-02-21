package com.aptoide.android.aptoidegames.feature_promotional

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cm.aptoide.pt.appcomingsoon.domain.AppComingSoonCard
import cm.aptoide.pt.extensions.hasNotificationsPermission
import cm.aptoide.pt.feature_home.domain.Bundle
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.design_system.PrimarySmallOutlinedButton
import com.aptoide.android.aptoidegames.design_system.SecondarySmallOutlinedButton
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Error
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Idle
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.Loading
import com.aptoide.android.aptoidegames.feature_promotional.AppComingSoonUIState.NoConnection
import com.aptoide.android.aptoidegames.home.LoadingView
import com.aptoide.android.aptoidegames.notifications.NotificationsPermissionRequester
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@SuppressLint("InlinedApi")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppComingSoonPromotionalView(
  bundle: Bundle,
) {
  var showNotificationsDialog by remember { mutableStateOf(false) }
  val onChangeNotificationDialogState = { shouldShow: Boolean ->
    showNotificationsDialog = shouldShow
  }

  bundle.view?.let {
    val (uiState, onSubscribe) = rememberAppComingSoon(it)

    when (uiState) {
      is Idle -> AppComingSoonPromotional(
        appComingSoonCard = uiState.subscribedAppComingSoonCard.appComingSoonCard,
        isSubscribed = uiState.subscribedAppComingSoonCard.isSubscribed,
        onSubscribe = onSubscribe,
        showNotificationsDialog = showNotificationsDialog,
        onChangeNotificationDialogState = onChangeNotificationDialogState
      )

      Loading -> LoadingView()
      Error,
      NoConnection -> Unit
    }
  }
}

@Composable
private fun AppComingSoonPromotional(
  appComingSoonCard: AppComingSoonCard,
  isSubscribed: Boolean,
  onSubscribe: (String, Boolean) -> Unit,
  showNotificationsDialog: Boolean,
  onChangeNotificationDialogState: (Boolean) -> Unit,
) {
  val context = LocalContext.current

  Column(
    modifier = Modifier
      .padding(horizontal = 16.dp)
  ) {
    AptoidePromotionalFeatureGraphicImage(
      featureGraphic = appComingSoonCard.featureGraphic,
      label = appComingSoonCard.caption,
      hasAppCoins = false
    )

    Row(
      modifier = Modifier.padding(top = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Image(
        painter = painterResource(id = R.drawable.app_icon),
        contentDescription = "AG icon",
        modifier = Modifier
          .size(40.dp)
      )
      Column(
        modifier = Modifier
          .padding(start = 8.dp, end = 8.dp)
          .weight(1f),
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = appComingSoonCard.title,
          modifier = Modifier
            .clearAndSetSemantics {}
            .wrapContentHeight(unbounded = true),
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AGTypography.DescriptionGames
        )
        Text(
          text = stringResource(id = if (!isSubscribed) R.string.promotional_soon_in_aptoide else R.string.promotional_new_active),
          modifier = Modifier
            .clearAndSetSemantics {}
            .wrapContentHeight(unbounded = true),
          color = Palette.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = AGTypography.InputsXS
        )
      }

      if (isSubscribed) {
        SecondarySmallOutlinedButton(
          title = stringResource(R.string.cancel_button),
          onClick = { onSubscribe(appComingSoonCard.packageName, !isSubscribed) }
        )
      } else {
        PrimarySmallOutlinedButton(
          title = stringResource(R.string.promotional_notify_button),
          onClick = {
            if (context.hasNotificationsPermission()) {
              onSubscribe(appComingSoonCard.packageName, !isSubscribed)
            } else {
              onChangeNotificationDialogState(true)
            }
          })
      }
    }
  }

  NotificationsPermissionRequester(
    showDialog = showNotificationsDialog,
    onDismiss = { onChangeNotificationDialogState(false) },
    onPermissionResult = { onSubscribe(appComingSoonCard.packageName, it) }
  )
}
