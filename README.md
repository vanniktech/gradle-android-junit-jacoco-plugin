# gradle-android-junit-jacoco-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin.svg?branch=master)](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin?branch=master)
[![Codecov](https://codecov.io/github/vanniktech/gradle-android-junit-jacoco-plugin/coverage.svg?branch=master)](https://codecov.io/github/vanniktech/gradle-android-junit-jacoco-plugin?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Java 7 required](https://img.shields.io/badge/java-7-brightgreen.svg)

Gradle plugin that generates Jacoco reports from a Gradle Project. Android Application, Android Library and Java Plugins are supported by this plugin. When this plugin is applied it goes over every subproject and creates the corresponding Jacoco tasks.

### Android project

- Task `jacocoTestReport<BuildType>`
  - Executes the `test<BuildType>` task before
  - Gets executed when the `check` task is executed
  - Generated Jacoco reports can be found under `build/reports/jacoco/<BuildType>`.

Where `<BuildType>` usually is `debug` and `release` unless additional build types where specified. For instance when having `debug` and `release` build types the following tasks would be created: `jacocoTestReportDebug`, `jacocoTestReportRelease`.

### Java project

- Task `jacocoTestReport`
  - Executes the `test` task before
  - Gets executed when the `check` task is executed
  - Generated Jacoco reports can be found under `build/reports/jacoco/`.

Works with the latest Gradle Android Tools version 2.1.0. This plugin is compiled using Java 7 hence you also need Java 7 in order to use it.

# Set up

**root/build.gradle**

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.vanniktech:gradle-android-junit-jacoco-plugin:0.4.0'
    }
}

apply plugin: 'com.vanniktech.android.junit.jacoco'
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.android.junit.jacoco)

### Snapshots

Can be found [here](https://oss.sonatype.org/#nexus-search;quick~gradle-android-junit-jacoco-plugin). Current one is:

```groovy
classpath 'com.vanniktech:gradle-android-junit-jacoco-plugin:0.5.0-SNAPSHOT'
```

### Configuration (since 0.3.0)

Those are all available configurations - shown with default values and their types. More information can be found in the [Java Documentation of the Extension](src/main/groovy/com/vanniktech/android/junit/jacoco/JunitJacocoExtension.groovy).

```groovy
junitJacoco {
    jacocoVersion = '0.7.2.201409121644' // type String
    ignoreProjects = [] // type String array
}
```

# License

Copyright (C) 2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0