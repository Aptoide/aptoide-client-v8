package com.aptoide.android.aptoidegames.play_and_earn.presentation.analytics

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class PaEDeviceRiskParamsTest {

  private val allNames = listOf(
    "emulator_build", "emulator_props", "emulator_files", "unknown_board", "root_files",
    "su_binary", "root_apps", "dangerous_props", "rw_system_paths", "test_keys", "xposed",
    "hook_libraries", "debugger", "test_harness", "app_debuggable",
  )

  @Test
  fun `Names are sorted and comma-separated`() = scenario {
    m Given "fired names out of order"
    val names = listOf("xposed", "debugger", "root_apps")

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "checks is the sorted list and count the number of names"
    assertEquals(mapOf("checks" to "debugger,root_apps,xposed", "count" to 3), params)
  }

  @Test
  fun `Every check name together splits into two parameters and loses nothing`() = scenario {
    m Given "every check name"
    val names = allNames

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "the values fit Firebase's limit, split between names"
    val chunks = listOf(params["checks"], params["checks_2"]).map { it as String }
    assertTrue(chunks.all { it.length <= 100 })
    assertEquals(allNames.sorted(), chunks.flatMap { it.split(",") })
    assertEquals(setOf("checks", "checks_2", "count"), params.keys)
    assertEquals(15, params["count"])
  }

  @Test
  fun `A value of exactly 100 characters stays in one parameter`() = scenario {
    m Given "two names that joined by a comma are 100 characters"
    val names = listOf("a".repeat(49), "b".repeat(50))

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "they share the checks parameter"
    assertEquals(mapOf("checks" to "a".repeat(49) + "," + "b".repeat(50), "count" to 2), params)
  }

  @Test
  fun `A value of 101 characters splits`() = scenario {
    m Given "two names that joined by a comma are 101 characters"
    val names = listOf("a".repeat(50), "b".repeat(50))

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "the second name moves to checks_2"
    assertEquals(
      mapOf("checks" to "a".repeat(50), "checks_2" to "b".repeat(50), "count" to 2),
      params
    )
  }

  @Test
  fun `A long list splits into more than two parameters`() = scenario {
    m Given "ten names of 40 characters, two of which fit per value"
    val names = ('a'..'j').map { it.toString().repeat(40) }

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "they fill checks to checks_5, two names each"
    assertEquals(
      listOf("checks", "checks_2", "checks_3", "checks_4", "checks_5", "count"),
      params.keys.sorted()
    )
    assertEquals("i".repeat(40) + "," + "j".repeat(40), params["checks_5"])
    assertEquals(10, params["count"])
  }

  @Test
  fun `A single name longer than 100 characters is cut to 100`() = scenario {
    m Given "one name of 150 characters"
    val names = listOf("x".repeat(150))

    m When "the parameters are built"
    val params = deviceRiskParams(names)

    m Then "checks holds its first 100 characters"
    assertEquals(mapOf("checks" to "x".repeat(100), "count" to 1), params)
  }
}
