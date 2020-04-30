# Change Log

Version 0.17.0 *(In development)*
---------------------------------

Version 0.16.0 *(2020-03-22)*
-----------------------------

- "Plugin not found" when using snapshot version [\#159](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/issues/159)
- Fix java classes being skipped in AGP \>= 3.4 [\#166](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/166) ([jeppeman](https://github.com/jeppeman))
- Fix slow file traversal in configuration phase [\#163](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/163) ([fo2rist](https://github.com/fo2rist))
- Fix "Could not get unknown property 'libraryVariants'" for dynamic-feature modules [\#160](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/160) ([igorwojda](https://github.com/igorwojda))
- Add support for Android "dynamic-feature" module type [\#158](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/158) ([igorwojda](https://github.com/igorwojda))

Version 0.15.0 *(2019-05-27)*
-----------------------------

- Fixed "No signature of method: org.gradle.api.internal.file.CompositeFileCollection$1.setFrom\(\)" bug for mergeJacocoReports task. [\#157](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/157) ([vasdeepika](https://github.com/vasdeepika))
- Fix Gradle 6.0 deprecation warnings. [\#155](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/155) ([vanniktech](https://github.com/vanniktech))

Version 0.14.0 *(2019-04-30)*
-----------------------------

- Update dependencies. [\#154](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/154) ([vanniktech](https://github.com/vanniktech))
- Add support for Gradle 5 [\#153](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/153) ([henriquenfaria](https://github.com/henriquenfaria))
- Fix StackOverflowError with Gradle 5.0 regarding FileCollection [\#151](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/151) ([Laimiux](https://github.com/Laimiux))
- Cope with new Android DSL to configure includeNoLocationClasses [\#150](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/150) ([aygalinc](https://github.com/aygalinc))
- Add support for kotlin multiplatform plugin [\#141](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/141) ([henriquenfaria](https://github.com/henriquenfaria))
- Remove sudo: false from travis config. [\#135](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/135) ([vanniktech](https://github.com/vanniktech))
- Don't run tests when creating the merged test coverage report. Instead it's required to run specific tests manually before. [\#134](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/134) ([vRallev](https://github.com/vRallev))

Version 0.13.0 *(2018-10-11)*
-----------------------------

- Update Gradle Maven Publish Plugin to 0.6.0 [\#132](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/132) ([vanniktech](https://github.com/vanniktech))
- Integrate instrumentation tests [\#131](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/131) ([vRallev](https://github.com/vRallev))
- Don't apply the plugin for Android test projects [\#130](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/130) ([vRallev](https://github.com/vRallev))
- Use Jacoco 0.8.2 by default [\#129](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/129) ([vRallev](https://github.com/vRallev))
- Reapply the Jacoco version after the project has been evaluated [\#128](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/128) ([vRallev](https://github.com/vRallev))
- Update Plugin Publish Plugin to 0.10.0 [\#126](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/126) ([vanniktech](https://github.com/vanniktech))
- Support all the Android / Java plugins. [\#124](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/124) ([vanniktech](https://github.com/vanniktech))

Big thanks to Ralf for all of his work!

Version 0.12.0 *(2018-06-30)*
-----------------------------

- Add the new path for Java class files in newer Android Gradle Plugin … [\#122](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/122) ([vRallev](https://github.com/vRallev))
- Fix id for Gradle Plugin that was added in \#118 [\#120](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/120) ([vanniktech](https://github.com/vanniktech))
- Unify setup, improve a few things and bump versions. [\#118](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/118) ([vanniktech](https://github.com/vanniktech))
- Use Gradle Maven Publish Plugin for publishing. [\#117](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/117) ([vanniktech](https://github.com/vanniktech))
- Instant app support [\#111](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/111) ([dazza5000](https://github.com/dazza5000))

Version 0.11.0 *(2017-12-10)*
-----------------------------

- Only include main sources in the classes directory for Java projects. [\#105](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/105) ([vanniktech](https://github.com/vanniktech))
- Allow ignoring the module path and name [\#103](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/103) ([vRallev](https://github.com/vRallev))
- Ignore classes generated by the android-state library [\#102](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/102) ([vRallev](https://github.com/vRallev))
- Add correct Kotlin class path [\#101](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/101) ([vRallev](https://github.com/vRallev))
- Update Android License hash. [\#98](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/98) ([vanniktech](https://github.com/vanniktech))
- Update plugin-publish-plugin [\#97](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/97) ([timyates](https://github.com/timyates))

Version 0.10.0 *(2017-10-08)*
-----------------------------

- Update JUnit Jacoco Gradle Plugin to 0.9.0 [\#96](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/96) ([vanniktech](https://github.com/vanniktech))
- Generate csv reports [\#95](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/95) ([alexrwegener](https://github.com/alexrwegener))
- Remove deprecated method calls [\#94](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/94) ([vRallev](https://github.com/vRallev))

Version 0.9.0 *(2017-09-12)*
----------------------------

- Fix the class file location for the Android Gradle Plugin 3.0.0 [\#90](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/90) ([vRallev](https://github.com/vRallev))
- Merged test code coverage report [\#89](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/89) ([vRallev](https://github.com/vRallev))
- Autoexclude \*\_Factory classes that are generated by Dagger 2. [\#88](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/88) ([vanniktech](https://github.com/vanniktech))
- Don't clean build again when deploying SNAPSHOTS. [\#86](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/86) ([vanniktech](https://github.com/vanniktech))
- Update Jacoco Gradle Plugin to 0.8.0 [\#85](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/85) ([vanniktech](https://github.com/vanniktech))

Version 0.8.0 *(2017-08-14)*
----------------------------

- Fix Jacoco generation for Java. [\#82](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/82) ([vanniktech](https://github.com/vanniktech))

Version 0.7.0 *(2017-08-02)*
----------------------------

- Support popular JVM languages [\#75](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/75) ([jaredsburrows](https://github.com/jaredsburrows))
- \[Tests\] - Add tests for GenerationPlugin [\#71](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/71) ([jaredsburrows](https://github.com/jaredsburrows))
- Don't add jacoco tasks for ignored build variants [\#69](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/69) ([passsy](https://github.com/passsy))

Version 0.6.0 *(2017-03-20)*
----------------------------

- Fix includeNoLocationClasses. [\#61](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/61) ([vanniktech](https://github.com/vanniktech))
- Add a few more default excludes. [\#58](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/58) ([vanniktech](https://github.com/vanniktech))
- Add extension for includeNoLocationClasses [\#55](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/55) ([vanniktech](https://github.com/vanniktech))
- Adding AutoValue to exclusion list [\#54](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/54) ([setheclark](https://github.com/setheclark))
- Added several exclusions when generating report. [\#41](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/41) ([zsavely](https://github.com/zsavely))

Version 0.5.0 *(2016-07-17)*
----------------------------

- Add excludes to JunitJacoco extension [\#37](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/37) ([vanniktech](https://github.com/vanniktech))
- Clean up tests [\#36](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/36) ([vanniktech](https://github.com/vanniktech))
- Restore flavor iterations [\#34](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/34) ([outlying](https://github.com/outlying)) - many thanks to him
- Update to Gradle 2.14 [\#33](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/33) ([vanniktech](https://github.com/vanniktech))
- Fix Travis after\_success. [\#32](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/32) ([vanniktech](https://github.com/vanniktech))
- Update to Gradle 2.13 [\#31](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/31) ([vanniktech](https://github.com/vanniktech))
- Add Codecov file [\#30](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/30) ([vanniktech](https://github.com/vanniktech))
- Update Android Gradle Build Tools to 2.1.0 [\#29](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/29) ([vanniktech](https://github.com/vanniktech))
- Add Codecov Coverage [\#26](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/26) ([vanniktech](https://github.com/vanniktech))

Version 0.4.0 *(2016-04-10)*
--------------------------------

- Add Single project support [\#25](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/25) ([vanniktech](https://github.com/vanniktech))

Version 0.3.0 *(2016-03-23)*
--------------------------------

- Remove merged report [\#24](https://github.com/vanniktech/OnActivityResult/pull/24) ([vanniktech](https://github.com/vanniktech))
- Add support for Java projects [\#24](https://github.com/vanniktech/OnActivityResult/pull/24) ([vanniktech](https://github.com/vanniktech))
- Add Android build variant specific Jacoco tasks [\#24](https://github.com/vanniktech/OnActivityResult/pull/24) ([vanniktech](https://github.com/vanniktech))
- Hook Jacoco tasks into check task [\#24](https://github.com/vanniktech/OnActivityResult/pull/24) ([vanniktech](https://github.com/vanniktech))
- Add some more excludes [\#21](https://github.com/vanniktech/gradle-android-junit-jacoco-plugin/pull/21) ([vanniktech](https://github.com/vanniktech))

**Note: Since tasks were removed and added please have a look at the [README](README.md) to get an overview of all the features of version 0.3.0**

Version 0.2.0 *(2016-11-01)*
----------------------------

- Add merged report

Version 0.1.1 *(2015-10-25)*
----------------------------

- Fix Jacoco version specification

Version 0.1.0 *(2015-10-15)*
----------------------------

- Initial release
