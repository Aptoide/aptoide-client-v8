package cm.aptoide.pt.payment_method.paypal.repository

import android.content.Context

interface PaypalHttpHeadersProvider {
  fun init(context: Context)
  fun getMetadataId(): String
}
