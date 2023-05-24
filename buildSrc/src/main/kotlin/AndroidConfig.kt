object AndroidConfig {
  const val COMPILE_SDK = 33
  const val MIN_SDK = 26
  const val TARGET_SDK = 33
  const val BUILD_TOOLS = "33.0.2"

  const val VERSION_CODE = 10000
  const val VERSION_NAME = "10.0.0.0-alpha01"

  const val ID = "cm.aptoide.pt.v10"
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