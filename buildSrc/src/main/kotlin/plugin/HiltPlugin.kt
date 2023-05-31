package plugin

import GradlePluginId
import LibraryDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")
    project.plugins.apply {
      apply(GradlePluginId.KOTLIN_KAPT)
      apply(GradlePluginId.HILT_PLUGIN)
    }
    project.dependencies.apply {
      add("implementation", LibraryDependency.HILT)
      add("kapt", LibraryDependency.HILT_COMPILER)
    }
  }
}
