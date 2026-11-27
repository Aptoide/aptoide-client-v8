package com.aptoide.android.aptoidegames.device_info.checks

import cm.aptoide.pt.test.gherkin.scenario
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class RuntimeChecksTest {

  private class FakeClassProbe(private val loadable: Set<String> = emptySet()) : ClassProbe {
    override fun isLoadable(className: String): Boolean = className in loadable
  }

  private class FakeCallStack(private val frames: List<String> = emptyList()) : CallStack {
    override fun classNames(): List<String> = frames
  }

  private class FakeProcessMaps(private val paths: Set<String> = emptySet()) : ProcessMaps {
    override fun mappedPaths(): Set<String> = paths
  }

  private class FakeDebuggerState(
    private val connected: Boolean = false,
    private val waiting: Boolean = false,
  ) : DebuggerState {
    override fun isDebuggerConnected(): Boolean = connected
    override fun isWaitingForDebugger(): Boolean = waiting
  }

  private class FakeTestHarnessState(
    private val harness: Boolean = false,
    private val monkey: Boolean = false,
  ) : TestHarnessState {
    override fun isRunningInTestHarness(): Boolean = harness
    override fun isUserAMonkey(): Boolean = monkey
  }

  private val ordinaryMaps = setOf(
    "/system/lib64/libc.so",
    "/apex/com.android.art/lib64/libart.so",
    "/data/app/~~x==/com.aptoide.android.aptoidegames.dev-y==/base.apk",
  )

  private fun xposedCheck(
    classes: ClassProbe = FakeClassProbe(),
    stack: CallStack = FakeCallStack(listOf("java.lang.Thread", "com.aptoide.Foo")),
    packages: InstalledPackages = FakeInstalledPackages(),
  ) = XposedCheck(classes, stack, packages)

  @Test
  fun `The runtime checks keep their names and never hide Play and Earn`() = scenario {
    m Given "the runtime checks"
    val checks = listOf(
      xposedCheck(),
      HookLibrariesCheck(FakeProcessMaps()),
      DebuggerCheck(FakeDebuggerState()),
      TestHarnessCheck(FakeTestHarnessState()),
      AppDebuggableCheck(isAppDebuggable = { false }, isDebugBuild = false),
    )

    m When "their names and groups are read"
    val names = checks.map { it.name }

    m Then "they are the stable runtime names, all in the runtime group"
    assertEquals(
      listOf("xposed", "hook_libraries", "debugger", "test_harness", "app_debuggable"),
      names
    )
    assertTrue(checks.all { it.group == DeviceCheckGroup.RUNTIME })
    assertTrue(checks.none { it.group.hidesPlayAndEarn })
  }

  @Test
  fun `The Xposed check does not fire on a clean process`() = scenario {
    m Given "no Xposed class, frame or manager"
    val check = xposedCheck()

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The Xposed check fires when the Xposed bridge class loads`() = scenario {
    m Given "XposedBridge loadable"
    val check = xposedCheck(
      classes = FakeClassProbe(setOf("de.robv.android.xposed.XposedBridge"))
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The Xposed check fires on an Xposed or LSPosed frame in the call stack`() = scenario {
    m Given "one stack with an Xposed frame and one with an LSPosed frame"
    val xposed = xposedCheck(
      stack = FakeCallStack(listOf("de.robv.android.xposed.XposedBridge", "com.aptoide.Foo"))
    )
    val lsposed = xposedCheck(
      stack = FakeCallStack(listOf("org.lsposed.lspd.core.Main", "com.aptoide.Foo"))
    )

    m When "the checks run"
    val fired = listOf(xposed.fired(), lsposed.fired())

    m Then "both fire"
    assertEquals(listOf(true, true), fired)
  }

  @Test
  fun `The Xposed check fires when the LSPosed manager is installed`() = scenario {
    m Given "the LSPosed manager installed"
    val check = xposedCheck(packages = FakeInstalledPackages(setOf("org.lsposed.manager")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The hook libraries check does not fire on ordinary mappings`() = scenario {
    m Given "only system libraries and the app mapped"
    val check = HookLibrariesCheck(FakeProcessMaps(ordinaryMaps))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The hook libraries check fires on a Frida agent in memory`() = scenario {
    m Given "a Frida agent mapped from an anonymous file"
    val check = HookLibrariesCheck(
      FakeProcessMaps(ordinaryMaps + "/memfd:frida-agent-64.so (deleted)")
    )

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires"
    assertTrue(fired)
  }

  @Test
  fun `The hook libraries check fires on Substrate, Xposed and LSPosed libraries`() = scenario {
    m Given "one process per hooking library"
    val checks = listOf(
      "/data/data/com.saurik.substrate/lib/libsubstrate.so",
      "/system/lib64/libxposed_art.so",
      "/data/adb/modules/zygisk_lsposed/lib/liblspd.so",
      "/system/framework/XposedBridge.jar",
    ).map { HookLibrariesCheck(FakeProcessMaps(ordinaryMaps + it)) }

    m When "the checks run"
    val fired = checks.map { it.fired() }

    m Then "every one fires"
    assertEquals(listOf(true, true, true, true), fired)
  }

  @Test
  fun `The hook libraries check only looks at file names, not folders`() = scenario {
    m Given "an ordinary library inside a folder named after Frida"
    val check = HookLibrariesCheck(FakeProcessMaps(setOf("/data/app/frida-agent/libgame.so")))

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The debugger check fires when a debugger is attached or awaited`() = scenario {
    m Given "one process with a debugger attached and one waiting for it"
    val attached = DebuggerCheck(FakeDebuggerState(connected = true))
    val waiting = DebuggerCheck(FakeDebuggerState(waiting = true))

    m When "the checks run"
    val fired = listOf(attached.fired(), waiting.fired())

    m Then "both fire"
    assertEquals(listOf(true, true), fired)
  }

  @Test
  fun `The debugger check does not fire without a debugger`() = scenario {
    m Given "no debugger"
    val check = DebuggerCheck(FakeDebuggerState())

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The test harness check fires in a test harness or under the monkey`() = scenario {
    m Given "one device in a test harness and one driven by the monkey"
    val harness = TestHarnessCheck(FakeTestHarnessState(harness = true))
    val monkey = TestHarnessCheck(FakeTestHarnessState(monkey = true))

    m When "the checks run"
    val fired = listOf(harness.fired(), monkey.fired())

    m Then "both fire"
    assertEquals(listOf(true, true), fired)
  }

  @Test
  fun `The test harness check does not fire on a normal device`() = scenario {
    m Given "a device used by a person"
    val check = TestHarnessCheck(FakeTestHarnessState())

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `The app debuggable check fires on a release build made debuggable`() = scenario {
    m Given "a release build whose manifest says debuggable"
    val check = AppDebuggableCheck(isAppDebuggable = { true }, isDebugBuild = false)

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires: the APK was rebuilt"
    assertTrue(fired)
  }

  @Test
  fun `The app debuggable check never fires on a debug build`() = scenario {
    m Given "a debug build, debuggable by design"
    var asked = false
    val check = AppDebuggableCheck(isAppDebuggable = { asked = true; true }, isDebugBuild = true)

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire and does not even look"
    assertFalse(fired)
    assertFalse(asked)
  }

  @Test
  fun `The app debuggable check does not fire on an untouched release build`() = scenario {
    m Given "a release build that is not debuggable"
    val check = AppDebuggableCheck(isAppDebuggable = { false }, isDebugBuild = false)

    m When "the check runs"
    val fired = check.fired()

    m Then "it does not fire"
    assertFalse(fired)
  }

  @Test
  fun `One package that cannot be looked up does not stop the Xposed check`() = scenario {
    m Given "a lookup that fails on the Xposed Installer and finds the LSPosed manager"
    val packages = object : InstalledPackages {
      override fun isInstalled(packageName: String): Boolean = when (packageName) {
        "de.robv.android.xposed.installer" -> throw IllegalStateException("DeadObjectException")
        "org.lsposed.manager" -> true
        else -> false
      }
    }
    val check = xposedCheck(packages = packages)

    m When "the check runs"
    val fired = check.fired()

    m Then "it fires on the LSPosed manager"
    assertTrue(fired)
  }
}
