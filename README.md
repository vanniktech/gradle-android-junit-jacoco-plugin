# gradle-android-junit-jacoco-plugin

Gradle plugin that generates JaCoCo reports from an Android Gradle Project.

# Set up

## Root build.gradle

```groovy
apply plugin: "com.vanniktech.android.junit.jacoco"

buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.vanniktech:gradle-android-junit-jacoco-plugin:0.1.0"
    }
}
```

## Jacoco Reports

```groovy
./gradlew jacocoReport
```

# License

Copyright (C) 2014-2015 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0