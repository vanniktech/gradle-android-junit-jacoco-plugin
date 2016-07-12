package com.vanniktech.android.junit.jacoco

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.Before
import org.junit.Test

import static com.vanniktech.android.junit.jacoco.ProjectHelper.ProjectType.*

public class GenerationTest {

    @Test
    public void addJacocoAndroidApp() {

        // Given
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        // When
        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        // Then
        assertJacocoAndroidWithoutFlavors(androidAppProject)
    }

    @Test
    public void addJacocoAndroidLibrary() {

        // Given
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        // When
        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        // Then
        assertJacocoAndroidWithoutFlavors(androidLibraryProject)
    }

    @Test
    public void addJacocoJava() {

        // Given
        def javaProject = ProjectHelper.prepare(JAVA).get()

        // When
        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        // Then
        assertJacocoJava(javaProject)
    }

    @Test
    public void jacocoVersion() {

        // Given
        final def extension = new JunitJacocoExtension()
        extension.jacocoVersion = '0.7.6.201602180812'
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()
        def javaProject = ProjectHelper.prepare(JAVA).get()

        // When
        Generation.addJacoco(androidAppProject, extension)
        Generation.addJacoco(androidLibraryProject, extension)
        Generation.addJacoco(javaProject, extension)

        // Then
        assert androidAppProject.jacoco.toolVersion == '0.7.6.201602180812'
        assert androidLibraryProject.jacoco.toolVersion == '0.7.6.201602180812'
        assert javaProject.jacoco.toolVersion == '0.7.6.201602180812'
    }

    @Test
    public void ignoreProjects() {
        // Given
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get(),
                ProjectHelper.prepare(JAVA).get()] as Project[]

        for (final def project : projects) {
            // When
            extension.ignoreProjects = [project.name]

            // Then
            assert !Generation.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test
    public void androidAppBuildExecutesJacocoTask() {

        // Given
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        // When
        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        // Then
        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void androidLibraryBuildExecutesJacocoTask() {

        // Given
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        // When
        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        // Then
        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void javaBuildExecutesJacocoTask() {

        // Given
        def javaProject = ProjectHelper.prepare(JAVA).get()

        // When
        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        // Then
        assert taskDependsOn(javaProject.check, 'jacocoTestReport')
    }

    /**
     * Assert proper project construction for Android project without flavors
     *
     * @param project
     */
    private void assertJacocoAndroidWithoutFlavors(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.toolVersion == '0.7.2.201409121644'

        final def debugTask = project.tasks.findByName('jacocoTestReportDebug')

        assert debugTask instanceof JacocoReport

        debugTask.with {
            assert description == 'Generate Jacoco coverage reports after running debug tests.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testDebugUnitTest.exec")

            assert additionalSourceDirs.size() == 2
            assert additionalSourceDirs.contains(project.file('src/main/java'))
            assert additionalSourceDirs.contains(project.file('src/debug/java'))

            assert sourceDirectories.size() == 2
            assert sourceDirectories.contains(project.file('src/main/java'))
            assert sourceDirectories.contains(project.file('src/debug/java'))

            assert reports.xml.enabled
            assert reports.xml.destination.toString() == project.buildDir.absolutePath + '/reports/jacoco/debug/jacoco.xml'
            assert reports.html.enabled
            assert reports.html.destination.toString() == project.buildDir.absolutePath + '/reports/jacoco/debug'

            assert classDirectories.dir == project.file('build/intermediates/classes/debug')

            assert taskDependsOn(debugTask, 'testDebugUnitTest')
            assert taskDependsOn(project.tasks.findByName('check'), 'jacocoTestReportDebug')
        }

        final def releaseTask = project.tasks.findByName('jacocoTestReportRelease')

        assert releaseTask instanceof JacocoReport

        releaseTask.with {
            assert description == 'Generate Jacoco coverage reports after running release tests.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/testReleaseUnitTest.exec")

            assert additionalSourceDirs.size() == 2
            assert additionalSourceDirs.contains(project.file('src/main/java'))
            assert additionalSourceDirs.contains(project.file('src/release/java'))

            assert sourceDirectories.size() == 2
            assert sourceDirectories.contains(project.file('src/main/java'))
            assert sourceDirectories.contains(project.file('src/release/java'))

            assert reports.xml.enabled
            assert reports.xml.destination.toString() == project.buildDir.absolutePath + '/reports/jacoco/release/jacoco.xml'
            assert reports.html.enabled
            assert reports.html.destination.toString() == project.buildDir.absolutePath + '/reports/jacoco/release'

            assert classDirectories.dir == project.file('build/intermediates/classes/release')

            assert taskDependsOn(releaseTask, 'testReleaseUnitTest')
            assert taskDependsOn(project.tasks.findByName('check'), 'jacocoTestReportRelease')
        }
    }

    /**
     * Assert proper JAva project construction
     *
     * @param project
     */
    private void assertJacocoJava(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.toolVersion == '0.7.2.201409121644'

        final def task = project.tasks.findByName('jacocoTestReport')

        assert task instanceof JacocoReport

        task.with {
            assert description == 'Generate Jacoco coverage reports.'
            assert group == 'Reporting'

            assert executionData.singleFile == project.file("${project.buildDir}/jacoco/test.exec")

            assert additionalSourceDirs.size() == 1
            assert additionalSourceDirs.contains(project.file('src/main/java'))

            assert sourceDirectories.size() == 1
            assert sourceDirectories.contains(project.file('src/main/java'))

            assert classDirectories.dir == project.file('build/classes/main/')

            assert reports.xml.enabled
            assert reports.html.enabled

            assert taskDependsOn(task, 'test')
        }
    }

    static boolean taskDependsOn(final Task task, final String taskName) {
        final def it = task.dependsOn.iterator()

        while (it.hasNext()) {
            final def item = it.next()

            if (item.toString().equals(taskName)) {
                return  true
            }
        }

        return false
    }
}
