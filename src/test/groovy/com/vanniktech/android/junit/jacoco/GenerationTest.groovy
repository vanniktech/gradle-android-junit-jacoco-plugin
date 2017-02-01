package com.vanniktech.android.junit.jacoco

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.Test

import static com.vanniktech.android.junit.jacoco.ProjectHelper.ProjectType.*

public class GenerationTest {
    @Test
    public void addJacocoAndroidAppWithFlavors() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).withRedBlueFlavors().get()

        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithFlavors(androidAppProject)
    }

    @Test
    public void addJacocoAndroidLibraryWithFlavors() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).withRedBlueFlavors().get()

        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithFlavors(androidLibraryProject)
    }

    @Test
    public void addJacocoAndroidApp() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidAppProject)
    }

    @Test
    public void addJacocoAndroidLibrary() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assertJacocoAndroidWithoutFlavors(androidLibraryProject)
    }

    @Test
    public void addJacocoJava() {
        def javaProject = ProjectHelper.prepare(JAVA).get()

        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        assertJacocoJava(javaProject)
    }

    @Test
    public void jacocoVersionDefault() {
        def defaultJacocoProject = ProjectBuilder.builder().build();
        defaultJacocoProject.plugins.apply('jacoco');
        def defaultJacocoVersion = defaultJacocoProject.jacoco.toolVersion;

        final def extension = new JunitJacocoExtension()

        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()
        def javaProject = ProjectHelper.prepare(JAVA).get()

        Generation.addJacoco(androidAppProject, extension)
        Generation.addJacoco(androidLibraryProject, extension)
        Generation.addJacoco(javaProject, extension)

        assert androidAppProject.jacoco.toolVersion == defaultJacocoVersion
        assert androidLibraryProject.jacoco.toolVersion == defaultJacocoVersion
        assert javaProject.jacoco.toolVersion == defaultJacocoVersion
    }

    @Test
    public void jacocoVersion() {
        final def extension = new JunitJacocoExtension()
        extension.jacocoVersion = '0.7.6.test'
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()
        def javaProject = ProjectHelper.prepare(JAVA).get()

        Generation.addJacoco(androidAppProject, extension)
        Generation.addJacoco(androidLibraryProject, extension)
        Generation.addJacoco(javaProject, extension)

        assert androidAppProject.jacoco.toolVersion == extension.jacocoVersion
        assert androidLibraryProject.jacoco.toolVersion == extension.jacocoVersion
        assert javaProject.jacoco.toolVersion == extension.jacocoVersion
    }

    @Test
    public void ignoreProjects() {
        final def extension = new JunitJacocoExtension()
        final def projects = [
                ProjectHelper.prepare(ANDROID_APPLICATION).get(),
                ProjectHelper.prepare(ANDROID_LIBRARY).get(),
                ProjectHelper.prepare(JAVA).get()] as Project[]

        for (final def project : projects) {
            extension.ignoreProjects = [project.name]

            assert !Generation.addJacoco(project, extension)
            assert !project.plugins.hasPlugin(JacocoPlugin)
        }
    }

    @Test
    public void androidAppBuildExecutesJacocoTask() {
        def androidAppProject = ProjectHelper.prepare(ANDROID_APPLICATION).get()

        Generation.addJacoco(androidAppProject, new JunitJacocoExtension())

        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidAppProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void androidLibraryBuildExecutesJacocoTask() {
        def androidLibraryProject = ProjectHelper.prepare(ANDROID_LIBRARY).get()

        Generation.addJacoco(androidLibraryProject, new JunitJacocoExtension())

        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportDebug')
        assert taskDependsOn(androidLibraryProject.check, 'jacocoTestReportRelease')
    }

    @Test
    public void javaBuildExecutesJacocoTask() {
        def javaProject = ProjectHelper.prepare(JAVA).get()

        Generation.addJacoco(javaProject, new JunitJacocoExtension())

        assert taskDependsOn(javaProject.check, 'jacocoTestReport')
    }

    private void assertJacocoAndroidWithFlavors(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.hasProperty('toolVersion')

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

            assert additionalSourceDirs.size() == 3
            assert additionalSourceDirs.contains(project.file('src/main/java'))
            assert additionalSourceDirs.contains(project.file("src/${buildType}/java"))
            assert additionalSourceDirs.contains(project.file("src/${flavor}/java"))

            assert sourceDirectories.size() == 3
            assert sourceDirectories.contains(project.file('src/main/java'))
            assert sourceDirectories.contains(project.file("src/${buildType}/java"))
            assert sourceDirectories.contains(project.file("src/${flavor}/java"))

            assert reports.xml.enabled
            assert reports.xml.destination.toString() == project.buildDir.absolutePath + "/reports/jacoco/${flavor}${buildType.capitalize()}/jacoco.xml"
            assert reports.html.enabled
            assert reports.html.destination.toString() == project.buildDir.absolutePath + "/reports/jacoco/${flavor}${buildType.capitalize()}"

            assert classDirectories.dir == project.file("build/intermediates/classes/${flavor}/${buildType}")

            assert taskDependsOn(task, "test${flavor.capitalize()}${buildType.capitalize()}UnitTest")
            assert taskDependsOn(project.tasks.findByName('check'), "jacocoTestReport${flavor.capitalize()}${buildType.capitalize()}")
        }
    }

    private void assertJacocoAndroidWithoutFlavors(final Project project) {
        assert project.plugins.hasPlugin(JacocoPlugin)

        assert project.jacoco.hasProperty('toolVersion')

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

        assert project.jacoco.hasProperty('toolVersion')

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
                return true
            }
        }

        return false
    }

    @Test
    public void getExcludesDefault() {
        final def excludes = Generation.getExcludes(new JunitJacocoExtension())

        assert excludes.size == 14
        assert excludes.contains('**/R.class')
        assert excludes.contains('**/R$*.class')
        assert excludes.contains('**/*$$*')
        assert excludes.contains('**/*$ViewInjector*.*')
        assert excludes.contains('**/*$ViewBinder*.*')
        assert excludes.contains('**/BuildConfig.*')
        assert excludes.contains('**/Manifest*.*')
        assert excludes.contains('**/*$Lambda$*.*')
        assert excludes.contains('**/*Dagger*.*')
        assert excludes.contains('**/*MembersInjector*.*')
        assert excludes.contains('**/*_Provide*Factory*.*')
        assert excludes.contains('**/*$JsonObjectMapper.*')
        assert excludes.contains('**/*$inlined$*.*')
        assert excludes.contains('**/*$Icepick.*')
    }

    @Test
    public void getExcludesCustom() {
        final def extension = new JunitJacocoExtension()
        extension.excludes = new ArrayList<>()
        extension.excludes.add("**/*.java")

        final def excludes = Generation.getExcludes(extension)

        assert excludes == extension.excludes
    }

    @Test
    public void isIncludeNoLocationClassesRequired() {
        // Test gradle version combinations
        assert !Generation.isIncludeNoLocationClassesRequired("1.30", "0.7.3.0100123")
        assert !Generation.isIncludeNoLocationClassesRequired("2.1", "0.7.3.0100123")
        assert !Generation.isIncludeNoLocationClassesRequired("2.12", "0.7.3.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.3.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.14.1", "0.7.3.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("3.1", "0.7.3.0100123")

        // Test jacoco version combinations
        assert !Generation.isIncludeNoLocationClassesRequired("2.13", "0.6.3.0100123")
        assert !Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.2.0100123")
        assert !Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.0.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.3.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.4.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.3")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.7.8")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "0.8")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "1.0")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "1.7.3.0100123")
        assert Generation.isIncludeNoLocationClassesRequired("2.13", "4.7.3.0100123")
    }
}
