package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import extensions.libs
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestsPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")
    with(project) {
      plugins.apply(libs.findPlugin("junit5").get().get().pluginId)
      dependencies.apply {
        add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
        add("testImplementation", project.project(":test"))
      }
    }
  }
}
