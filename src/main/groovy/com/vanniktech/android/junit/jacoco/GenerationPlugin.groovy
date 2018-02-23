package com.vanniktech.android.junit.jacoco

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoMerge
import org.gradle.testing.jacoco.tasks.JacocoReport

class GenerationPlugin implements Plugin<Project> {
    @Override
    void apply(final Project rootProject) {
        rootProject.extensions.create('junitJacoco', JunitJacocoExtension)

        final def hasSubProjects = rootProject.subprojects.size() > 0

        if (hasSubProjects) {
            final def (JacocoMerge mergeTask, JacocoReport mergedReportTask) = addJacocoMergeToRootProject(rootProject, rootProject.junitJacoco)

            rootProject.subprojects { subProject ->
                subProject.tasks.whenTaskAdded {
                    if (it instanceof JacocoReport) {
                        mergeTask.dependsOn it
                    }
                }

                afterEvaluate {
                    final def extension = rootProject.junitJacoco
                    addJacoco(subProject, extension, mergeTask, mergedReportTask)
                }
            }
        } else {
            rootProject.afterEvaluate {
                final def extension = rootProject.junitJacoco

                addJacoco(rootProject, extension)
            }
        }
    }

    protected static boolean addJacoco(final Project subProject, final JunitJacocoExtension extension) {
        return addJacoco(subProject, extension, null, null)
    }

    protected static boolean addJacoco(final Project subProject, final JunitJacocoExtension extension, JacocoMerge mergeTask, JacocoReport mergedReportTask) {
        if (!shouldIgnore(subProject, extension)) {
            if (isAndroidApplication(subProject) || isAndroidLibrary(subProject) ||
                    isAndroidFeature(subProject) || isAndroidInstantApp(subProject)) {
                addJacocoAndroid(subProject, extension, mergeTask, mergedReportTask)
                return true
            } else if (isJavaProject(subProject)) {
                addJacocoJava(subProject, extension, mergeTask, mergedReportTask)
                return true
            }
        }

        return false
    }

    private static void addJacocoJava(final Project subProject, final JunitJacocoExtension extension, JacocoMerge mergeTask, JacocoReport mergedReportTask) {
        subProject.plugins.apply('jacoco')

        subProject.jacoco {
            toolVersion extension.jacocoVersion
        }

        subProject.jacocoTestReport {
            dependsOn 'test'

            group = 'Reporting'
            description = 'Generate Jacoco coverage reports.'

            reports {
                xml.enabled = true
                csv.enabled = true
                html.enabled = true
            }

            classDirectories = subProject.fileTree(
                    dir: subProject.buildDir,
                    includes: ['**/classes/**/main/**'],
                    excludes: getExcludes(extension)
            )

            final def coverageSourceDirs = [
                'src/main/clojure',
                'src/main/groovy',
                'src/main/java',
                'src/main/kotlin',
                'src/main/scala'
            ]

            additionalSourceDirs = subProject.files(coverageSourceDirs)
            sourceDirectories = subProject.files(coverageSourceDirs)
            executionData = subProject.files("${subProject.buildDir}/jacoco/test.exec")

            if (mergeTask != null) {
                mergeTask.executionData += executionData
            }
            if (mergedReportTask != null) {
                mergedReportTask.classDirectories += classDirectories
                mergedReportTask.additionalSourceDirs += additionalSourceDirs
                mergedReportTask.sourceDirectories += sourceDirectories
            }
        }

        subProject.check.dependsOn 'jacocoTestReport'
    }

