package cm.aptoide.pt.feature_payment.parser

object OSPUriConstants {
  object Structure {
    const val SCHEME = "https"
    const val PATH = "/transaction"
  }

  object Parameters {
    const val PRODUCT = "product"
    const val DOMAIN = "domain"
    const val CALLBACK_URL = "callback_url"
    const val ORDER_REFERENCE = "order_reference"
    const val SIGNATURE = "signature"
    const val VALUE = "value"
    const val CURRENCY = "currency"
  }

  const val TO = "to"
  const val DATA = "data"
  const val TYPE = "type"
  const val PRODUCT_TOKEN = "product_token"
  const val SKILLS = "skills"
  const val PAYMENT_TYPE_INAPP_UNMANAGED = "INAPP_UNMANAGED"
  const val ESKILLS = "ESKILLS"
  const val NETWORK_ID_ROPSTEN = 3L
  const val NETWORK_ID_MAIN = 1L
}
