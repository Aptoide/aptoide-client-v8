package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringReader

internal class ProcMapsParsingTest {

  @Test
  fun `Mapped paths are the file column of each line, without anonymous regions`() = scenario {
    m Given "a few maps lines, some anonymous"
    val maps = """
      7f0000-7f1000 r--p 00000000 fd:00 123  /system/lib64/libc.so
      7f1000-7f2000 r-xp 00001000 fd:00 123  /system/lib64/libc.so
      7f2000-7f3000 rw-p 00000000 00:00 0
      7f3000-7f4000 rw-p 00000000 00:00 0    [anon:dalvik-main space]
      7f4000-7f5000 r-xp 00000000 00:05 9    /memfd:frida-agent-64.so (deleted)
    """.trimIndent()

    m When "the paths are read"
    val paths = readMappedPaths(StringReader(maps), maxChars = 10_000)

    m Then "each mapped file appears once"
    assertEquals(
      setOf("/system/lib64/libc.so", "/memfd:frida-agent-64.so (deleted)"),
      paths
    )
  }

  @Test
  fun `Reading stops at the size cap`() = scenario {
    m Given "a long maps file"
    val maps = (1..1_000).joinToString("\n") { "0-1 r--p 0 0:0 0  /lib/lib$it.so" }

    m When "it is read with a small cap"
    val paths = readMappedPaths(StringReader(maps), maxChars = 100)

    m Then "only the whole lines within the cap are kept"
    assertEquals(setOf("/lib/lib1.so", "/lib/lib2.so", "/lib/lib3.so"), paths)
  }
}
