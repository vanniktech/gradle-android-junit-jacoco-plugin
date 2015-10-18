# gradle-android-junit-jacoco-plugin

[![Build Status](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin.svg)](https://travis-ci.org/vanniktech/gradle-android-junit-jacoco-plugin)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Gradle plugin that generates JaCoCo reports from an Android Gradle Project. It goes over every subproject and creates the `jacocoReport` task.

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
        classpath 'gradle.plugin.com.vanniktech:gradle-android-junit-jacoco-plugin:0.1.0'
    }
}
```

## Get reports

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

# License

Copyright (C) 2014-2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0