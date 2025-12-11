package com.aptoide.android.aptoidegames.gamegenie.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.BottomSheetContent
import com.aptoide.android.aptoidegames.BottomSheetHeader
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import cm.aptoide.pt.extensions.toAnnotatedString

class GameGenieOverlayPermissionSheet(
  private val onAccept: () -> Unit,
) : BottomSheetContent {
  @Composable
  override fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  ) {
    val permissionMessage = stringResource(R.string.gamegenie_overlay_permission_message)

    val styledText = permissionMessage.toAnnotatedString(
      SpanStyle(fontWeight = FontWeight.Bold, color = Palette.Secondary)
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      BottomSheetHeader()

      AnimationComposable(
        modifier = Modifier.size(88.dp),
        resId = R.raw.game_genie_updated_chat_big_animation
      )

      Text(
        text = styledText,
        style = AGTypography.SubHeadingS,
        color = Palette.White,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .background(Palette.Secondary)
          .clickable {
            onAccept()
            dismiss()
          },
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = stringResource(R.string.gamegenie_overlay_permission_accept),
          style = AGTypography.InputsL,
          color = Palette.White
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = stringResource(R.string.cancel_button),
        style = AGTypography.InputsM,
        color = Palette.GreyLight,
        modifier = Modifier.clickable { dismiss() }
      )
    }
  }
}
