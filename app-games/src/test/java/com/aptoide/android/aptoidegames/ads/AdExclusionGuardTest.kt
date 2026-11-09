package com.aptoide.android.aptoidegames.ads

import cm.aptoide.pt.test.gherkin.scenario
import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Structural guard for the "no MAX ads in RTB, Updates, reward, bonus or install-management
 * surfaces" rule. These screens must never reference the MAX native / MREC ad code; the only
 * shared renderer (`AppsList`) takes an explicit opt-in. The guard reads the sources, so a file
 * that is renamed or moved fails loudly instead of silently passing.
 */
internal class AdExclusionGuardTest {

  private val root = File(System.getProperty("user.dir"), "src/main/java/com/aptoide/android/aptoidegames")

  private val protectedFiles = listOf(
    "updates/presentation/UpdatesScreen.kt",
    "feature_rtb/presentation/RTBSectionView.kt",
    "feature_rtb/presentation/RTBSectionMoreView.kt",
    "feature_rtb/presentation/RTBAppsRowView.kt",
    "appview/postinstall/PostInstallRecommendsView.kt",
    "feature_apps/presentation/BonusBundleMoreScreen.kt",
    "feature_apps/presentation/BonusSectionView.kt",
    "feature_apps/presentation/SeeAllMyGamesScreen.kt",
    "installer/presentation/InstallView.kt",
    "installer/presentation/InstallViewShort.kt",
  )

  private val forbidden = listOf(
    "native_ads", "NativeAd", "MrecAdSlot", "MaxAdView", "MaxNative", "ads.search", "ads.mrec",
    "SearchAdOpportunity", "SearchAdSlotViewModel", "rememberSearchAdSlot", "AppLovin", "MaxAd",
  )

  /** Every file outside the ads package that may reference the MAX ad code. */
  private val allowedAdHosts = setOf(
    "AppViewScreen.kt",
    "BundlesView.kt",
    "CategoryDetailView.kt",
    "MoreBundleView.kt",
    "SearchScreen.kt",
    "TopChartsView.kt",
  )

  @Test
  fun `Protected surfaces never reference the MAX ad code`() = scenario {
    m Given "the list of RTB, updates, bonus and install-management sources"
    val files = protectedFiles.map { File(root, it) }

    m When "each file is read"
    val missing = files.filterNot { it.exists() }.map { it.path }
    val offenders = files.filter { it.exists() }.flatMap { file ->
      val text = file.readText()
      forbidden.filter { it in text }.map { "${file.name}: $it" }
    }

    m Then "every file exists and none mentions the ad code"
    assertTrue(missing.isEmpty(), "protected files moved or renamed: $missing")
    assertTrue(offenders.isEmpty(), "ads leaked into protected surfaces: $offenders")
  }

  @Test
  fun `Only the opted-in screens pass an ad into AppsList`() = scenario {
    m Given "every source that calls AppsList"
    val callers = root.walkTopDown().filter { it.extension == "kt" }
      .filter { "AppsList(" in it.readText() && "fun AppsList(" !in it.readText() }
      .toList()

    m When "the callers that pass collectionNativeAd are collected"
    val optedIn = callers.filter { "collectionNativeAd" in it.readText() }.map { it.name }

    m Then "only the ordinary See All screen opts in"
    assertTrue(optedIn.all { it == "MoreBundleView.kt" }, "unexpected opt-in: $optedIn")
  }

  @Test
  fun `Only the allow-listed screens reference the MAX ad code anywhere in the product sources`() =
    scenario {
      m Given "every Kotlin source outside the ads and app-open packages, in every source set"
      val sourceSets = File(System.getProperty("user.dir"), "src").listFiles { f -> f.isDirectory }
        ?.filterNot { it.name.startsWith("test") || it.name.startsWith("androidTest") }.orEmpty()
      val sources = sourceSets.flatMap { set -> set.walkTopDown().filter { it.extension == "kt" }.toList() }
        .filterNot { "/ads/" in it.path || "/app_open_ads/" in it.path }

      m When "the files mentioning the ad code are collected"
      val hosts = sources.filter { file ->
        val text = file.readText()
        listOf("ads.native_ads", "ads.mrec", "ads.search", "NativeAdSlot", "itemsWithNativeAd",
          "NativeAdListSlotEffect", "rememberSearchAdSlot").any { it in text }
      }.map { it.name }.toSet()

      m Then "no screen outside the allow list hosts an ad"
      assertTrue(hosts.isNotEmpty(), "the guard found no ad hosts; the scan is broken")
      assertTrue((hosts - allowedAdHosts).isEmpty(), "unexpected ad hosts: ${hosts - allowedAdHosts}")
    }

  @Test
  fun `Remote Config defaults select Search MREC without overriding placement controls`() = scenario {
    m Given "the shipped Remote Config defaults"
    val xml = File(System.getProperty("user.dir"), "src/main/res/xml/remote_config_defaults.xml").readText()
    val entries = Regex("<key>([^<]+)</key>\\s*<value>([^<]*)</value>")
      .findAll(xml).associate { it.groupValues[1] to it.groupValues[2].trim() }

    m When "the ad keys are inspected"
    val placementPrefixes = listOf(
      "home_native", "home_native_deep", "search_native", "search_mrec", "appview_native",
      "appview_native_bottom", "browse_native_category", "browse_native_top_charts",
      "browse_native_collection",
    )
    val enabledKeys = placementPrefixes.map { "${it}_enabled" }.filter { it in entries }
    val geoKeys = placementPrefixes.map { "${it}_excluded_geos" }.filter { it in entries }

    m Then "placement controls remain unset while Search ships fully allocated to MREC without refresh"
    assertTrue(enabledKeys.isEmpty(), "enabled defaults present: $enabledKeys")
    assertTrue(geoKeys.isEmpty(), "geo defaults present: $geoKeys")
    assertEquals("true", entries["search_ad_format_experiment_enabled"])
    assertEquals("100", entries["search_mrec_allocation"])
    assertEquals("false", entries["search_mrec_auto_refresh"])
  }
}
