package plugin

import GradlePluginId
import ModuleDependency
import TestLibraryDependency
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestsPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")
    project.plugins.apply(GradlePluginId.JUNIT5_PLUGIN)
    project.dependencies.apply {
      add("testRuntimeOnly", TestLibraryDependency.JUNIT_JUPITER_ENGINE)
      add("testImplementation", project.project(ModuleDependency.TEST))
    }
  }
}
