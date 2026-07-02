package com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import cm.aptoide.pt.extensions.PreviewDark
import cm.aptoide.pt.extensions.ScreenData
import cm.aptoide.pt.extensions.toAnnotatedString
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.analytics.presentation.withAnalytics
import com.aptoide.android.aptoidegames.design_system.AccentButton
import com.aptoide.android.aptoidegames.design_system.IndeterminateCircularLoading
import com.aptoide.android.aptoidegames.drawables.icons.getError
import com.aptoide.android.aptoidegames.error_views.GenericErrorView
import com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics.rememberPaEAnalytics
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardHeroArt
import com.aptoide.android.aptoidegames.play_and_earn.presentation.rewards.PaERewardType
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.rememberUserInfo
import com.aptoide.android.aptoidegames.theme.AGTypography
import com.aptoide.android.aptoidegames.theme.Palette
import com.aptoide.android.aptoidegames.toolbar.AppGamesTopBar

private const val exchangeEmailBase = "exchangeEmail"
private const val rewardTypeArg = "rewardType"
private const val amountArg = "amount"
const val exchangeEmailRoute = "$exchangeEmailBase/{$rewardTypeArg}/{$amountArg}"

fun buildExchangeEmailRoute(rewardType: PaERewardType, formattedAmount: String) =
  "$exchangeEmailBase/${rewardType.name}/${android.net.Uri.encode(formattedAmount)}"

fun exchangeEmailScreen(
  navigateToSuccess: (PaERewardType, String, String) -> Unit,
) = ScreenData.withAnalytics(
  route = exchangeEmailRoute,
  screenAnalyticsName = "ExchangeEmail",
  arguments = listOf(
    navArgument(rewardTypeArg) { type = NavType.StringType },
    navArgument(amountArg) { type = NavType.StringType },
  ),
) { args, _, navigateBack ->
  val rewardType = args?.getString(rewardTypeArg)
    ?.let { runCatching { PaERewardType.valueOf(it) }.getOrNull() }
    ?: PaERewardType.ROBUX
  val formattedAmount = args?.getString(amountArg)?.let { android.net.Uri.decode(it) }.orEmpty()

  val userInfo = rememberUserInfo()

  val viewModel = hiltViewModel<ExchangeByEmailViewModel>()
  val uiState by viewModel.uiState.collectAsState()
  val paeAnalytics = rememberPaEAnalytics()

  var submittedEmail by rememberSaveable { mutableStateOf("") }

  LaunchedEffect(uiState) {
    when (uiState) {
      ExchangeEmailUiState.Success -> {
        paeAnalytics.sendPaEExchangeResult(success = true, rewardType = rewardType)
        navigateToSuccess(rewardType, formattedAmount, submittedEmail)
        viewModel.resetState()
      }

      ExchangeEmailUiState.Error ->
        paeAnalytics.sendPaEExchangeResult(success = false, rewardType = rewardType)

      else -> Unit
    }
  }

  ExchangeEmailScreen(
    uiState = uiState,
    rewardType = rewardType,
    formattedAmount = formattedAmount,
    prefilledEmail = userInfo?.email.orEmpty(),
    navigateBack = navigateBack,
    onSubmit = { email ->
      paeAnalytics.sendPaEExchangeGetCodeClick(rewardType)
      submittedEmail = email
      viewModel.exchange(email, rewardType)
    },
    onRetry = viewModel::retry,
  )
}

@Composable
fun ExchangeEmailScreen(
  uiState: ExchangeEmailUiState,
  rewardType: PaERewardType,
  formattedAmount: String,
  prefilledEmail: String,
  navigateBack: () -> Unit,
  onSubmit: (email: String) -> Unit,
  onRetry: () -> Unit,
) {
  ExchangeFlowBackground {
    Column(modifier = Modifier.fillMaxSize()) {
      AppGamesTopBar(
        navigateBack = navigateBack,
        title = "",
        iconColor = Palette.White,
      )

      when (uiState) {
        ExchangeEmailUiState.Error -> GenericErrorView(
          onRetryClick = onRetry,
          modifier = Modifier.weight(1f),
        )

        ExchangeEmailUiState.Loading -> Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center,
        ) {
          IndeterminateCircularLoading(color = Palette.Primary)
        }

        ExchangeEmailUiState.Idle,
        ExchangeEmailUiState.Success -> ExchangeEmailForm(
          rewardType = rewardType,
          formattedAmount = formattedAmount,
          prefilledEmail = prefilledEmail,
          onSubmit = onSubmit,
          modifier = Modifier.weight(1f),
        )
      }
    }
  }
}

