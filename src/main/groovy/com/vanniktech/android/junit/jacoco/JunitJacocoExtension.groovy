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
    String jacocoVersion = '0.8.2'

    /**
     * subprojects that should be ignored
     * @since 0.3.0
     */
    String[] ignoreProjects = []

    /**
     * Patterns of files that should be ignored
     * @since 0.5.0
     */
    List<String> excludes = null

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
}
