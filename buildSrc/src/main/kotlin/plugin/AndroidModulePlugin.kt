package plugin

import AndroidConfig
import GradlePluginId
import JavaLibrary
import KeyHelper
import LibraryDependency
import TestLibraryDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidModulePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = project.extensions.getByName("android") as? BaseExtension
    when (extension) {
      is ApplicationExtension -> extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK

        defaultConfig {
          buildToolsVersion = AndroidConfig.BUILD_TOOLS
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
          create("signingConfigRelease") {
            storeFile = project.file(project.properties[KeyHelper.KEY_STORE_FILE].toString())
            storePassword = project.properties[KeyHelper.KEY_STORE_PASS].toString()
            keyAlias = project.properties[KeyHelper.KEY_ALIAS].toString()
            keyPassword = project.properties[KeyHelper.KEY_PASS].toString()
          }
        }

        buildTypes {
          release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("proguard-rules.pro")
            signingConfig = signingConfigs.getByName("signingConfigRelease")
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
      }

      is LibraryExtension -> extension.apply {
        compileSdk = AndroidConfig.COMPILE_SDK

        defaultConfig {
          minSdk = AndroidConfig.MIN_SDK

          consumerProguardFiles("consumer-rules.pro")
        }

        compileOptions {
          sourceCompatibility = JavaLibrary.SOURCE_COMPATIBILITY_JAVA_VERSION
          targetCompatibility = JavaLibrary.TARGET_COMPATIBILITY_JAVA_VERSION
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
