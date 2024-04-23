package com.appcoins.payment_method.adyen.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import com.adyen.checkout.components.ActionComponentData
import com.appcoins.payment_method.adyen.presentation.ActionResolution.Success

class AdyenActionResolveContract<T : Parcelable>(private val resolver: Class<out Activity>) :
  ActivityResultContract<T, ActionResolution>() {
  override fun createIntent(
    context: Context,
    input: T,
  ): Intent = Intent(context, resolver).apply {
    putExtra(REDIRECT_ACTION, input)
  }

  @Suppress("DEPRECATION")
  override fun parseResult(
    resultCode: Int,
    intent: Intent?,
  ): ActionResolution = when (resultCode) {
    Activity.RESULT_OK -> Success(data = intent!!.getParcelableExtra(ACTION_COMPONENT_DATA)!!)
    Activity.RESULT_CANCELED -> ActionResolution.Cancel
    else -> ActionResolution.Fail
  }

  companion object {
    const val REDIRECT_ACTION = "redirect_action"
    const val ACTION_COMPONENT_DATA = "action_component_data"
    const val ADYEN_CHECKOUT_SCHEME = "adyencheckout"
  }
}

sealed class ActionResolution {

  object Cancel : ActionResolution()

  object Fail : ActionResolution()

  data class Success(val data: ActionComponentData) : ActionResolution()
}
