package com.vanniktech.android.junit.jacoco

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.Before
import org.junit.Test

public class GenerationTest {

    private Project rootProject
    private Project javaProject
    private Project androidAppProject
    private Project androidLibraryProject

    private Project[] projects

    @Before
    public void setUp() {
        rootProject = ProjectBuilder.builder().withName('root').build()

        javaProject = ProjectBuilder.builder().withName('java').withParent(rootProject).build()
        javaProject.plugins.apply('java')

        androidAppProject = ProjectBuilder.builder().withName('android app').build()
        androidAppProject.plugins.apply('com.android.application')

        androidLibraryProject = ProjectBuilder.builder().withName('android library').build()
        androidLibraryProject.plugins.apply('com.android.library')

        projects = [javaProject, androidAppProject, androidLibraryProject]
    }

    @Test
    public void addJacocoAndroidApp() {
        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidAppProject)
    }

    @Test
    public void addJacocoAndroidLibrary() {
        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject)
    }

    @Test
    public void addJacocoJava() {
        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        assertJacocoJava(javaProject)
    }

    @Test
    public void jacocoVersion() {
        final def extension = new JunitJacocoExtension()
        extension.jacocoVersion = '0.7.6.201602180812'

        Generation.addJacoco(androidAppProject, extension)
        Generation.addJacoco(androidLibraryProject, extension)
        Generation.addJacoco(javaProject, extension)

        assert androidAppProject.jacoco.toolVersion == '0.7.6.201602180812'
        assert androidLibraryProject.jacoco.toolVersion == '0.7.6.201602180812'
        assert javaProject.jacoco.toolVersion == '0.7.6.201602180812'
    }

    @Test
    public void ignoreProjects() {
        final def extension = new JunitJacocoExtension()

        for (final def project : projects) {
            extension.ignoreProjects = [project.name]

            assert !Generation.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

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

    @Test
    public void androidAppBuildExecutesJacocoTask() {
        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void androidLibraryBuildExecutesJacocoTask() {
        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void javaBuildExecutesJacocoTask() {
        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        assert taskDependsOn(javaProject.check, 'jacocoTestReport')
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
