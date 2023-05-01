package com.vanniktech.android.junit.jacoco

import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

final class GenerationPluginSpec extends Specification {
  final static ANDROID_PLUGINS = ["com.android.application", "com.android.library", "com.android.test", "com.android.dynamic-feature"]
  final static COMPILE_SDK_VERSION = 33
  final static BUILD_TOOLS_VERSION = "33.0.0"
  final static APPLICATION_ID = "com.vanniktech"

  def project

  def "setup"() {
    project = ProjectBuilder.builder()
        .build()

    def manifest = new File(project.projectDir, 'src/main/AndroidManifest.xml')
    manifest.parentFile.mkdirs()
    manifest.write('<manifest package="com.example.test"/>')
  }

  @Unroll "#projectPlugin project"() {
    given:
    project.apply plugin: projectPlugin

    when:
    project.plugins.apply(GenerationPlugin)

    then:
    noExceptionThrown()

    where:
    projectPlugin << ANDROID_PLUGINS
  }

  def "android - all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.plugins.apply(GenerationPlugin)
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }
    }

    when:
    project.evaluate()

    then:
    project.tasks.getByName("jacocoTestReportDebug")
  }

  def "android [buildTypes] - all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.plugins.apply(GenerationPlugin)
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }
    }

    when:
    project.evaluate()

    then:
    project.tasks.getByName("jacocoTestReportDebug")
    project.tasks.getByName("jacocoTestReportRelease")
  }

  def "android [buildTypes + productFlavors] - all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.plugins.apply(GenerationPlugin)
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }

      flavorDimensions "number"

      productFlavors {
        flavor1 {
          dimension "number"
        }
        flavor2 {
          dimension "number"
        }
      }
    }

    when:
    project.evaluate()

    then:
    project.tasks.getByName("jacocoTestReportFlavor1Debug")
    project.tasks.getByName("jacocoTestReportFlavor1Release")
    project.tasks.getByName("jacocoTestReportFlavor2Debug")
    project.tasks.getByName("jacocoTestReportFlavor2Release")
  }

  def "android [buildTypes + productFlavors + flavorDimensions] - all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.plugins.apply(GenerationPlugin)
    project.android {
      compileSdkVersion COMPILE_SDK_VERSION
      buildToolsVersion BUILD_TOOLS_VERSION

      defaultConfig {
        applicationId APPLICATION_ID
      }

      buildTypes {
        debug {}
        release {}
      }

      flavorDimensions "a", "b"

      productFlavors {
        flavor1 { dimension "a" }
        flavor2 { dimension "a" }
        flavor3 { dimension "b" }
        flavor4 { dimension "b" }
      }
    }

    when:
    project.evaluate()

    then:
    project.tasks.getByName("jacocoTestReportFlavor1Flavor3Debug")
    project.tasks.getByName("jacocoTestReportFlavor1Flavor3Release")
    project.tasks.getByName("jacocoTestReportFlavor2Flavor4Debug")
    project.tasks.getByName("jacocoTestReportFlavor2Flavor4Release")
  }
}
