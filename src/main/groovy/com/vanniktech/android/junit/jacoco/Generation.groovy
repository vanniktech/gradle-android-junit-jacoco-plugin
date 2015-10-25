package com.vanniktech.android.junit.jacoco

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

class Generation implements Plugin<Project> {
    @Override
    void apply(final Project project) {
        project.subprojects { subProject ->
            subProject.plugins.apply('jacoco')

            jacoco {
                toolVersion '0.7.2.201409121644'
            }

            subProject.task("jacocoReport", type: JacocoReport, dependsOn: 'testDebugUnitTest') {
                group = 'Reporting'
                description = 'Generate Jacoco coverage reports after running tests.'

                reports {
                    xml {
                        enabled = true
                        destination "${subProject.buildDir}/reports/jacoco/jacoco.xml"
                    }
                    html {
                        enabled = true
                        destination "${subProject.buildDir}/reports/jacoco"
                    }
                }

                classDirectories = fileTree(
                        dir: 'build/intermediates/classes/debug',
                        excludes: [
                                '**/R*.class',
                                '**/BuildConfig*'
                        ]
                )

                sourceDirectories = files('src/main/java')
                executionData = files('build/jacoco/testDebugUnitTest.exec')

                doFirst {
                    files('build/intermediates/classes/debug').getFiles().each { file ->
                        if (file.name.contains('$$')) {
                            file.renameTo(file.path.replace('$$', '$'))
                        }
                    }
                }
            }
        }
    }
}
