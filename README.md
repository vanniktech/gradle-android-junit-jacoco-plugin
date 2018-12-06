# gradle-android-junit-jacoco-plugin

Gradle plugin that generates Jacoco reports from a Gradle Project. Android Application, Android Library, Kotlin and Java Plugins are supported by this plugin. When this plugin is applied it goes over every subproject and creates the corresponding Jacoco tasks.

### Android project

*JVM Unit-Tests*
- Task `jacocoTestReport<Flavor><BuildType>`
  - Executes the `test<Flavor><BuildType>UnitTest` task before
  - Gets executed when the `check` task is executed
  - Generated Jacoco reports can be found under `build/reports/jacoco/<Flavor>/<BuildType>`.

*Instrumented tests*
- Task `combinedTestReport<Flavor><BuildType>`
  - Executes the `test<Flavor><BuildType>UnitTest` and `create<Flavor><BuildType>CoverageReports` tasks before (JVM and instrumented tests)
  - Gets executed when the `check` task is executed
  - Generated Jacoco reports can be found under `build/reports/jacocoCombined/<Flavor>/<BuildType>`.
Note that this task is only generated, if you set `testCoverageEnabled = true` for your [build type](https://google.github.io/android-gradle-dsl/current/com.android.build.gradle.internal.dsl.BuildType.html#com.android.build.gradle.internal.dsl.BuildType:testCoverageEnabled), e.g.
```groovy
android {
  buildTypes {
    debug {
      testCoverageEnabled true
    }
  }
}
```

Where `<BuildType>` is usually `debug` & `release` unless additional build types where specified.
`<Flavor>` is optional and will be ignored if not specified.

For instance when having `debug` & `release` build types and no flavors the following tasks would be created: `jacocoTestReportDebug` and `jacocoTestReportRelease`.

When having `debug` & `release` build types and `red` & `blue` flavors the following tasks would be created: `jacocoTestReportRedDebug`, `jacocoTestReportBlueDebug`, `jacocoTestReportRedRelease` and `jacocoTestReportBlueRelease`.

### Java project

- Task `jacocoTestReport`
  - Executes the `test` task before
  - Gets executed when the `check` task is executed
  - Generated Jacoco reports can be found under `build/reports/jacoco/`.

In addition the plugin generates `mergeJacocoReports` & `jacocoTestReportMerged` tasks.

`mergeJacocoReports` will merge all of the jacoco reports together.

`jacocoTestReportMerged` will output an xml and html file for the merged report.

Works with the latest Gradle Android Tools version 2.3.3. This plugin is compiled using Java 7 hence you also need Java 7 in order to use it.

# Set up

**root/build.gradle**

```gradle
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath "com.vanniktech:gradle-android-junit-jacoco-plugin:0.13.0"
  }
}

apply plugin: "com.vanniktech.android.junit.jacoco"
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.android.junit.jacoco)

### Snapshot

```gradle
buildscript {
  repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots" }
  }
  dependencies {
    classpath "com.vanniktech:gradle-android-junit-jacoco-plugin:0.14.0-SNAPSHOT"
  }
}

apply plugin: "com.vanniktech.android.junit.jacoco"
```

### Configuration

Those are all available configurations - shown with default values and their types. More information can be found in the [Java Documentation of the Extension](src/main/groovy/com/vanniktech/android/junit/jacoco/JunitJacocoExtension.groovy).

```groovy
junitJacoco {
  jacocoVersion = '0.8.2' // type String
  ignoreProjects = [] // type String array
  includes = [] // type String List
  excludes = [] // type String List
  includeNoLocationClasses = false // type boolean
  includeInstrumentationCoverageInMergedReport = false // type boolean
}
```

# License

Copyright (C) 2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0
