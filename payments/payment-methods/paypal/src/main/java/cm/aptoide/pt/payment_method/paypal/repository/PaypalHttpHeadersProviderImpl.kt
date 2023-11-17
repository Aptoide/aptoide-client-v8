package cm.aptoide.pt.payment_method.paypal.repository

import android.content.Context
import lib.android.paypal.com.magnessdk.MagnesResult
import lib.android.paypal.com.magnessdk.MagnesSDK
import lib.android.paypal.com.magnessdk.MagnesSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PaypalHttpHeadersProviderImpl @Inject constructor(
  private val magnesSettings: MagnesSettings
) : PaypalHttpHeadersProvider {

  private lateinit var magnusResult: MagnesResult

  override fun init(context: Context) {
    MagnesSDK.getInstance().setUp(magnesSettings)
    magnusResult = MagnesSDK.getInstance().collectAndSubmit(context)
  }

  override fun getMetadataId(): String =
    magnusResult.paypalClientMetaDataId
}
