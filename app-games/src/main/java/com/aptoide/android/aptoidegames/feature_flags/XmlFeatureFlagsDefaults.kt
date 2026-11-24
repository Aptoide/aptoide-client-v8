package com.aptoide.android.aptoidegames.feature_flags

import android.content.Context
import cm.aptoide.pt.feature_flags.domain.FeatureFlagsDefaults
import com.aptoide.android.aptoidegames.R
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser

/**
 * The defaults shipped in `res/xml/remote_config_defaults.xml`: the values a flag has on a first
 * run with no connectivity, or when the server template does not carry the key.
 */
class XmlFeatureFlagsDefaults(private val context: Context) : FeatureFlagsDefaults {

  private val defaults by lazy {
    context.resources.getXml(R.xml.remote_config_defaults).use(::parseFeatureFlagsDefaults)
  }

  // A fresh copy per call: the flags domain overlays the remote values onto what it gets
  override fun get(): JSONObject = JSONObject(defaults.toString())
}

/**
 * Reads `<entry><key>…</key><value>…</value></entry>` pairs. An entry with no key or with an empty
 * value is skipped, so a flag cannot default to the empty string from this file.
 */
internal fun parseFeatureFlagsDefaults(parser: XmlPullParser): JSONObject {
  val defaults = JSONObject()
  var tag: String? = null
  var key: String? = null
  var value: String? = null
  var event = parser.eventType
  while (event != XmlPullParser.END_DOCUMENT) {
    when (event) {
      XmlPullParser.START_TAG -> tag = parser.name
      XmlPullParser.TEXT -> when (tag) {
        TAG_KEY -> key = parser.text
        TAG_VALUE -> value = parser.text
      }

      XmlPullParser.END_TAG -> {
        if (parser.name == TAG_ENTRY) {
          val entryKey = key
          val entryValue = value
          if (!entryKey.isNullOrBlank() && !entryValue.isNullOrEmpty()) {
            defaults.put(entryKey, entryValue)
          }
          key = null
          value = null
        }
        tag = null
      }
    }
    event = parser.next()
  }
  return defaults
}

private const val TAG_ENTRY = "entry"
private const val TAG_KEY = "key"
private const val TAG_VALUE = "value"
