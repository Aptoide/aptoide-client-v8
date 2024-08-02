package com.aptoide.android.aptoidegames

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.launch

interface BottomSheetContent {
  @Composable
  fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AptoideGamesBottomSheet(
  navigate: (String) -> Unit = {},
  content: @Composable (show: (BottomSheetContent?) -> Unit) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var bottomSheetContent: BottomSheetContent? by remember { mutableStateOf(null) }

  val sheetState = rememberModalBottomSheetState(
    initialValue = ModalBottomSheetValue.Hidden,
  )

  val onCloseBottomSheetClick: () -> Unit = {
    coroutineScope.launch {
      sheetState.hide()
      bottomSheetContent = null
    }
  }

  if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
    DisposableEffect(Unit) {
      onDispose {
        bottomSheetContent = null
      }
    }
  }

  ModalBottomSheetLayout(
    sheetState = sheetState,
    scrimColor = Color.Black.copy(alpha = 0.60f),
    sheetBackgroundColor = Palette.Black,
    sheetElevation = 0.dp,
    sheetContent = {
      bottomSheetContent?.Draw(
        dismiss = onCloseBottomSheetClick,
        navigate = navigate,
      )
    },
    content = {
      content {
        bottomSheetContent = it
        coroutineScope.launch {
          sheetState.show()
        }
      }
    }
  )
}

@Composable
fun BottomSheetHeader() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(36.dp),
    contentAlignment = Alignment.Center
  ) {
    Box(
      modifier = Modifier
        .size(width = 32.dp, height = 4.dp)
        .clip(RoundedCornerShape(100.dp))
        .background(Palette.Grey)
    )
  }
}
