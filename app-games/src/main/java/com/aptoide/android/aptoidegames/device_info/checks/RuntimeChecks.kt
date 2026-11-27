package com.aptoide.android.aptoidegames.device_info.checks

/** Whether a class can be loaded by the system class loader. */
interface ClassProbe {
  fun isLoadable(className: String): Boolean
}

/** The class names of the frames of the current call stack. */
interface CallStack {
  fun classNames(): List<String>
}

/** The files mapped into this process's memory (the path column of `/proc/self/maps`). */
interface ProcessMaps {
  fun mappedPaths(): Set<String>
}

interface DebuggerState {
  fun isDebuggerConnected(): Boolean
  fun isWaitingForDebugger(): Boolean
}

interface TestHarnessState {
  fun isRunningInTestHarness(): Boolean
  fun isUserAMonkey(): Boolean
}

/** The Xposed framework or one of its successors (EdXposed, LSPosed) is active or installed. */
class XposedCheck(
  private val classes: ClassProbe,
  private val stack: CallStack,
  private val packages: InstalledPackages,
) : DeviceCheck {
  override val name = "xposed"
  override val group = DeviceCheckGroup.RUNTIME

  override fun fired(): Boolean =
    XPOSED_CLASSES.any(classes::isLoadable) ||
      stack.classNames().any { frame -> XPOSED_FRAME_PREFIXES.any(frame::startsWith) } ||
      XPOSED_MANAGERS.any(packages::isInstalled)

  private companion object {
    // The Xposed API entry point. EdXposed and LSPosed keep the same class for module
    // compatibility, and inject it where the app's process can load it.
    val XPOSED_CLASSES = listOf("de.robv.android.xposed.XposedBridge")

    // Frames left in the call stack by hooked calls.
    val XPOSED_FRAME_PREFIXES = listOf(
      "de.robv.android.xposed.", // Xposed API, used by all three frameworks
      "org.lsposed.lspd.", // LSPosed core
    )

    // The frameworks' manager apps.
    val XPOSED_MANAGERS = listOf(
      "de.robv.android.xposed.installer", // Xposed Installer
      "org.meowcat.edxposed.manager", // EdXposed Manager
      "org.lsposed.manager", // LSPosed Manager
    )
  }
}

/** A hooking framework's library is mapped into this process. Matches file names only. */
class HookLibrariesCheck(private val maps: ProcessMaps) : DeviceCheck {
  override val name = "hook_libraries"
  override val group = DeviceCheckGroup.RUNTIME

  override fun fired(): Boolean = maps.mappedPaths().any { path ->
    val fileName = path.substringAfterLast('/').lowercase()
    HOOK_LIBRARIES.any(fileName::contains)
  }

  private companion object {
    val HOOK_LIBRARIES = listOf(
      "frida-agent", // Frida, injected by frida-server
      "frida-gadget", // Frida, embedded in a repackaged app
      "libsubstrate", // Cydia Substrate
      "libxposed", // Xposed native library
      "xposedbridge", // Xposed bridge jar
      "liblspd", // LSPosed native library
    )
  }
}

/** A debugger is attached to this process, or the process waits for one. */
class DebuggerCheck(private val state: DebuggerState) : DeviceCheck {
  override val name = "debugger"
  override val group = DeviceCheckGroup.RUNTIME

  override fun fired(): Boolean = state.isDebuggerConnected() || state.isWaitingForDebugger()
}

/** The device runs in a test harness, or the UI is driven by the monkey tool. */
class TestHarnessCheck(private val state: TestHarnessState) : DeviceCheck {
  override val name = "test_harness"
  override val group = DeviceCheckGroup.RUNTIME

  override fun fired(): Boolean = state.isRunningInTestHarness() || state.isUserAMonkey()
}

/**
 * A release build is debuggable: the APK was rebuilt with `android:debuggable` on. Debug builds
 * are debuggable by design, so on them the check never fires.
 */
class AppDebuggableCheck(
  private val isAppDebuggable: () -> Boolean,
  private val isDebugBuild: Boolean,
) : DeviceCheck {
  override val name = "app_debuggable"
  override val group = DeviceCheckGroup.RUNTIME

  override fun fired(): Boolean = !isDebugBuild && isAppDebuggable()
}
