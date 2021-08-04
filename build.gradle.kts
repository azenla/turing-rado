import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.gradle.internal.os.OperatingSystem as GradleOperatingSystem

plugins {
  kotlin("multiplatform") version "1.5.20"
  kotlin("plugin.serialization") version "1.5.20"
}

repositories {
  mavenCentral()
}

fun kotlinx(name: String, version: String): String = "org.jetbrains.kotlinx:kotlinx-$name:$version"

kotlin {
  sourceSets {
    commonMain {
      dependencies {
        api(kotlinx("serialization-json", "1.2.2"))
        api(kotlinx("coroutines-core", "1.5.1"))
      }
    }
  }

  fun KotlinNativeTarget.configureNativeTarget() {
    compilations["main"].defaultSourceSet {
      kotlin.srcDir("src/main/kotlin")
    }

    binaries {
      executable {
        entryPoint = "turing.machine.main"
      }
    }
  }

  if (GradleOperatingSystem.current().isLinux) {
    linuxX64().configureNativeTarget()
    linuxArm64().configureNativeTarget()
  }

  if (GradleOperatingSystem.current().isMacOsX) {
    macosX64().configureNativeTarget()
  }

  if (GradleOperatingSystem.current().isWindows) {
    mingwX64().configureNativeTarget()
    mingwX86().configureNativeTarget()
  }

  jvm {}
}

tasks.withType<Wrapper> {
  gradleVersion = "7.1.1"
  distributionType = Wrapper.DistributionType.ALL
}
