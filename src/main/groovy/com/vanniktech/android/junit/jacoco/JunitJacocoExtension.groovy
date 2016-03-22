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
    String jacocoVersion = '0.7.2.201409121644'

    /**
     * subprojects that should be ignored
     * @since 0.3.0
     */
    String[] ignoreProjects = []
}
