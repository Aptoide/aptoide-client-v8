object AndroidConfig {
  const val COMPILE_SDK = 30
  const val MIN_SDK = 21
  const val TARGET_SDK = 30
  const val BUILD_TOOLS = "30.0.2"

  const val VERSION_CODE = 1
  const val VERSION_NAME = "1.0"

  const val ID = "cm.aptoide.pt"
  const val TEST_INSTRUMENTATION_RUNNER = "android.support.test.runner.AndroidJUnitRunner"
}

interface BuildType {
  companion object {
    const val RELEASE = "release"
    const val DEBUG = "debug"
  }

  val isMinifyEnabled: Boolean
  val shrinkResources: Boolean
}

object BuildTypeDebug : BuildType {
  override val isMinifyEnabled = false
  override val shrinkResources = false
}

object BuildTypeRelease : BuildType {
  override val isMinifyEnabled = true
  override val shrinkResources = true
}