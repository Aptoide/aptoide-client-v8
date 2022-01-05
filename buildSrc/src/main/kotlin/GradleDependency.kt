object GradlePluginVersion {
  const val ANDROID_GRADLE = "4.2.2"
  const val KOTLIN = CoreVersion.KOTLIN
  const val KOTLIN_JVM = "1.8"
}

object GradlePluginId {
  const val ANDROID_APPLICATION = "com.android.application"
  const val ANDROID_LIBRARY = "com.android.library"
  const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
  const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
  const val KOTLIN_ANDROID_EXTENSIONS = "org.jetbrains.kotlin.android.extensions"
}

object GradleOldWayPlugins {
  const val ANDROID_GRADLE = "com.android.tools.build:gradle:${GradlePluginVersion.ANDROID_GRADLE}"
}