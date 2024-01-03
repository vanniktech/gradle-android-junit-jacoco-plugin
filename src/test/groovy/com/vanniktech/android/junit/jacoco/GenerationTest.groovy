package com.vanniktech.android.junit.jacoco

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.jupiter.api.Test

import java.nio.file.Paths

import static com.vanniktech.android.junit.jacoco.ProjectHelper.ProjectType.*

class GenerationTest {
    def LANGUAGES = ["clojure", "groovy", "java", "kotlin", "scala"]

    @Test void addJacocoAndroidAppWithFlavors() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).withRedBlueFlavors().get()

        GenerationPlugin.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithFlavors(androidAppProject)
    }

    @Test void addJacocoAndroidLibraryWithFlavors() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).withRedBlueFlavors().get()

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithFlavors(androidLibraryProject)
    }

    @Test void addJacocoAndroidApp() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        GenerationPlugin.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidAppProject, true)
    }

    @Test void addJacocoAndroidLibrary() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject, true)
    }

    @Test void addJacocoAndroidDynamicFeature() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_DYNAMIC_FEATURE).get()

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject, true)
    }

    @Test void addJacocoAndroidAppWithoutInstrumentationCoverage() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()
        androidAppProject.android.buildTypes.each { it.testCoverageEnabled = false }

        GenerationPlugin.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidAppProject, false)
    }

    @Test void addJacocoAndroidLibraryWithoutInstrumentationCoverage() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()
        androidLibraryProject.android.buildTypes.each { it.testCoverageEnabled = false }

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject, false)
    }

    @Test void addJacocoAndroidDynamicFeatureWithoutInstrumentationCoverage() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_DYNAMIC_FEATURE).get()
        androidLibraryProject.android.buildTypes.each { it.testCoverageEnabled = false }

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject, false)
    }

    @Test void addJacocoAndroidTest() {
        def androidTestProject = ProjectHelper.prepare(ANDROID_TEST).get()
        assert !GenerationPlugin.addJacoco(androidTestProject, new JunitJacocoExtension())
    }

    @Test void addJacocoJava() {
        def javaProject = ProjectHelper.prepare(JAVA).get()

        GenerationPlugin.addJacoco(javaProject, new JunitJacocoExtension())

        assertJacocoJava(javaProject)
    }

    @Test void jacocoVersion() {
        final def extension = new JunitJacocoExtension()
        extension.jacocoVersion = '0.7.6.201602180812'
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()
        def javaProject = ProjectHelper.prepare(JAVA).get()

        GenerationPlugin.addJacoco(androidAppProject, extension)
        GenerationPlugin.addJacoco(androidLibraryProject, extension)
        GenerationPlugin.addJacoco(javaProject, extension)

        assert androidAppProject.jacoco.toolVersion == extension.jacocoVersion
        assert androidAppProject.android.jacoco.version == extension.jacocoVersion
        assert androidLibraryProject.jacoco.toolVersion == extension.jacocoVersion
        assert androidLibraryProject.android.jacoco.version == extension.jacocoVersion
        assert javaProject.jacoco.toolVersion == extension.jacocoVersion
    }

    @Test void ignoreProjects() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get(),
                ProjectHelper.prepare(JAVA).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = [project.name]

            assert !GenerationPlugin.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test void ignoreProjectsPath() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get(),
                ProjectHelper.prepare(JAVA).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = [project.path]

            assert !GenerationPlugin.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test void ignoreProjectsRegexPath() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get(),
                ProjectHelper.prepare(JAVA).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = [".*"]

            assert !GenerationPlugin.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test void ignoreProjectsRegexName() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = ["android*"]

            assert !GenerationPlugin.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test void ignoreProjectsWrongRegexName() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = ["androidFFF*"]

            assert GenerationPlugin.addJacoco(project, extension)
            assert project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test void androidAppBuildExecutesJacocoTask() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        GenerationPlugin.addJacoco(androidAppProject, new JunitJacocoExtension())

        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportRelease')
    }

    @Test void androidLibraryBuildExecutesJacocoTask() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        GenerationPlugin.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportRelease')
    }

    @Test void javaBuildExecutesJacocoTask() {
        def javaProject = ProjectHelper.prepare(JAVA).get()

        GenerationPlugin.addJacoco(javaProject, new JunitJacocoExtension())

        assert taskDependsOn(javaProject.check, 'jacocoTestReport')
    }

    @Test void mergedJacocoReportDoesNotHaveDependencies() {
        def rootProject = ProjectHelper.prepare(ROOT).get()

        def jacocoTestReportMerged = rootProject.tasks.findByName("jacocoTestReportMerged")

        assert jacocoTestReportMerged != null

        values().findAll { it != ROOT && it != ANDROID_TEST }.each {
            def project = ProjectHelper.prepare(it, rootProject).get()
            GenerationPlugin.addJacoco(project, new JunitJacocoExtension(), jacocoTestReportMerged)
            if (it == JAVA) {
                assertJacocoJava(project)
            } else {
                assertJacocoAndroidWithoutFlavors(project, true)
            }
        }

        assert jacocoTestReportMerged.dependsOn.size() == 0
    }

    private void assertJacocoAndroidWithFlavors(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.toolVersion == '0.8.7'

        assertTask(project, 'red', 'debug')
        assertTask(project, 'red', 'release')
        assertTask(project, 'blue', 'debug')
        assertTask(project, 'blue', 'release')
    }

    private void assertTask(final Project project, final String flavor, final String buildType) {
        final def task = project.tasks.findByName("jacocoTestReport${flavor.capitalize()}${buildType.capitalize()}")

        assert task instanceof JacocoReport

        task.with {
            assert description == "Generate Jacoco coverage reports after running ${flavor}${buildType.capitalize()} tests."
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/test${flavor.capitalize()}${buildType.capitalize()}UnitTest.exec")

            assert additionalSourceDirs.size() == 15
            LANGUAGES.every {
              assert additionalSourceDirs.contains(project.file("src/main/$it"))
              assert additionalSourceDirs.contains(project.file("src/${buildType}/$it"))
              assert additionalSourceDirs.contains(project.file("src/${flavor}/$it"))
            }

            assert sourceDirectories.size() == 15
            LANGUAGES.every {
              assert sourceDirectories.contains(project.file("src/main/$it"))
              assert sourceDirectories.contains(project.file("src/${buildType}/$it"))
              assert sourceDirectories.contains(project.file("src/${flavor}/$it"))
            }

            assert reports.xml.required
            assert reports.xml.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/${flavor}${buildType.capitalize()}/jacoco.xml")
            assert reports.csv.required
            assert reports.csv.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/${flavor}${buildType.capitalize()}/jacoco.csv")
            assert reports.html.required
            assert reports.html.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/${flavor}${buildType.capitalize()}")

            assert classDirectories.getFrom().first().dir == project.file("build/")

            assert contentEquals(classDirectories.getFrom().first().includes, [
               "**/intermediates/classes/${flavor}/${buildType}/**".toString(),
               "**/intermediates/javac/${flavor}${buildType.capitalize()}/*/classes/**".toString(),
               "**/intermediates/javac/${flavor}${buildType.capitalize()}/classes/**".toString()
              ])

            if (hasKotlin(project)) {
                assert contentEquals(classDirectories.getFrom().first().includes, [
                  "**/intermediates/classes/${flavor}/${buildType}/**",
                  "**/intermediates/javac/${flavor}${buildType.capitalize()}/*/classes/**",
                  "**/intermediates/javac/${flavor}${buildType.capitalize()}/classes/**",
                  "**/tmp/kotlin-classes/${buildType}/**",
                  "**/tmp/kotlin-classes/${flavor}${buildType.capitalize()}/**"])
            } else {
                assert contentEquals(classDirectories.getFrom().first().includes, [
                  "**/intermediates/classes/${flavor}/${buildType}/**".toString(),
                  "**/intermediates/javac/${flavor}${buildType.capitalize()}/*/classes/**".toString(),
                  "**/intermediates/javac/${flavor}${buildType.capitalize()}/classes/**".toString()
                ])
            }

            assert taskDependsOn(task, "test${flavor.capitalize()}${buildType.capitalize()}UnitTest")
            assert taskDependsOn(project.tasks.findByName('check'), "jacocoTestReport${flavor.capitalize()}${buildType.capitalize()}")
        }
    }

    private void assertJacocoAndroidWithoutFlavors(final Project project, final boolean hasCoverage) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.toolVersion == '0.8.7'

        final def debugTask = project.tasks.findByName('jacocoTestReportDebug')

        assert debugTask instanceof JacocoReport

        debugTask.with {
            assert description == 'Generate Jacoco coverage reports after running debug tests.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testDebugUnitTest.exec")

            assert additionalSourceDirs.size() == 10
            LANGUAGES.every {
              assert additionalSourceDirs.contains(project.file("src/main/$it"))
              assert additionalSourceDirs.contains(project.file("src/debug/$it"))
            }

            assert sourceDirectories.size() == 10
            LANGUAGES.every {
             assert sourceDirectories.contains(project.file("src/main/$it"))
             assert sourceDirectories.contains(project.file("src/debug/$it"))
            }

            assert reports.xml.required
            assert reports.xml.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/debug/jacoco.xml")
            assert reports.csv.required
            assert reports.csv.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/debug/jacoco.csv")
            assert reports.html.required
            assert reports.html.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, "/reports/jacoco/debug")

            assert classDirectories.getFrom().first().dir == project.file("build/")
            if (hasKotlin(project)) {
              assert contentEquals(classDirectories.getFrom().first().includes, [
                '**/intermediates/classes/debug/**',
                '**/intermediates/javac/debug/*/classes/**',
                "**/intermediates/javac/debug/classes/**",
                '**/tmp/kotlin-classes/debug/**'
              ])
            } else {
              assert contentEquals(classDirectories.getFrom().first().includes, [
                '**/intermediates/classes/debug/**',
                '**/intermediates/javac/debug/*/classes/**',
                "**/intermediates/javac/debug/classes/**"
              ])
            }

            assert taskDependsOn(debugTask, 'testDebugUnitTest')
            assert taskDependsOn(project.tasks.findByName('check'), 'jacocoTestReportDebug')
        }

        final def debugTaskCombined = project.tasks.findByName('combinedTestReportDebug')
        if (hasCoverage) {
            assert debugTaskCombined instanceof JacocoReport

            debugTaskCombined.with {
                assert description == 'Generate Jacoco coverage reports after running debug tests.'
                assert group == 'Reporting'

                assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testDebugUnitTest.exec")

                assert additionalSourceDirs.size() == 10
                LANGUAGES.every {
                    assert additionalSourceDirs.contains(project.file("src/main/$it"))
                    assert additionalSourceDirs.contains(project.file("src/debug/$it"))
                }

                assert sourceDirectories.size() == 10
                LANGUAGES.every {
                    assert sourceDirectories.contains(project.file("src/main/$it"))
                    assert sourceDirectories.contains(project.file("src/debug/$it"))
                }

                assert reports.xml.required
                assert reports.xml.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/debug/jacoco.xml')
                assert reports.csv.required
                assert reports.csv.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/debug/jacoco.csv')
                assert reports.html.required
                assert reports.html.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/debug')

                assert classDirectories.getFrom().first().dir == project.file("build/")
                if (hasKotlin(project)) {
                  assert contentEquals(classDirectories.getFrom().first().includes, [
                    '**/intermediates/classes/debug/**',
                    '**/intermediates/javac/debug/*/classes/**',
                    "**/intermediates/javac/debug/classes/**",
                    '**/tmp/kotlin-classes/debug/**'
                  ])
                } else {
                  assert contentEquals(classDirectories.getFrom().first().includes, [
                    '**/intermediates/classes/debug/**',
                    '**/intermediates/javac/debug/*/classes/**',
                    "**/intermediates/javac/debug/classes/**",
                  ])
                }

                assert taskDependsOn(debugTaskCombined, 'testDebugUnitTest')
                assert taskDependsOn(debugTaskCombined, 'createDebugCoverageReport')
                assert taskDependsOn(project.tasks.findByName('check'), 'combinedTestReportDebug')
            }
        } else {
            assert debugTaskCombined == null
        }

        final def releaseTask = project.tasks.findByName('jacocoTestReportRelease')

        assert releaseTask instanceof JacocoReport

        releaseTask.with {
            assert description == 'Generate Jacoco coverage reports after running release tests.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testReleaseUnitTest.exec")

            assert additionalSourceDirs.size() == 10
            LANGUAGES.every {
              assert additionalSourceDirs.contains(project.file("src/main/$it"))
              assert additionalSourceDirs.contains(project.file("src/release/$it"))
            }

            assert sourceDirectories.size() == 10
            LANGUAGES.every {
              assert sourceDirectories.contains(project.file("src/main/$it"))
              assert sourceDirectories.contains(project.file("src/release/$it"))
            }

            assert reports.xml.required
            assert reports.xml.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacoco/release/jacoco.xml')
            assert reports.csv.required
            assert reports.csv.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacoco/release/jacoco.csv')
            assert reports.html.required
            assert reports.html.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacoco/release')

            assert classDirectories.getFrom().first().dir == project.file("build/")
            if (hasKotlin(project)) {
              assert contentEquals(classDirectories.getFrom().first().includes, [
                '**/intermediates/classes/release/**',
                '**/intermediates/javac/release/*/classes/**',
                "**/intermediates/javac/release/classes/**",
                '**/tmp/kotlin-classes/release/**'
              ])
            } else {
              assert contentEquals(classDirectories.getFrom().first().includes, [
                '**/intermediates/classes/release/**',
                '**/intermediates/javac/release/*/classes/**',
                "**/intermediates/javac/release/classes/**"
              ])
            }

            assert taskDependsOn(releaseTask, 'testReleaseUnitTest')
            assert taskDependsOn(project.tasks.findByName('check'), 'jacocoTestReportRelease')
        }

        final def releaseTaskCombined = project.tasks.findByName('combinedTestReportRelease')

        if (hasCoverage) {
            assert releaseTaskCombined instanceof JacocoReport

            releaseTaskCombined.with {
                assert description == 'Generate Jacoco coverage reports after running release tests.'
                assert group == 'Reporting'

                assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testReleaseUnitTest.exec")

                assert additionalSourceDirs.size() == 10
                LANGUAGES.every {
                    assert additionalSourceDirs.contains(project.file("src/main/$it"))
                    assert additionalSourceDirs.contains(project.file("src/release/$it"))
                }

                assert sourceDirectories.size() == 10
                LANGUAGES.every {
                    assert sourceDirectories.contains(project.file("src/main/$it"))
                    assert sourceDirectories.contains(project.file("src/release/$it"))
                }

                assert reports.xml.required
                assert reports.xml.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/release/jacoco.xml')
                assert reports.csv.required
                assert reports.csv.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/release/jacoco.csv')
                assert reports.html.required
                assert reports.html.outputLocation.get().asFile.toPath() == Paths.get(project.buildDir.absolutePath, '/reports/jacocoCombined/release')

                assert classDirectories.getFrom().first().dir == project.file("build/")
                if (hasKotlin(project)) {
                  assert contentEquals(classDirectories.getFrom().first().includes, [
                    '**/intermediates/classes/release/**',
                    '**/intermediates/javac/release/*/classes/**',
                    "**/intermediates/javac/release/classes/**",
                    '**/tmp/kotlin-classes/release/**'
                  ])
                } else {
                  assert contentEquals(classDirectories.getFrom().first().includes, [
                    '**/intermediates/classes/release/**',
                    '**/intermediates/javac/release/*/classes/**',
                    "**/intermediates/javac/release/classes/**",
                  ])
                }

              assert taskDependsOn(releaseTaskCombined, 'testReleaseUnitTest')
                assert taskDependsOn(releaseTaskCombined, 'createReleaseCoverageReport')
                assert taskDependsOn(project.tasks.findByName('check'), 'combinedTestReportRelease')
            }
        } else {
            assert releaseTaskCombined == null
        }
    }

    private void assertJacocoJava(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.toolVersion == '0.8.7'

        final def task = project.tasks.findByName('jacocoTestReport')

        assert task instanceof JacocoReport

        task.with {
            assert description == 'Generate Jacoco coverage reports.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/test.exec")

            assert additionalSourceDirs.size() == 5
            LANGUAGES.every {
              assert additionalSourceDirs.contains(project.file("src/main/$it"))
            }

            assert sourceDirectories.size() == 5
            LANGUAGES.every {
              assert sourceDirectories.contains(project.file("src/main/$it"))
            }

            assert classDirectories.size() == 2 // First one is empty and the second fileTree is the one we plant.
            assert classDirectories.getFrom()[1].dir == project.file("build/")
            assert contentEquals(classDirectories.getFrom()[1].includes, ['**/classes/**/main/**'])

            assert reports.xml.required
            assert reports.csv.required
            assert reports.html.required

            assert taskDependsOn(task, 'test')
        }
    }

    static boolean contentEquals(Collection<?> c1, Collection<?> c2) {
        return c1.containsAll(c2) && c2.containsAll(c1)
    }

    static boolean taskDependsOn(final Task task, final String taskName) {
        final def it = task.dependsOn.iterator()

        while (it.hasNext()) {
            final def item = it.next()

            if (item.toString() == taskName) {
                return true
            }
        }

        return false
    }

    static boolean hasKotlin(Project project) {
        return project.plugins.hasPlugin('org.jetbrains.kotlin.android') || project.plugins.hasPlugin('org.jetbrains.kotlin.multiplatform')
    }

    @Test void getExcludesDefault() {
        final def excludes = GenerationPlugin.getExcludes(new JunitJacocoExtension())

        assert excludes.size == 20
        assert excludes.contains('**/R.class')
        assert excludes.contains('**/R2.class')
        assert excludes.contains('**/R$*.class')
        assert excludes.contains('**/R2$*.class')
        assert excludes.contains('**/*$$*')
        assert excludes.contains('**/*$ViewInjector*.*')
        assert excludes.contains('**/*$ViewBinder*.*')
        assert excludes.contains('**/*_ViewBinding*.*')
        assert excludes.contains('**/BuildConfig.*')
        assert excludes.contains('**/Manifest*.*')
        assert excludes.contains('**/*$Lambda$*.*')
        assert excludes.contains('**/*Dagger*.*')
        assert excludes.contains('**/*MembersInjector*.*')
        assert excludes.contains('**/*_Provide*Factory*.*')
        assert excludes.contains('**/*_Factory*.*')
        assert excludes.contains('**/*$JsonObjectMapper.*')
        assert excludes.contains('**/*$inlined$*.*')
        assert excludes.contains('**/*$Icepick.*')
        assert excludes.contains('**/*$StateSaver.*')
        assert excludes.contains('**/*AutoValue_*.*')
    }

  @Test void getExcludesCustom() {
    final def extension = new JunitJacocoExtension()
    extension.excludes = new ArrayList<>()
    extension.excludes.add("**/*.java")

    final def excludes = GenerationPlugin.getExcludes(extension)

    assert excludes == extension.excludes
  }

  @Test void getExcludesCustomPlus() {
    final def extension = new JunitJacocoExtension()
    extension.excludes.add("**/*custom*.java")

    final def excludes = GenerationPlugin.getExcludes(extension)

    assert excludes == extension.excludes
    assert 1 < excludes.size() // Includes defaults
  }

  @Test void getExcludesNull() {
    final def extension = new JunitJacocoExtension()
    extension.excludes = null

    final def excludes = GenerationPlugin.getExcludes(extension)

    assert null != excludes
    assert 0 == excludes.size()
  }
}
