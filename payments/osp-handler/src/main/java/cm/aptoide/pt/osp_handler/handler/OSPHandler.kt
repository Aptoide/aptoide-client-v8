package cm.aptoide.pt.osp_handler.handler

import android.net.Uri
import cm.aptoide.pt.oem_extractor.OemIdExtractor
import cm.aptoide.pt.osp_handler.di.DefaultOemIdPackageName
import cm.aptoide.pt.osp_handler.handler.OSPUriConstants.Parameters
import cm.aptoide.pt.osp_handler.handler.exception.MissingDataParseException
import cm.aptoide.pt.payment_manager.manager.domain.PurchaseRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OSPHandlerImpl @Inject constructor(
  private val oemIdExtractor: OemIdExtractor,
  @DefaultOemIdPackageName private val oemIdDefaultPackageName: String?,
) : OSPHandler {

  override fun extract(uri: Uri?): PurchaseRequest? {
    if (uri == null) return null

    val scheme = uri.scheme
    val host = uri.host
    val path = uri.path
    val parameters = mutableListOf<Pair<String, String>>().also { list ->
      uri.queryParameterNames.forEach { name ->
        uri.getQueryParameter(name)?.let { param -> list.add(name to param) }
      }
    }
    val domain = parameters.find { it.first == Parameters.DOMAIN }?.second
      ?: throw MissingDataParseException("OSP uri must contain the domain name")
    val oemId = oemIdExtractor.extractOemId(oemIdDefaultPackageName ?: domain)
    return PurchaseRequest(
      ospUri = uri,
      scheme = scheme ?: throw MissingDataParseException("OSP uri must contain the scheme"),
      host = host ?: throw MissingDataParseException("OSP uri must contain the host"),
      path = path ?: throw MissingDataParseException("OSP uri must contain the path"),
      product = parameters.find { it.first == Parameters.PRODUCT }?.second,
      domain = domain,
      callbackUrl = parameters.find { it.first == Parameters.CALLBACK_URL }?.second,
      orderReference = parameters.find { it.first == Parameters.ORDER_REFERENCE }?.second,
      signature = parameters.find { it.first == Parameters.SIGNATURE }?.second,
      value = parameters.find { it.first == Parameters.VALUE }?.second?.toDouble(),
      currency = parameters.find { it.first == Parameters.CURRENCY }?.second,
      oemId = oemId
    )
  }
}

interface OSPHandler {
  fun extract(uri: Uri?): PurchaseRequest?
}
