package plugin

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.BaseExtension
import extensions.libs
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class HiltPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    (project.extensions.getByName("android") as? BaseExtension)
      .let { it as? ApplicationExtension ?: it as? LibraryExtension }
      ?: throw GradleException("Unsupported BaseExtension type!")
    with(project) {
      plugins.apply {
        apply(libs.findPlugin("kotlin-kapt").get().get().pluginId)
        apply(libs.findPlugin("hilt-android-plugin").get().get().pluginId)
      }

      dependencies.apply {
        add("implementation", libs.findLibrary("hilt").get())
        add("kapt", libs.findLibrary("hilt-dagger-compiler").get())
        add("kapt", libs.findLibrary("hilt-compiler").get())
      }
    }
  }
}
