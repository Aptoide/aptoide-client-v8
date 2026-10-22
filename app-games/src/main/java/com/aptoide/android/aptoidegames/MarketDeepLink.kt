package com.aptoide.android.aptoidegames

import cm.aptoide.pt.feature_apps.domain.AppSource
import com.aptoide.android.aptoidegames.appview.buildAppViewRoute
import com.aptoide.android.aptoidegames.search.presentation.buildSearchRoute
import java.net.URLDecoder
import java.net.URLEncoder

private const val MARKET_PREFIX = "market://"
private const val DETAILS_HOST = "details"
private const val SEARCH_HOST = "search"
private const val PACKAGE_PARAM = "id"
private const val QUERY_PARAM = "q"

/** Android's own limit on a package name. */
private const val MAX_PACKAGE_NAME_LENGTH = 255

/** Longer queries are cut here: the value feeds a nav route, a request and analytics. */
internal const val MAX_SEARCH_QUERY_LENGTH = 256

private val packageNameRegex = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")

/**
 * Maps a Play-style `market://` URI, as fired by other apps to open their store page, to this
 * client's own deep link so the navigation graph can route it. Returns null for anything the client
 * does not handle: other hosts, a missing or invalid package name, a blank search query.
 *
 * Kept free of `android.net.Uri` so it runs in plain JVM unit tests.
 */
fun marketUriToDeepLink(
  marketUri: String,
  scheme: String = BuildConfig.DEEP_LINK_SCHEMA,
): String? {
  if (!marketUri.startsWith(MARKET_PREFIX, ignoreCase = true)) return null
  val rest = marketUri.substring(MARKET_PREFIX.length).substringBefore('#')
  val host = rest.substringBefore('?').substringBefore('/').lowercase()
  val params = parseQuery(rest.substringAfter('?', ""))

  return when (host) {
    DETAILS_HOST -> params[PACKAGE_PARAM]
      ?.takeIf { it.length <= MAX_PACKAGE_NAME_LENGTH && packageNameRegex.matches(it) }
      ?.let { scheme + buildAppViewRoute(AppSource.of(appId = null, packageName = it)) }

    SEARCH_HOST -> params[QUERY_PARAM]
      ?.trim()
      ?.take(MAX_SEARCH_QUERY_LENGTH)
      ?.takeIf(String::isNotEmpty)
      ?.let { scheme + buildSearchRoute(it.percentEncoded()) }

    else -> null
  }
}

// The first occurrence of a parameter wins, as android.net.Uri.getQueryParameter resolves it.
private fun parseQuery(query: String): Map<String, String> = buildMap {
  query.split('&')
    .filter { it.isNotEmpty() }
    .forEach { pair ->
      putIfAbsent(pair.substringBefore('='), pair.substringAfter('=', "").formDecoded())
    }
}

private fun String.formDecoded(): String =
  runCatching { URLDecoder.decode(this, Charsets.UTF_8.name()) }.getOrDefault(this)

// Navigation decodes deep-link arguments with Uri.decode, which keeps "+" literal; a space must
// therefore travel as %20, not as the form-encoding "+" that URLEncoder produces.
private fun String.percentEncoded(): String =
  URLEncoder.encode(this, Charsets.UTF_8.name()).replace("+", "%20")
