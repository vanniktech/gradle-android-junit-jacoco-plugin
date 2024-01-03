package com.vanniktech.android.junit.jacoco

import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestExtension
import com.android.build.gradle.internal.coverage.JacocoOptions
import com.android.builder.model.BuildType
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

/** Provides projects for testing */
final class ProjectHelper {
    static ProjectHelper prepare(ProjectType projectType) {
        return prepare(projectType, null)
    }

    static ProjectHelper prepare(ProjectType projectType, Project parent) {
        return new ProjectHelper(projectType, parent)
    }

    private final ProjectType projectType
    private final Project project

    private ProjectHelper(ProjectType projectType, Project parent) {
        this.projectType = projectType

        def builder = ProjectBuilder.builder().withParent(parent)

        switch (projectType) {
            case ProjectType.ROOT:
                project = builder.withName('root').build()
                project.extensions.create('junitJacoco', JunitJacocoExtension)
                GenerationPlugin.addJacocoMergeToRootProject(project, project.junitJacoco)
                break
            case ProjectType.JAVA:
                project = builder.withName('java').build()
                break
            case ProjectType.ANDROID_APPLICATION:
            case ProjectType.ANDROID_KOTLIN_APPLICATION:
            case ProjectType.ANDROID_DYNAMIC_FEATURE:
                def name = "android app ${projectType.name()}"
                project = builder.withName(name).build()
                def androidMock = new MockFor(AppExtension)
                def buildTypesMock = ["debug", "release"].collect { bt ->
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { bt }
                    type.metaClass.testCoverageEnabled = true
                    type
                }
                androidMock.metaClass.getBuildTypes = { buildTypesMock }
                def appVariants = buildTypesMock.collect { bt ->
                    def variant = new MockFor(ApplicationVariant)
                    variant.metaClass.getFlavorName = { null }
                    variant.metaClass.getBuildType = { bt }
                    variant
                }
                androidMock.metaClass.getApplicationVariants = { appVariants }
                androidMock.metaClass.testOptions = null
                androidMock.metaClass.jacoco = mockJacocoOptions()
                project.metaClass.android = androidMock
                // mock .all{ } function from android gradle lib with standard groovy .each{ }
                project.android.applicationVariants.metaClass.all = { delegate.each(it) }
                break
            case ProjectType.ANDROID_LIBRARY:
            case ProjectType.ANDROID_KOTLIN_MULTIPLATFORM:
                def name = "android library ${projectType.name()}"
                project = builder.withName(name).build()
                def androidMock = new MockFor(LibraryExtension)
                def buildTypesMock = ["debug", "release"].collect { bt ->
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { bt }
                    type.metaClass.testCoverageEnabled = true
                    type
                }
                androidMock.metaClass.getBuildTypes = { buildTypesMock }
                def appVariants = buildTypesMock.collect { bt ->
                    def variant = new MockFor(ApplicationVariant)
                    variant.metaClass.getFlavorName = { null }
                    variant.metaClass.getBuildType = { bt }
                    variant
                }
                androidMock.metaClass.getLibraryVariants = { appVariants }
                androidMock.metaClass.testOptions = null
                androidMock.metaClass.jacoco = mockJacocoOptions()
                project.metaClass.android = androidMock
                // mock .all{ } function from android gradle lib with standard groovy .each{ }
                project.android.libraryVariants.metaClass.all = { delegate.each(it) }
                break
            case ProjectType.ANDROID_TEST:
                project = builder.withName('android test').build()
                def androidMock = new MockFor(TestExtension)
                androidMock.metaClass.testOptions = null
                androidMock.metaClass.jacoco = mockJacocoOptions()
                project.metaClass.android = androidMock
                break
        }

        if (projectType.pluginNames != null) {
            for (String pluginName : projectType.pluginNames) {
                if (pluginName) {
                    project.plugins.apply(pluginName)
                }
            }
        }
    }

    private static def mockJacocoOptions(){
        def options = new MockFor(JacocoOptions)
        options.metaClass.version = '7.9.0'
        return options
    }

    /** Adds flavors to project, only for Android based projects */
    ProjectHelper withRedBlueFlavors() {
        if (projectType == ProjectType.JAVA || projectType == ProjectType.ROOT) {
            throw new UnsupportedOperationException('Not supported with Java or plain projects')
        }

        def customFlavors = [
                red : [applicationId: 'com.example.red'],
                blue: [applicationId: 'com.example.blue']
        ]

        def variants = customFlavors.collect { flavorName, config ->
            project.android.buildTypes.collect { buildType ->
                def variant = new MockFor(ApplicationVariant)
                variant.metaClass.getBuildType = {
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { buildType.name }
                    type.metaClass.testCoverageEnabled = true
                    type
                }
                variant.metaClass.getFlavorName = { flavorName }
                variant.metaClass.getApplicationId = { config.applicationId }
                variant
            }
        }.flatten()

        switch (projectType) {
            case ProjectType.ANDROID_APPLICATION:
                project.android.metaClass.applicationVariants = variants
                // mock .all{ } function from android gradle lib with standard groovy .each{ }
                project.android.applicationVariants.metaClass.all = { delegate.each(it) }
                break
            case ProjectType.ANDROID_LIBRARY:
            case ProjectType.ANDROID_DYNAMIC_FEATURE:
                project.android.metaClass.libraryVariants = variants
                // mock .all{ } function from android gradle lib with standard groovy .each{ }
                project.android.libraryVariants.metaClass.all = { delegate.each(it) }
                break
        }

        return this
    }

    Project get() {
        return project
    }

    enum ProjectType {
        ANDROID_APPLICATION('com.android.application'),
        ANDROID_KOTLIN_APPLICATION('com.android.application', 'org.jetbrains.kotlin.android'),
        ANDROID_KOTLIN_MULTIPLATFORM('com.android.library', 'org.jetbrains.kotlin.multiplatform'),
        ANDROID_LIBRARY('com.android.library'),
        ANDROID_DYNAMIC_FEATURE('com.android.dynamic-feature'),
        ANDROID_TEST('com.android.test'),
        JAVA('java'),
        ROOT(null)

        private final String[] pluginNames

        ProjectType(String... pluginNames) {
            this.pluginNames = pluginNames
        }
    }
}
