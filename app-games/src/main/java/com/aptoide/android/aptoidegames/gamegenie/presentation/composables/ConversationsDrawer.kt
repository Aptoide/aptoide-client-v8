import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.gamegenie.analytics.rememberGameGenieAnalytics
import com.aptoide.android.aptoidegames.gamegenie.presentation.composables.DrawerContent
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import kotlinx.coroutines.launch

private const val DRAWER_SIZE = 304f
private const val ANIMATION_DURATION = 300

@Composable
fun ConversationsDrawer(
  mainScreen: @Composable () -> Unit,
  loadConversationFn: (String) -> Unit,
  currentChatId: String,
  newChatFn: () -> Unit,
) {
  val isOpen = remember { mutableStateOf(false) }
  val screenOffsetX = remember { Animatable(0f) }
  val scope = rememberCoroutineScope()
  val analytics = rememberGameGenieAnalytics()

  fun openDrawer() {
    scope.launch {
      screenOffsetX.animateTo(DRAWER_SIZE, tween(ANIMATION_DURATION))
      isOpen.value = true
    }
    analytics.sendGameGenieHistoryOpen()
  }

  fun closeDrawer() {
    scope.launch {
      screenOffsetX.animateTo(0f, tween(ANIMATION_DURATION))
      isOpen.value = false
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .pointerInput(Unit) {
        detectHorizontalDragGestures(
          onHorizontalDrag = { change, dragAmount ->
            change.consume()
            scope.launch {
              val target = screenOffsetX.value + dragAmount / 2
              screenOffsetX.snapTo(target.coerceIn(0f, DRAWER_SIZE))
            }
          },
          onDragEnd = {
            if (screenOffsetX.value > DRAWER_SIZE / 2) {
              openDrawer()
            } else {
              closeDrawer()
            }
          }
        )
      }
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .offset(x = screenOffsetX.value.dp)
        .background(Palette.GreyDark.copy(alpha = (screenOffsetX.value / DRAWER_SIZE * 0.5f)))
        .pointerInput(Unit) {
          detectTapGestures { closeDrawer() }
        }
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Box(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.align(Alignment.CenterStart)
          ) {
            IconButton(
              onClick = {
                if (isOpen.value) {
                  closeDrawer()
                } else {
                  openDrawer()
                }
              }
            ) {
              Icon(
                painter = painterResource(R.drawable.more),
                contentDescription = "Toggle Drawer",
                tint = Color.White
              )
            }
          }
          Row(
            modifier = Modifier
              .align(Alignment.Center)
              .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              stringResource(R.string.genai_bottom_navigation_gamegenie_button),
              style = AGTypography.InputsL
            )
            Spacer(modifier = Modifier.width(4.dp))
            Image(
              painter = painterResource(R.drawable.gamegenie_ai_toolbar_icon),
              contentDescription = null,
              )
          }
        }
        mainScreen()
      }
    }

    Box(
      modifier = Modifier
        .fillMaxHeight()
        .width(DRAWER_SIZE.dp)
        .offset(x = screenOffsetX.value.dp - DRAWER_SIZE.dp - 1.dp)
    ) {
      DrawerContent(
        { id ->
          loadConversationFn(id)
          analytics.sendGameGenieHistoryClick()
          closeDrawer()
        },
        currentChatId,
        {
          newChatFn()
          analytics.sendGameGenieNewChat()
          closeDrawer()
        },
        analytics::sendGameGenieHistoryDelete
      )
    }
  }
}
