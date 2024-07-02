package com.aptoide.android.aptoidegames

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
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch

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

interface BottomSheetContent {
  @Composable
  fun Draw(
    dismiss: () -> Unit,
    navigate: (String) -> Unit,
  )
}
