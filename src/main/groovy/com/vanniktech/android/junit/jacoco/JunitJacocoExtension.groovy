package com.vanniktech.android.junit.jacoco

/**
 * Extension for junit jacoco
 * @since 0.3.0
 */
class JunitJacocoExtension {
    /**
     * define the version of jacoco which should be used
     * @since 0.3.0
     */
    String jacocoVersion = '0.8.10'

    /**
     * subprojects that should be ignored
     * @since 0.3.0
     */
    List<String> ignoreProjects = []

    /**
     * Patterns of files that should be ignored
     * @since 0.5.0
     */
    List<String> excludes = [
      '**/R.class',
      '**/R2.class', // ButterKnife Gradle Plugin.
      '**/R$*.class',
      '**/R2$*.class', // ButterKnife Gradle Plugin.
      '**/*$$*',
      '**/*$ViewInjector*.*', // Older ButterKnife Versions.
      '**/*$ViewBinder*.*', // Older ButterKnife Versions.
      '**/*_ViewBinding*.*', // Newer ButterKnife Versions.
      '**/BuildConfig.*',
      '**/Manifest*.*',
      '**/*$Lambda$*.*', // Jacoco can not handle several "$" in class name.
      '**/*Dagger*.*', // Dagger auto-generated code.
      '**/*MembersInjector*.*', // Dagger auto-generated code.
      '**/*_Provide*Factory*.*', // Dagger auto-generated code.
      '**/*_Factory*.*', // Dagger auto-generated code.
      '**/*$JsonObjectMapper.*', // LoganSquare auto-generated code.
      '**/*$inlined$*.*', // Kotlin specific, Jacoco can not handle several "$" in class name.
      '**/*$Icepick.*', // Icepick auto-generated code.
      '**/*$StateSaver.*', // android-state auto-generated code.
      '**/*AutoValue_*.*' // AutoValue auto-generated code.
    ]

    /**
     * Whether or not to include no location classes
     * @since 0.6.0
     */
    boolean includeNoLocationClasses

    /**
     * Whether or not to include instrumentation coverage in the final global merged report.
     * Note that this will run all instrumentation tests when true.
     * @since 0.13.0
     */
    boolean includeInstrumentationCoverageInMergedReport = false

    /**
     * Whether or not to generate an xml report
     * @since 0.17.0
     */
    ReportConfig xml = new ReportConfig(true)

    /**
     * Whether or not to generate a csv report
     * @since 0.17.0
     */
    ReportConfig csv = new ReportConfig(true)

    /**
     * Whether or not to generate a html report
     * @since 0.17.0
     */
    ReportConfig html = new ReportConfig(true)
}
