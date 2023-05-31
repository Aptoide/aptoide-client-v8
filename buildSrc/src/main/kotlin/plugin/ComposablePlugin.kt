package plugin

import CoreVersion
import LibraryDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ComposablePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    val extension = (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")

    val isApp = extension is ApplicationExtension

    extension.apply {
      buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
        buildConfig = true
      }

      composeOptions {
        kotlinCompilerExtensionVersion = CoreVersion.KT_COMPILER_EXTENSION
      }
    }

    project.dependencies.apply {
      // material
      add("implementation", LibraryDependency.APP_COMPAT)
      add("implementation", LibraryDependency.MATERIAL)

      // navigation
      if (isApp) {
        add("implementation", LibraryDependency.NAVIGATION_FRAGMENT_KTX)
        add("implementation", LibraryDependency.NAVIGATION_UI_KTX)
      }
      add("implementation", LibraryDependency.NAVIGATION_COMPOSE)
      add("implementation", LibraryDependency.HILT_NAV_COMPOSE)

      //compose-ui
      if (isApp) {
        add("implementation", LibraryDependency.UI_COMPOSE)
        add("implementation", LibraryDependency.UI_UTIL)
        add("implementation", LibraryDependency.ACTIVITY_COMPOSE)
      }
      add("implementation", LibraryDependency.MATERIAL_COMPOSE)
      add("implementation", LibraryDependency.ANIMATION_COMPOSE)
      add("implementation", LibraryDependency.UI_TOOLING_COMPOSE)
      add("implementation", LibraryDependency.VIEWMODEL_COMPOSE)
      add("implementation", LibraryDependency.MATERIAL_ICONS_EXTENDED)

      //imageloader
      add("implementation", LibraryDependency.COIL_COMPOSE)
    }
  }
}