@Composable
private fun ExchangeEmailForm(
  rewardType: PaERewardType,
  formattedAmount: String,
  prefilledEmail: String,
  onSubmit: (email: String) -> Unit,
  modifier: Modifier = Modifier,
) {
  var email by rememberSaveable { mutableStateOf(prefilledEmail) }
  var userEdited by rememberSaveable { mutableStateOf(false) }
  var showError by rememberSaveable { mutableStateOf(false) }

  // The signed-in user's email arrives asynchronously; populate the field once it does,
  // unless the user has already started editing.
  LaunchedEffect(prefilledEmail) {
    if (prefilledEmail.isNotBlank() && !userEdited && email != prefilledEmail) {
      email = prefilledEmail
    }
  }

  Column(
    modifier = modifier.verticalScroll(rememberScrollState()),
  ) {
    Spacer(modifier = Modifier.height(8.dp))

    PaERewardHeroArt(
      paERewardType = rewardType,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 32.dp, vertical = 24.dp),
    )

    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(R.string.play_and_earn_exchange_almost_there),
      style = AGTypography.Title,
      color = Palette.White,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )

    Spacer(modifier = Modifier.height(56.dp))

    Text(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
      text = stringResource(
        R.string.play_and_earn_exchange_send_code,
        formattedAmount,
        rewardType.exchangeDisplayName,
      ).toAnnotatedString(
        AGTypography.Title.toSpanStyle().copy(color = Palette.Yellow100)
      ),
      style = AGTypography.Title,
      color = Palette.White,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp),
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_exchange_email_address_label),
        style = AGTypography.InputsL,
        color = Palette.White,
      )
      Spacer(modifier = Modifier.height(16.dp))
      EmailTextField(
        value = email,
        isError = showError,
        onValueChange = {
          email = it
          userEdited = true
          // Clear the error as soon as the user changes the input;
          // it will reappear if the email is rejected again.
          if (showError) showError = false
        },
      )
      if (showError) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = stringResource(R.string.play_and_earn_exchange_email_error),
          style = AGTypography.SmallGames,
          color = Palette.Error,
        )
      }
    }

    Spacer(modifier = Modifier.height(58.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .background(Palette.Secondary.copy(alpha = 0.2f))
        .padding(16.dp),
    ) {
      Text(
        text = stringResource(R.string.play_and_earn_exchange_email_safe_note),
        style = AGTypography.Body,
        color = Palette.White,
      )
    }

    AccentButton(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 24.dp),
      title = stringResource(R.string.play_and_earn_exchange_get_my_code_button),
      enabled = email.isNotBlank(),
      onClick = {
        if (email.isValidEmail()) {
          showError = false
          onSubmit(email)
        } else {
          showError = true
        }
      },
    )
  }
}

@Composable
private fun EmailTextField(
  value: String,
  isError: Boolean,
  onValueChange: (String) -> Unit,
) {
  val contentColor = if (isError) Palette.Error else Palette.White
  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = value,
    onValueChange = onValueChange,
    singleLine = true,
    isError = isError,
    shape = RectangleShape,
    textStyle = AGTypography.DescriptionGames.copy(color = contentColor),
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
    placeholder = {
      Text(
        text = "you@example.com",
        style = AGTypography.DescriptionGames,
      )
    },
    trailingIcon = if (isError) {
      {
        Image(
          imageVector = getError(Palette.Error),
          contentDescription = null,
          modifier = Modifier.size(20.dp),
        )
      }
    } else null,
    colors = TextFieldDefaults.outlinedTextFieldColors(
      textColor = Palette.White,
      cursorColor = Palette.White,
      errorCursorColor = Palette.Error,
      focusedBorderColor = Palette.White,
      unfocusedBorderColor = Palette.White,
      errorBorderColor = Palette.Error,
      placeholderColor = Palette.White.copy(alpha = 0.5f),
    ),
  )
}

private fun String.isValidEmail(): Boolean =
  isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

@PreviewDark
@Composable
private fun ExchangeEmailRobuxPreview() {
  ExchangeEmailScreen(
    uiState = ExchangeEmailUiState.Idle,
    rewardType = PaERewardType.ROBUX,
    formattedAmount = "$5.00",
    prefilledEmail = "",
    navigateBack = {},
    onSubmit = {},
    onRetry = {},
  )
}

@PreviewDark
@Composable
private fun ExchangeEmailDiamondsPreview() {
  ExchangeEmailScreen(
    uiState = ExchangeEmailUiState.Idle,
    rewardType = PaERewardType.DIAMONDS,
    formattedAmount = "$5.00",
    prefilledEmail = "user@example.com",
    navigateBack = {},
    onSubmit = {},
    onRetry = {},
  )
}
