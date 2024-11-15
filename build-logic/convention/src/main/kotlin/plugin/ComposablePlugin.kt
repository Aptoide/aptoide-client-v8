package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import extensions.libs
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposablePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")

    with(project) {
      plugins.apply(libs.findPlugin("kotlin-compose-compiler").get().get().pluginId)
    }

    val isApp = extension is ApplicationExtension

    extension.apply {
      buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
        buildConfig = true
      }
    }

    with(project) {
      dependencies.apply {
        // material
        add("implementation", libs.findLibrary("app-compat").get())
        add("implementation", libs.findLibrary("activity-ktx").get())
        add("implementation", libs.findLibrary("material").get())

        // navigation
        if (isApp) {
          add("implementation", libs.findLibrary("navigation-fragment-ktx").get())
          add("implementation", libs.findLibrary("navigation-ui-ktx").get())
        }
        add("implementation", libs.findLibrary("navigation-compose").get())
        add("implementation", libs.findLibrary("hilt-nav-compose").get())

        //compose-ui
        if (isApp) {
          add("implementation", libs.findLibrary("ui-compose").get())
          add("implementation", libs.findLibrary("compose-lifecycle").get())
          add("implementation", libs.findLibrary("ui-util").get())
          add("implementation", libs.findLibrary("activity-compose").get())
        }
        add("implementation", libs.findLibrary("material-compose").get())
        add("implementation", libs.findLibrary("animation-compose").get())
        add("implementation", libs.findLibrary("ui-tooling-compose").get())
        add("implementation", libs.findLibrary("viewmodel-compose").get())
        add("implementation", libs.findLibrary("material-icons-extended").get())

        //imageloader
        add("implementation", libs.findLibrary("coil-compose").get())
      }
    }
  }
}
