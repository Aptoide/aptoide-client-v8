package cm.aptoide.pt.settings.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxColors
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cm.aptoide.pt.aptoide_ui.buttons.GradientButton
import cm.aptoide.pt.aptoide_ui.theme.AppTheme
import cm.aptoide.pt.aptoide_ui.theme.orangeGradient
import cm.aptoide.pt.aptoide_ui.toolbar.NavigationTopBar
import cm.aptoide.pt.settings.repository.sendMail
import cm.aptoide.pt.theme.grey
import cm.aptoide.pt.theme.greyMedium
import cm.aptoide.pt.theme.pinkishOrange
import cm.aptoide.pt.theme.textBlack

const val sendFeedbackRoute = "sendFeedback"

fun NavGraphBuilder.sendFeedbackScreen(
  navigateBack: () -> Unit,
  showSnack: (String) -> Unit,
) = composable(sendFeedbackRoute) {
  val sendFeedbackTitle = "Send Feedback"
  SendFeedbackScreen(
    title = sendFeedbackTitle,
    navigateBack = navigateBack,
    showSnack = showSnack,
  )
}

@Composable
fun SendFeedbackScreen(
  title: String,
  navigateBack: () -> Unit,
  showSnack: (String) -> Unit,
) {
  val feedbackViewModel = hiltViewModel<FeedbackViewModel>()
  val keyboardFocus = LocalFocusManager.current
  val localContext = LocalContext.current

  var subject by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var includeLogs by remember { mutableStateOf(false) }

  SettingsViewContent(
    title = title,
    onBackPressed = navigateBack,

    subject = subject,
    subjectOnClick = { subject = it },
    onKeyboardNext = { keyboardFocus.moveFocus(FocusDirection.Down) },
    description = description,
    descriptionOnClick = { description = it },
    includeLogs = includeLogs,
    includeLogsOnClick = { includeLogs = it },
    submitOnClick = {
      println("Some Feedback String")
      feedbackViewModel
        .getFeedback(
          subject = subject,
          description = description,
          includeLogs = includeLogs
        )
        .let { localContext.sendMail(it) }
      showSnack("Feedback Sent Successfully")
    }
  )
}

@Preview
@Composable
fun SettingsViewContent(
  // View Arguments
  title: String = "Send Feedback",
  onBackPressed: () -> Unit = {},

  subject: String = "",
  subjectOnClick: (String) -> Unit = {},
  onKeyboardNext: () -> Unit = {},
  description: String = "",
  descriptionOnClick: (String) -> Unit = {},
  includeLogs: Boolean = false,
  includeLogsOnClick: (Boolean) -> Unit = {},
  submitOnClick: () -> Unit = {},
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    NavigationTopBar(title, onBackPressed)
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = "Subject",
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.regular_S,
      )

      TextField(
        value = subject,
        onValueChange = subjectOnClick,
        maxLines = 1,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 5.dp)
          .border(
            width = 1.dp,
            color = greyMedium,
            shape = RoundedCornerShape(16.dp)
          ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { onKeyboardNext() }),
        colors = TextFieldDefaults.textFieldColors(
          backgroundColor = AppTheme.colors.background,
          placeholderColor = AppTheme.colors.greyText,
          focusedIndicatorColor = AppTheme.colors.background,
          unfocusedIndicatorColor = AppTheme.colors.background,
          disabledIndicatorColor = AppTheme.colors.background
        ),
        placeholder = {
          Text(
            text = "New updates for Aptoide",
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = AppTheme.typography.regular_S,
            color = AppTheme.colors.greyText
          )
        },
      )

      Text(
        text = "Description",
        textAlign = TextAlign.Start,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        style = AppTheme.typography.regular_S,
      )

      TextField(
        value = description,
        onValueChange = descriptionOnClick,
        maxLines = Int.MAX_VALUE,
        modifier = Modifier
          .fillMaxWidth()
          .weight(2f)
          .border(
            width = 1.dp,
            color = greyMedium,
            shape = RoundedCornerShape(16.dp)
          ),
        colors = TextFieldDefaults.textFieldColors(
          textColor = AppTheme.colors.onBackground,
          disabledLabelColor = textBlack,
          backgroundColor = AppTheme.colors.background,
          placeholderColor = AppTheme.colors.greyText,
          focusedIndicatorColor = AppTheme.colors.background,
          unfocusedIndicatorColor = AppTheme.colors.background,
          disabledIndicatorColor = AppTheme.colors.background
        ),
        placeholder = {
          Text(
            text = "Please give us all the details you can:\n" +
              " - Steps you have done\n" +
              " - Errors you are seeing",
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            maxLines = 3,
            style = AppTheme.typography.regular_S,
            color = AppTheme.colors.greyText
          )
        },
      )

      Row(
        modifier = Modifier.clickable { includeLogsOnClick(!includeLogs) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Checkbox(
            checked = includeLogs,
            onCheckedChange = includeLogsOnClick,
            modifier = Modifier.size(18.dp),
            colors = AptoideCheckboxColors()
          )
        }
        Text(
          text = "Include Logs (Recommended)",
          textAlign = TextAlign.Start,
          overflow = TextOverflow.Ellipsis,
          maxLines = 1,
          style = AppTheme.typography.regular_XS,
          color = AppTheme.colors.greyText
        )
      }

      Spacer(modifier = Modifier.weight(1f))

      GradientButton(
        title = "Send feedback",
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        gradient = orangeGradient,
        isEnabled = subject.isNotEmpty() && description.isNotEmpty(),
        style = AppTheme.typography.button_L,
        onClick = submitOnClick
      )
    }
  }
}

