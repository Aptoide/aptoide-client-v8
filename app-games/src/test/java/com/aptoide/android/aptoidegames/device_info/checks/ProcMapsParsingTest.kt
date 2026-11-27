package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.StringReader

internal class ProcMapsParsingTest {

  private fun mapsLine(path: String) = "7f0000-7f1000 r-xp 00000000 fd:00 123  $path"

  @Test
  fun `Paths are the file column of each line, anonymous regions skipped`() = scenario {
    m Given "a few maps lines, some anonymous"
    val maps = """
      7f0000-7f1000 r--p 00000000 fd:00 123  /system/lib64/libc.so
      7f2000-7f3000 rw-p 00000000 00:00 0
      7f3000-7f4000 rw-p 00000000 00:00 0    [anon:dalvik-main space]
      7f4000-7f5000 r-xp 00000000 00:05 9    /memfd:frida-agent-64.so (deleted)
    """.trimIndent()
    val seen = mutableListOf<String>()

    m When "the file is scanned with a predicate that matches nothing"
    val found = anyMappedPath(StringReader(maps)) { seen += it; false }

    m Then "only the mapped files were offered, in order"
    assertFalse(found)
    assertEquals(listOf("/system/lib64/libc.so", "/memfd:frida-agent-64.so (deleted)"), seen)
  }

  @Test
  fun `A long file is scanned to its end`() = scenario {
    m Given "fifty thousand ordinary lines with a hooking library last"
    val maps = (1..50_000).joinToString("\n") { mapsLine("/system/lib64/lib$it.so") } +
      "\n" + mapsLine("/data/local/tmp/frida-gadget.so")

    m When "the file is scanned for it"
    val found = anyMappedPath(StringReader(maps)) { it.contains("frida-gadget") }

    m Then "it is found"
    assertTrue(found)
  }

  @Test
  fun `The scan stops at the first match`() = scenario {
    m Given "three mapped files"
    val maps = listOf("/a.so", "/b.so", "/c.so").joinToString("\n", transform = ::mapsLine)
    var asked = 0

    m When "the scan matches the first one"
    val found = anyMappedPath(StringReader(maps)) { asked++; true }

    m Then "the other lines were not looked at"
    assertTrue(found)
    assertEquals(1, asked)
  }

  @Test
  fun `Reading stops at the line bound`() = scenario {
    m Given "a match on the fourth line"
    val maps = listOf("/a.so", "/b.so", "/c.so", "/frida-agent.so")
      .joinToString("\n", transform = ::mapsLine)

    m When "it is scanned with a bound of three lines"
    val found = anyMappedPath(StringReader(maps), maxLines = 3) { it.contains("frida") }

    m Then "the fourth line is never read"
    assertFalse(found)
  }

  @Test
  fun `An over-long line is cut and the next line is still read`() = scenario {
    m Given "a huge line followed by a hooking library"
    val maps = mapsLine("/" + "x".repeat(100_000)) + "\n" + mapsLine("/lib/libsubstrate.so")
    val lengths = mutableListOf<Int>()

    m When "it is scanned with a line bound of 200 characters"
    val found = anyMappedPath(StringReader(maps), maxLineLength = 200) {
      lengths += it.length
      it.contains("libsubstrate")
    }

    m Then "the huge line was cut and the library found"
    assertTrue(found)
    assertTrue(lengths.first() < 200)
  }
}
