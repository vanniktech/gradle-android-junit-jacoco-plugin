# gradle-android-junit-jacoco-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin.svg?branch=master)](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
![Java 8 required](https://img.shields.io/badge/java-8-brightgreen.svg)

Gradle plugin that generates JaCoCo reports from an Android Gradle Project. It goes over every subproject and creates the `jacocoReport` task. If you want an aggregated report from all subprojects use the `jacocoFullReport` task.

Works with the latest Gradle Android Tools version 1.3.1. This plugin is compiled using Java 8 hence you also need Java 8 in order to use it.

# Set up

**root/build.gradle**

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.vanniktech:gradle-android-junit-jacoco-plugin:0.2.0'
    }
}

apply plugin: 'com.vanniktech.android.junit.jacoco'
```

Information: [This plugin is also available on Gradle plugins](https://plugins.gradle.org/plugin/com.vanniktech.android.junit.jacoco)

### Snapshots

Can be found [here](https://oss.sonatype.org/#nexus-search;quick~gradle-android-junit-jacoco-plugin). Current one is:

```groovy
classpath 'com.vanniktech:gradle-android-junit-jacoco-plugin:0.2.1-SNAPSHOT'
```

## Get reports for each subproject

```groovy
./gradlew jacocoReport
```

**XML reports**

```
<subproject>/build/reports/jacoco/jacoco.xml
```

**HTML reports**

```
<subproject>/build/reports/jacoco/index.html
```

**Exec files**

```
<subproject>/build/jacoco/testDebugUnitTest.exec
<subproject>/build/jacoco/testReleaseUnitTest.exec
```

## Get aggreated report from all subprojects

```groovy
./gradlew jacocoFullReport
```

**XML reports**

```
<root>/build/reports/jacoco/full/jacoco.xml
```

**HTML reports**

```
<root>/build/reports/jacoco/full/index.html
```

# License

Copyright (C) 2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0