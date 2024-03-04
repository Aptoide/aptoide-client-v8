plugins {
  kotlin("jvm")
}

apply("../../versions.gradle.kts")

repositories {
  mavenCentral()
}

dependencies {
  val kspVersion = runCatching { rootProject.extra["kspVersion"] }
    .getOrDefault(project.extra["kspVersion"])
    .toString()
  implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}
