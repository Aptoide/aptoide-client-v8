package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import extensions.libs
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
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
        compileSdk = project.libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig {
          minSdk = project.libs.findVersion("minSdk").get().toString().toInt()
          targetSdk = project.libs.findVersion("targetSdk").get().toString().toInt()
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
            proguardFiles(
              getDefaultProguardFile("proguard-android-optimize.txt"),
              "proguard-rules.pro"
            )
          }

          debug {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("signingConfigDebug")
          }
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_17
          targetCompatibility = JavaVersion.VERSION_17
        }

        lint {
          abortOnError = LINT_ABORT_ON_ERROR
          checkDependencies = true
          xmlReport = false
          htmlReport = true
        }
      }

      is LibraryExtension -> extension.apply {
        compileSdk = project.libs.findVersion("compileSdk").get().toString().toInt()

        defaultConfig {
          minSdk = project.libs.findVersion("minSdk").get().toString().toInt()

          consumerProguardFiles("consumer-rules.pro")
        }

        buildTypes {
          release {
            proguardFiles(
              getDefaultProguardFile("proguard-android-optimize.txt"),
              "proguard-rules.pro"
            )
          }
        }

        compileOptions {
          sourceCompatibility = JavaVersion.VERSION_17
          targetCompatibility = JavaVersion.VERSION_17
        }

        lint {
          abortOnError = LINT_ABORT_ON_ERROR
        }
      }

      else -> throw GradleException("Unsupported BaseExtension type!")
    }

    with(project) {
      plugins.apply(libs.findPlugin("kotlin-android").get().get().pluginId)

      dependencies.apply {
        // kotlin
        add("implementation", libs.findLibrary("core-ktx").get())
        add("implementation", libs.findLibrary("kotlin").get())

        // coroutines
        add("implementation", libs.findLibrary("coroutines").get())
        add("implementation", libs.findLibrary("coroutines-core").get())

        //logger
        add("implementation", libs.findLibrary("timber").get())
      }
    }
  }
}