private const val BoxInDuration = 50
private const val BoxOutDuration = 100

@Stable
private class AptoideCheckboxColors(
  private val checkedCheckmarkColor: Color = pinkishOrange,
  private val uncheckedCheckmarkColor: Color = Color.Transparent,
  private val checkedBoxColor: Color = Color.Transparent,
  private val uncheckedBoxColor: Color = Color.Transparent,
  private val disabledCheckedBoxColor: Color = grey,
  private val disabledUncheckedBoxColor: Color = grey,
  private val disabledIndeterminateBoxColor: Color = grey,
  private val checkedBorderColor: Color = pinkishOrange,
  private val uncheckedBorderColor: Color = pinkishOrange,
  private val disabledBorderColor: Color = grey,
  private val disabledIndeterminateBorderColor: Color = grey,
) : CheckboxColors {
  @Composable
  override fun checkmarkColor(state: ToggleableState): State<Color> {
    val target = if (state == ToggleableState.Off) {
      uncheckedCheckmarkColor
    } else {
      checkedCheckmarkColor
    }

    val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
    return animateColorAsState(target, tween(durationMillis = duration))
  }

  @Composable
  override fun boxColor(
    enabled: Boolean,
    state: ToggleableState,
  ): State<Color> {
    val target = if (enabled) {
      when (state) {
        ToggleableState.On, ToggleableState.Indeterminate -> checkedBoxColor
        ToggleableState.Off -> uncheckedBoxColor
      }
    } else {
      when (state) {
        ToggleableState.On -> disabledCheckedBoxColor
        ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
        ToggleableState.Off -> disabledUncheckedBoxColor
      }
    }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
      val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
      animateColorAsState(target, tween(durationMillis = duration))
    } else {
      rememberUpdatedState(target)
    }
  }

  @Composable
  override fun borderColor(
    enabled: Boolean,
    state: ToggleableState,
  ): State<Color> {
    val target = if (enabled) {
      when (state) {
        ToggleableState.On, ToggleableState.Indeterminate -> checkedBorderColor
        ToggleableState.Off -> uncheckedBorderColor
      }
    } else {
      when (state) {
        ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
        ToggleableState.On, ToggleableState.Off -> disabledBorderColor
      }
    }

    // If not enabled 'snap' to the disabled state, as there should be no animations between
    // enabled / disabled.
    return if (enabled) {
      val duration = if (state == ToggleableState.Off) BoxOutDuration else BoxInDuration
      animateColorAsState(target, tween(durationMillis = duration))
    } else {
      rememberUpdatedState(target)
    }
  }
}
