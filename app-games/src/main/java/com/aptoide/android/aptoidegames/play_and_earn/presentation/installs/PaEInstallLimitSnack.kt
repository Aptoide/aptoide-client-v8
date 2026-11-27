package com.aptoide.android.aptoidegames.play_and_earn.presentation.installs

import androidx.annotation.StringRes
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.play_and_earn.domain.installs.PaEInstallLimit
import com.aptoide.android.aptoidegames.play_and_earn.domain.installs.PaEInstallTracker
import com.aptoide.android.aptoidegames.play_and_earn.presentation.unit_exchange_flow.formatLimitEnd
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PaEInstallLimitViewModel @Inject constructor(
  installTracker: PaEInstallTracker,
) : ViewModel() {
  val installLimitReached: SharedFlow<PaEInstallLimit> = installTracker.installLimitReached
}

/**
 * Says that the day's Play & Earn install limit was reached (AND-954), once, as the user starts
 * the install the backend refused to count. The install goes on.
 */
@Composable
fun PaEInstallLimitSnack(showSnack: (message: String, duration: SnackbarDuration) -> Unit) {
  val viewModel = hiltViewModel<PaEInstallLimitViewModel>()
  val context = LocalContext.current
  LaunchedEffect(viewModel) {
    viewModel.installLimitReached.collect { limit ->
      val message = installLimitMessage(
        retryAt = limit.retryAt,
        now = Instant.now(),
        zone = ZoneId.systemDefault(),
        locale = context.resources.configuration.locales[0],
      )
      showSnack(
        message.retryAt?.let { context.getString(message.text, it) }
          ?: context.getString(message.text),
        // Long enough to read that the game still downloads.
        SnackbarDuration.Long,
      )
    }
  }
}

/** @param retryAt when installs count again, already written for the phone: the `%1$s`. */
data class PaEInstallLimitMessage(
  @StringRes val text: Int,
  val retryAt: String? = null,
)

/**
 * The install limit's message: when installs count again, unless the backend did not say or that
 * moment is already gone.
 */
fun installLimitMessage(
  retryAt: Instant?,
  now: Instant,
  zone: ZoneId,
  locale: Locale,
): PaEInstallLimitMessage = retryAt?.takeIf { it.isAfter(now) }
  ?.let {
    PaEInstallLimitMessage(
      text = R.string.play_and_earn_install_limit_body_until,
      retryAt = formatLimitEnd(it, now, zone, locale, timeOnlyToday = false),
    )
  }
  ?: PaEInstallLimitMessage(R.string.play_and_earn_install_limit_body)
