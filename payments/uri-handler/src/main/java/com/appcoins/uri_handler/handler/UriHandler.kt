package com.appcoins.uri_handler.handler

import android.net.Uri
import com.appcoins.oem_extractor.OemIdExtractor
import com.appcoins.oem_extractor.OemPackageExtractor
import com.appcoins.payments.arch.PURCHASE_URI_OSP_SCHEME
import com.appcoins.payments.arch.PurchaseRequest
import com.appcoins.payments.arch.PurchaseUriParameters
import com.appcoins.uri_handler.handler.exception.MissingDataParseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriHandlerImpl @Inject constructor(
  private val oemIdExtractor: OemIdExtractor,
  private val oemPackageExtractor: OemPackageExtractor,
) : UriHandler {

  override fun extract(uri: Uri?): PurchaseRequest? {
    if (uri == null) return null

    val parameters = mutableListOf<Pair<String, String>>().also { list ->
      uri.queryParameterNames.forEach { name ->
        uri.getQueryParameter(name)?.let { param -> list.add(name to param) }
      }
    }
    val domain = parameters.find { it.first == PurchaseUriParameters.DOMAIN }?.second
      ?: throw MissingDataParseException("URI must contain the domain name")
    val oemId = oemIdExtractor.extractOemId(domain)
    val oemPackage = oemPackageExtractor.extractOemPackage(domain)
    return PurchaseRequest(
      uri = uri.takeIf { it.scheme == PURCHASE_URI_OSP_SCHEME },
      type = parameters.find { it.first == PurchaseUriParameters.TYPE }?.second
        ?: "INAPP_UNMANAGED",
      origin = parameters.find { it.first == PurchaseUriParameters.ORIGIN }?.second
        ?: "BDS",
      product = parameters.find { it.first == PurchaseUriParameters.PRODUCT }?.second,
      domain = domain,
      callbackUrl = parameters.find { it.first == PurchaseUriParameters.CALLBACK_URL }?.second,
      orderReference = parameters.find { it.first == PurchaseUriParameters.ORDER_REFERENCE }?.second,
      signature = parameters.find { it.first == PurchaseUriParameters.SIGNATURE }?.second,
      value = parameters.find { it.first == PurchaseUriParameters.VALUE }?.second?.toDouble(),
      currency = parameters.find { it.first == PurchaseUriParameters.CURRENCY }?.second,
      oemId = oemId,
      oemPackage = oemPackage,
      metadata = parameters.find { it.first == PurchaseUriParameters.METADATA }?.second,
      productToken = parameters.find { it.first == PurchaseUriParameters.PRODUCT_TOKEN }?.second,
      skills = parameters.find { it.first == PurchaseUriParameters.SKILLS }?.second != null,
    )
  }
}

interface UriHandler {
  fun extract(uri: Uri?): PurchaseRequest?
}
