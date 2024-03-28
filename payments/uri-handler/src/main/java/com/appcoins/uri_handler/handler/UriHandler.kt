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

  override fun extract(uri: Uri?): PurchaseRequest {
    uri ?: throw NullPointerException("No URI to handle")
    val parameters = mutableListOf<Pair<String, String>>().also { list ->
      uri.queryParameterNames.forEach { name ->
        uri.getQueryParameter(name)?.let { param -> list.add(name to param) }
      }
    }
    val domain = parameters.find(PurchaseUriParameters.DOMAIN)
      ?: throw MissingDataParseException("URI must contain the domain name")
    val oemId = oemIdExtractor.extractOemId(domain)
    val oemPackage = oemPackageExtractor.extractOemPackage(domain)
    return PurchaseRequest(
      uri = uri.takeIf { it.scheme == PURCHASE_URI_OSP_SCHEME },
      type = parameters.find(PurchaseUriParameters.TYPE) ?: "INAPP_UNMANAGED",
      origin = parameters.find(PurchaseUriParameters.ORIGIN) ?: "BDS",
      product = parameters.find(PurchaseUriParameters.PRODUCT),
      domain = domain,
      callbackUrl = parameters.find(PurchaseUriParameters.CALLBACK_URL),
      orderReference = parameters.find(PurchaseUriParameters.ORDER_REFERENCE),
      signature = parameters.find(PurchaseUriParameters.SIGNATURE),
      value = parameters.find(PurchaseUriParameters.VALUE)?.toDouble(),
      currency = parameters.find(PurchaseUriParameters.CURRENCY),
      oemId = oemId,
      oemPackage = oemPackage,
      metadata = parameters.find(PurchaseUriParameters.METADATA),
      productToken = parameters.find(PurchaseUriParameters.PRODUCT_TOKEN),
      skills = parameters.find(PurchaseUriParameters.SKILLS) != null,
    )
  }
}

private fun <T> List<Pair<String, T>>.find(key: String): T? = find { it.first == key }?.second

interface UriHandler {
  fun extract(uri: Uri?): PurchaseRequest
}
