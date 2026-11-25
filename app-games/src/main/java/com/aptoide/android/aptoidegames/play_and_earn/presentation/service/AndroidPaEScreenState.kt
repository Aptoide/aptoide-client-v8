package com.aptoide.android.aptoidegames.play_and_earn.presentation.service

import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.aptoide.android.aptoidegames.play_and_earn.domain.sessions.PaEScreenState

/**
 * [PaEScreenState] from the platform: no permission needed for either signal. A missing system
 * service reads as "not in use" (no credit); exceptions are caught by the meter, same result.
 */
class AndroidPaEScreenState(context: Context) : PaEScreenState {

  private val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
  private val keyguardManager =
    context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager

  override val isInteractive: Boolean get() = powerManager?.isInteractive ?: false

  override val isKeyguardLocked: Boolean get() = keyguardManager?.isKeyguardLocked ?: true
}
