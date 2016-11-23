package com.vanniktech.android.junit.jacoco

/**
 * Extension for junit jacoco
 * @since 0.3.0
 */
class JunitJacocoExtension {
    /**
     * define the version of jacoco which should be used
     * @since 0.6.0
     */
    String jacocoVersion = '0.7.7.201606060606'

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
}
