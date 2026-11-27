package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.TimeUnit

internal class GetpropSystemPropertiesTest {

  private class FakeProcess(
    private val output: String,
    private val finishes: Boolean = true,
  ) : Process() {
    var destroyed = false
    var waitedMillis = -1L

    override fun getOutputStream(): OutputStream = ByteArrayOutputStream()
    override fun getInputStream(): InputStream = ByteArrayInputStream(output.toByteArray())
    override fun getErrorStream(): InputStream = ByteArrayInputStream(ByteArray(0))
    override fun waitFor(): Int = 0
    override fun waitFor(timeout: Long, unit: TimeUnit): Boolean {
      waitedMillis = unit.toMillis(timeout)
      return finishes
    }

    override fun exitValue(): Int = 0
    override fun destroy() {
      destroyed = true
    }
  }

  @Test
  fun `The property is the first line getprop prints`() = scenario {
    m Given "getprop printing a value"
    val process = FakeProcess("1\n")
    val properties = GetpropSystemProperties { process }

    m When "the property is read"
    val value = properties.get("ro.debuggable")

    m Then "it is the printed value and the process is cleaned up"
    assertEquals("1", value)
    assertTrue(process.destroyed)
  }

  @Test
  fun `A getprop that hangs is given up on after two seconds`() = scenario {
    m Given "getprop never finishing"
    val process = FakeProcess("1\n", finishes = false)
    val properties = GetpropSystemProperties { process }

    m When "the property is read"
    val value = properties.get("ro.debuggable")

    m Then "it waited two seconds, killed the process and has no value"
    assertNull(value)
    assertEquals(2_000L, process.waitedMillis)
    assertTrue(process.destroyed)
  }

  @Test
  fun `A getprop that cannot start has no value`() = scenario {
    m Given "a process that cannot be started"
    val properties = GetpropSystemProperties { throw IOException("no getprop") }

    m When "the property is read"
    val value = properties.get("ro.debuggable")

    m Then "there is no value"
    assertNull(value)
  }
}
