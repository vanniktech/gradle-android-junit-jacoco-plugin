package com.vanniktech.android.junit.jacoco

import com.android.build.gradle.internal.SdkHandler
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll

final class GenerationPluginSpec extends Specification {
  final static ANDROID_PLUGINS = ["com.android.application", "com.android.library", "com.android.test"]
  final static COMPILE_SDK_VERSION = 27
  final static BUILD_TOOLS_VERSION = "27.0.1"
  final static APPLICATION_ID = "com.example"
  // Test fixture that emulates a local android sdk
  final static TEST_ANDROID_SDK = getClass().getResource("/android-sdk/").toURI()
  def project

  def "setup"() {
    project = ProjectBuilder.builder().build()

    // Set mock test sdk, we only need to test the plugins tasks
    SdkHandler.sTestSdkFolder = project.file TEST_ANDROID_SDK
  }

  @Unroll "#projectPlugin project"() {
    given:
    project.apply plugin: projectPlugin

    when:
    project.apply plugin: "com.vanniktech.android.junit.jacoco"

    then:
    noExceptionThrown()

    where:
    projectPlugin << ANDROID_PLUGINS
  }

  def "android - all tasks created"() {
    given:
    project.apply plugin: "com.android.application"
    project.apply plugin: "com.vanniktech.android.junit.jacoco"
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
    project.apply plugin: "com.vanniktech.android.junit.jacoco"
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
    project.apply plugin: "com.vanniktech.android.junit.jacoco"
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

      productFlavors {
        flavor1 {}
        flavor2 {}
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
    project.apply plugin: "com.vanniktech.android.junit.jacoco"
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
