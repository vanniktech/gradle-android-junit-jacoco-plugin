package com.vanniktech.android.junit.jacoco

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.testing.jacoco.tasks.JacocoReport

class Generation implements Plugin<Project> {
    @Override
    void apply(final Project rootProject) {
        rootProject.extensions.create('junitJacoco', JunitJacocoExtension)

        final def hasSubProjects = rootProject.subprojects.size() > 0

        if (hasSubProjects) {
            rootProject.subprojects { subProject ->
                afterEvaluate {
                    final def extension = rootProject.junitJacoco

                    addJacoco(subProject, extension)
                }
            }
        } else {
            rootProject.afterEvaluate {
                final def extension = rootProject.junitJacoco

                addJacoco(rootProject, extension)
            }
        }
    }

    protected static boolean addJacoco(final Project subProject,
            final JunitJacocoExtension extension) {
        if (!shouldIgnore(subProject, extension)) {
            if (isAndroidApplication(subProject) || isAndroidLibrary(subProject)) {
                addJacocoAndroid(subProject, extension)
                return true
            } else if (isJavaProject(subProject)) {
                addJacocoJava(subProject, extension)
                return true
            }
        }

        return false
    }

    private static void addJacocoJava(final Project subProject, final JunitJacocoExtension extension) {
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
                html.enabled = true
            }

            classDirectories = subProject.fileTree(
                    dir: 'build/classes/main/',
                    excludes: getExcludes(extension)
            )

            final def coverageSourceDirs = [
                    'src/main/java',
            ]

            additionalSourceDirs = subProject.files(coverageSourceDirs)
            sourceDirectories = subProject.files(coverageSourceDirs)
            executionData = subProject.files("${subProject.buildDir}/jacoco/test.exec")
        }

        subProject.check.dependsOn 'jacocoTestReport'
    }

    private static void addJacocoAndroid(final Project subProject, final JunitJacocoExtension extension) {
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
                        destination "${subProject.buildDir}/reports/jacoco/${sourceName}/jacoco.xml"
                    }
                    html {
                        enabled = true
                        destination "${subProject.buildDir}/reports/jacoco/${sourceName}"
                    }
                }

                classDirectories = subProject.fileTree(
                        dir: "${subProject.buildDir}/intermediates/classes/${sourcePath}",
                        excludes: getExcludes(extension)
                )

                final def coverageSourceDirs = [
                        "src/main/java",
                        "src/$buildTypeName/java"
                ]

                if (productFlavorName) {
                    coverageSourceDirs.add("src/$productFlavorName/java")
                }

                additionalSourceDirs = subProject.files(coverageSourceDirs)
                sourceDirectories = subProject.files(coverageSourceDirs)
                executionData = subProject.files("${subProject.buildDir}/jacoco/${testTaskName}.exec")
            }

            subProject.check.dependsOn "${taskName}"
        }
    }

    static List<String> getExcludes(final JunitJacocoExtension extension) {
        extension.excludes == null ? [
         '**/R.class',
         '**/R2.class', // ButterKnife Gradle Plugin
         '**/R$*.class',
         '**/R2$*.class', // ButterKnife Gradle Plugin
         '**/*$$*',
         '**/*$ViewInjector*.*', // Older ButterKnife Versions
         '**/*$ViewBinder*.*', // Older ButterKnife Versions
         '**/*_ViewBinding*.*', // Newer ButterKnife Versions
         '**/BuildConfig.*',
         '**/Manifest*.*',
         '**/*$Lambda$*.*', // Jacoco can not handle several "$" in class name.
         '**/*Dagger*.*', // Dagger auto-generated code.
         '**/*MembersInjector*.*', // Dagger auto-generated code.
         '**/*_Provide*Factory*.*', // Dagger auto-generated code.
         '**/*$JsonObjectMapper.*', // LoganSquare auto-generated code.
         '**/*$inlined$*.*', // Kotlin specific, Jacoco can not handle several "$" in class name.
         '**/*$Icepick.*', // Icepick auto-generated code.
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

    private static boolean shouldIgnore(final Project project, final JunitJacocoExtension extension) {
        return extension.ignoreProjects?.contains(project.name)
    }
}
