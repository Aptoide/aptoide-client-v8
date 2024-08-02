package plugin

import AndroidConfig
import GradlePluginId
import JavaLibrary
import LibraryDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidModulePlugin : Plugin<Project> {

  companion object {
    private val LINT_ABORT_ON_ERROR = false
  }

  override fun apply(project: Project) {
    val extension = project.extensions.getByName("android") as? BaseExtension
    when (extension) {
      is ApplicationExtension -> extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK

        defaultConfig {
          minSdk = AndroidConfig.MIN_SDK
          targetSdk = AndroidConfig.TARGET_SDK
        }

        signingConfigs {
          create("signingConfigDebug") {
            storeFile = project.file("../debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
          }
        }

        buildTypes {
          release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
          }

          debug {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("signingConfigDebug")
          }
        }

        compileOptions {
          sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
          targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
        }

        lint {
          abortOnError = LINT_ABORT_ON_ERROR
          checkDependencies = true
          xmlReport = false
          htmlReport = true
        }
      }

      is LibraryExtension -> extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK

        defaultConfig {
          minSdk = AndroidConfig.MIN_SDK

          consumerProguardFiles("consumer-rules.pro")
        }

        buildTypes {
          release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
          }
        }

        compileOptions {
          sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
          targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
        }

        lint {
          abortOnError = LINT_ABORT_ON_ERROR
        }
      }

      else -> throw GradleException("Unsupported BaseExtension type!")
    }

    project.plugins.apply {
      apply(GradlePluginId.KOTLIN_ANDROID)
    }
    project.dependencies.apply {
      // kotlin
      add("implementation", LibraryDependency.CORE_KTX)
      add("implementation", LibraryDependency.KOTLIN)

      // coroutines
      add("implementation", LibraryDependency.COROUTINES)
      add("implementation", LibraryDependency.COROUTINES_CORE)

      //logger
      add("implementation", LibraryDependency.TIMBER)
    }
  }
}
