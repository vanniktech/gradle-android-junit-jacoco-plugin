# gradle-android-junit-jacoco-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin.svg)](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle plugin that generates JaCoCo reports from an Android Gradle Project. It goes over every subproject and creates the `jacocoReport` task. If you want an aggregated report from all subprojects use the `jacocoFullReport` task.

Works with the latest Gradle Android Tools version 1.3.1.

# Set up

**root/build.gradle**

```groovy
apply plugin: 'com.vanniktech.android.junit.jacoco'

buildscript {
    repositories {
        maven {
            url 'https://plugins.gradle.org/m2/'
        }
    }
    dependencies {
        classpath 'gradle.plugin.com.vanniktech:gradle-android-junit-jacoco-plugin:0.2.0'
    }
}
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

Copyright (C) 2014-2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0