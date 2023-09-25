package cm.aptoide.pt.feature_payment.parser.model

import cm.aptoide.pt.feature_payment.parser.OSPUriConstants.Parameters
import cm.aptoide.pt.feature_payment.parser.exception.DomainNameMissingException

data class OSPUri(
  val scheme: String,
  val host: String,
  val path: String,
  val product: String?,
  val domain: String,
  val callbackUrl: String?,
  val orderReference: String?,
  val signature: String?,
  val value: Int?,
  val currency: String?,
) {

  /**
   * Throws ProductNameMissingException if parameters do not contain the product name
   */
  @Throws(DomainNameMissingException::class)
  constructor(
    scheme: String,
    host: String,
    path: String,
    parameters: List<Pair<String, String>>,
  ) : this(
    scheme = scheme,
    host = host,
    path = path,
    product = parameters.find { it.first == Parameters.PRODUCT }?.second,
    domain = parameters.find { it.first == Parameters.DOMAIN }?.second ?: throw DomainNameMissingException("OSP uri must contain the domain name"),
    callbackUrl = parameters.find { it.first == Parameters.CALLBACK_URL }?.second,
    orderReference = parameters.find { it.first == Parameters.ORDER_REFERENCE }?.second,
    signature = parameters.find { it.first == Parameters.SIGNATURE }?.second,
    value = parameters.find { it.first == Parameters.VALUE }?.second?.toInt(),
    currency = parameters.find { it.first == Parameters.CURRENCY }?.second,
  )
}
