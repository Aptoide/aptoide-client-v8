package com.aptoide.android.aptoidegames.feature_flags

import org.json.JSONArray
import org.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.kxml2.io.KXmlParser
import java.io.File
import java.io.StringReader

class FeatureFlagsDefaultsXmlTest {

  @Test
  fun `The shipped defaults file yields the values the code falls back to`() {
    val xml = File(System.getProperty("user.dir"), "src/main/res/xml/remote_config_defaults.xml")
      .readText()

    val defaults = parse(xml)

    assertEquals("false", defaults.getString("show_play_and_earn"))
    assertEquals("30", defaults.getString("appopen_cap_window_minutes"))
    assertEquals("2", defaults.getString("home_native_position"))
    // JSON-valued defaults stay as the JSON text the flags domain parses on read
    assertEquals("US", JSONArray(defaults.getString("appopen_excluded_geos")).getString(0))
    assertEquals(135588859, JSONObject(defaults.getString("pixelblast_app")).getInt("size"))
  }

  @Test
  fun `Entries without a key or a value are skipped`() {
    val defaults = parse(
      """
      <defaultsMap>
        <entry><key>kept</key><value>1</value></entry>
        <entry><key>no_value</key></entry>
        <entry><value>orphan</value></entry>
        <entry><key>empty</key><value></value></entry>
      </defaultsMap>
      """.trimIndent()
    )

    assertEquals("1", defaults.getString("kept"))
    assertFalse(defaults.has("no_value"))
    assertEquals(1, defaults.length())
  }

  private fun parse(xml: String): JSONObject =
    parseFeatureFlagsDefaults(KXmlParser().apply { setInput(StringReader(xml)) })
}
