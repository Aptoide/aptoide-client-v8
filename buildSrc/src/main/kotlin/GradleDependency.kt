object GradlePluginVersion {
  const val ANDROID_GRADLE = "7.0.0"
  const val KOTLIN = CoreVersion.KOTLIN
  const val KOTLIN_JVM = "1.8"
  const val HILT = "2.38.1"
}

object GradlePluginId {
  const val ANDROID_APPLICATION = "com.android.application"
  const val ANDROID_LIBRARY = "com.android.library"
  const val KOTLIN_KAPT = "kotlin-kapt"
  const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
  const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
  const val KOTLIN_ANDROID_EXTENSIONS = "org.jetbrains.kotlin.android.extensions"
  const val HILT_PLUGIN = "dagger.hilt.android.plugin"
  const val JUNIT5_PLUGIN = "de.mannodermaus.android-junit5"
}

object GradleOldWayPlugins {
  const val ANDROID_GRADLE = "com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}"
}