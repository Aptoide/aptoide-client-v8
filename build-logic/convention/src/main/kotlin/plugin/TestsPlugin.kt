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
    project.extensions.getByName("android")
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported Extension type!")
    with(project) {
      plugins.apply(libs.findPlugin("junit5").get().get().pluginId)
      dependencies.apply {
        add("testRuntimeOnly", libs.findLibrary("junit-jupiter-engine").get())
        // Gradle no longer puts the launcher on the test runtime classpath implicitly;
        // without it every test task fails with "Failed to load JUnit Platform"
        add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
        add("testImplementation", project.project(":test"))
      }
    }
  }
}
