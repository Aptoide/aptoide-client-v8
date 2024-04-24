package com.appcoins.payments.methods.paypal.repository

import android.content.Context
import lib.android.paypal.com.magnessdk.Environment
import lib.android.paypal.com.magnessdk.MagnesResult
import lib.android.paypal.com.magnessdk.MagnesSDK
import lib.android.paypal.com.magnessdk.MagnesSettings

internal class PaypalHttpHeadersProviderImpl(
  context: Context,
  magnesEnvironment: Environment,
) : PaypalHttpHeadersProvider {

  private val magnusResult: MagnesResult

  init {
    val magnesSettings = MagnesSettings.Builder(context)
      .setMagnesEnvironment(magnesEnvironment)
      .build()

    MagnesSDK.getInstance().setUp(magnesSettings)
    magnusResult = MagnesSDK.getInstance().collectAndSubmit(context)
  }

  override fun getMetadataId(): String =
    magnusResult.paypalClientMetaDataId
}
