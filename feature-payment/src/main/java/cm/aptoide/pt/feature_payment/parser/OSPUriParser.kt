package cm.aptoide.pt.feature_payment.parser

import android.net.Uri
import cm.aptoide.pt.feature_payment.parser.model.OSPUri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OSPUriParserImpl @Inject constructor() : OSPUriParser {

  override fun parseUri(uri: Uri): OSPUri {
    val scheme = uri.scheme
    val host = uri.host
    val path = uri.path
    val parameters = mutableListOf<Pair<String, String>>().also { list ->
      uri.queryParameterNames.forEach { name ->
        uri.getQueryParameter(name)?.let { param -> list.add(name to param) }
      }
    }
    return OSPUri(
      scheme = scheme ?: "",
      host = host ?: "",
      path = path ?: "",
      parameters = parameters
    )
  }
}

interface OSPUriParser {
  fun parseUri(uri: Uri): OSPUri
}