    private static void addJacocoAndroid(final Project subProject, final JunitJacocoExtension extension, JacocoMerge mergeTask, JacocoReport mergedReportTask) {
        subProject.plugins.apply('jacoco')

        subProject.jacoco {
            toolVersion extension.jacocoVersion
        }

        subProject.android.testOptions?.unitTests?.all {
            it.jacoco.includeNoLocationClasses = extension.includeNoLocationClasses
        }

        Collection<BaseVariant> variants = []
        if (isAndroidApplication(subProject)) {
            variants = subProject.android.applicationVariants
        } else if (isAndroidLibrary(subProject)) {
            variants = subProject.android.libraryVariants
        }

        variants.all { variant ->

            def productFlavorName = variant.getFlavorName()
            def buildTypeName = variant.getBuildType().name

            def sourceName, sourcePath
            if (!productFlavorName) {
                sourceName = sourcePath = "${buildTypeName}"
            } else {
                sourceName = "${productFlavorName}${buildTypeName.capitalize()}"
                sourcePath = "${productFlavorName}/${buildTypeName}"
            }

            final def testTaskName = "test${sourceName.capitalize()}UnitTest"
            final def taskName = "jacocoTestReport${sourceName.capitalize()}"

            subProject.task(taskName, type: JacocoReport, dependsOn: testTaskName) {
                group = 'Reporting'
                description = "Generate Jacoco coverage reports after running ${sourceName} tests."

                reports {
                    xml {
                        enabled = true
                        destination subProject.file("${subProject.buildDir}/reports/jacoco/${sourceName}/jacoco.xml")
                    }
                    csv {
                        enabled = true
                        destination subProject.file("${subProject.buildDir}/reports/jacoco/${sourceName}/jacoco.csv")
                    }
                    html {
                        enabled = true
                        destination subProject.file("${subProject.buildDir}/reports/jacoco/${sourceName}")
                    }
                }

                def classPaths = ["**/intermediates/classes/${sourcePath}/**"]
                if (isKotlinAndroid(subProject)) {
                    classPaths << "**/tmp/kotlin-classes/${sourcePath}/**"
                    if (productFlavorName) {
                        classPaths << "**/tmp/kotlin-classes/${productFlavorName}${buildTypeName.capitalize()}/**"
                    }
                }

                classDirectories = subProject.fileTree(
                        dir: subProject.buildDir,
                        includes: classPaths,
                        excludes: getExcludes(extension)
                )

                final def coverageSourceDirs = [
                        "src/main/clojure",
                        "src/main/groovy",
                        "src/main/java",
                        "src/main/kotlin",
                        "src/main/scala",
                        "src/$buildTypeName/clojure",
                        "src/$buildTypeName/groovy",
                        "src/$buildTypeName/java",
                        "src/$buildTypeName/kotlin",
                        "src/$buildTypeName/scala"
                ]

                if (productFlavorName) {
                    coverageSourceDirs.add("src/$productFlavorName/clojure")
                    coverageSourceDirs.add("src/$productFlavorName/groovy")
                    coverageSourceDirs.add("src/$productFlavorName/java")
                    coverageSourceDirs.add("src/$productFlavorName/kotlin")
                    coverageSourceDirs.add("src/$productFlavorName/scala")
                }

                additionalSourceDirs = subProject.files(coverageSourceDirs)
                sourceDirectories = subProject.files(coverageSourceDirs)
                executionData = subProject.files("${subProject.buildDir}/jacoco/${testTaskName}.exec")

                if (mergeTask != null) {
                    mergeTask.executionData += executionData
                }
                if (mergedReportTask != null) {
                    mergedReportTask.classDirectories += classDirectories
                    mergedReportTask.additionalSourceDirs += additionalSourceDirs
                    mergedReportTask.sourceDirectories += sourceDirectories
                }
            }

            subProject.check.dependsOn "${taskName}"
        }
    }

    private static addJacocoMergeToRootProject(final Project project, final JunitJacocoExtension extension) {
        project.plugins.apply('jacoco')

        project.jacoco {
            toolVersion extension.jacocoVersion
        }

        def mergeTask = project.task("mergeJacocoReports", type: JacocoMerge) {
            executionData project.files().asFileTree // Start with an empty collection.
            destinationFile project.file("${project.buildDir}/jacoco/mergedReport.exec")

            doFirst {
                // Filter non existing files.
                def realExecutionData = project.files().asFileTree

                executionData.each {
                    if (it.exists()) {
                        realExecutionData += project.files(it)
                    }
                }

                executionData = realExecutionData
            }
        }

        def mergedReportTask = project.task("jacocoTestReportMerged", type: JacocoReport, dependsOn: mergeTask) {
            executionData mergeTask.destinationFile

            reports {
                xml {
                    enabled = true
                    destination project.file("${project.buildDir}/reports/jacoco/jacoco.xml")
                }
                csv {
                    enabled = true
                    destination project.file("${project.buildDir}/reports/jacoco/jacoco.csv")
                }
                html {
                    enabled = true
                    destination project.file("${project.buildDir}/reports/jacoco")
                }
            }

            // Start with empty collections.
            classDirectories = project.files()
            additionalSourceDirs = project.files()
            sourceDirectories = project.files()
        }

        return [mergeTask, mergedReportTask]
    }

    static List<String> getExcludes(final JunitJacocoExtension extension) {
        extension.excludes == null ? [
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
        ] : extension.excludes
    }

    protected static boolean isAndroidLibrary(final Project project) {
        return project.plugins.hasPlugin('com.android.library')
    }

    protected static boolean isAndroidApplication(final Project project) {
        return project.plugins.hasPlugin('com.android.application')
    }

    protected static boolean isJavaProject(final Project project) {
        return project.plugins.hasPlugin('org.gradle.java')
    }

    protected static boolean isAndroidInstantApp(final Project project) {
        return project.plugins.hasPlugin('com.android.instantapp')
    }

    protected static boolean isAndroidFeature(final Project project) {
        return project.plugins.hasPlugin('com.android.feature')
    }

    protected static boolean isKotlinAndroid(final Project project) {
        return project.plugins.hasPlugin('org.jetbrains.kotlin.android')
    }

    private static boolean shouldIgnore(final Project project, final JunitJacocoExtension extension) {
        if (extension.ignoreProjects?.contains(project.name) || extension.ignoreProjects?.contains(project.path)) {
            // Regex could be slower.
            return true
        }

        if (extension.ignoreProjects != null) {
            for (String ignoredProject : extension.ignoreProjects) {
                if (project.name.find(ignoredProject) || project.path.find(ignoredProject)) {
                    return true
                }
            }
        }

        return false
    }
}
