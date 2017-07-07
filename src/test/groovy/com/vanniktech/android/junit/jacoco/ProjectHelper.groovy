package com.vanniktech.android.junit.jacoco

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.android.builder.model.BuildType
import groovy.mock.interceptor.MockFor
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

/** Provides projects for testing */
final class ProjectHelper {
    public static ProjectHelper prepare(ProjectType projectType) {
        return new ProjectHelper(projectType)
    }

    private final ProjectType projectType;
    private final Project project;

    private ProjectHelper(ProjectType projectType) {
        this.projectType = projectType;

        switch (projectType) {
            case ProjectType.JAVA:
                project = ProjectBuilder.builder().withName('java').build()
                break
            case ProjectType.ANDROID_APPLICATION:
                project = ProjectBuilder.builder().withName('android app').build()
                def androidMock = new MockFor(AppExtension)
                def buildTypesMock = ["debug", "release"].collect { bt ->
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { bt }
                    type
                }
                androidMock.metaClass.getBuildTypes = { buildTypesMock }
                def appVariants = buildTypesMock.collect { bt ->
                    def variant = new MockFor(BaseVariant)
                    variant.metaClass.getFlavorName = { null }
                    variant.metaClass.getBuildType = { bt }
                    variant
                }
                androidMock.metaClass.getApplicationVariants = { appVariants }
                androidMock.metaClass.testOptions = null
                project.metaClass.android = androidMock
                // fake all with each
                project.android.applicationVariants.metaClass.all = { delegate.each(it) }
                break
            case ProjectType.ANDROID_LIBRARY:
                project = ProjectBuilder.builder().withName('android library').build()
                def androidMock = new MockFor(LibraryExtension)
                def buildTypesMock = ["debug", "release"].collect { bt ->
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { bt }
                    type
                }
                androidMock.metaClass.getBuildTypes = { buildTypesMock }
                def appVariants = buildTypesMock.collect { bt ->
                    def variant = new MockFor(BaseVariant)
                    variant.metaClass.getFlavorName = { null }
                    variant.metaClass.getBuildType = { bt }
                    variant
                }
                androidMock.metaClass.getLibraryVariants = { appVariants }
                androidMock.metaClass.testOptions = null
                project.metaClass.android = androidMock
                // fake all with each
                project.android.libraryVariants.metaClass.all = { delegate.each(it) }
                break
        }

        project.plugins.apply(projectType.pluginName)
    }

    /** Adds flavors to project, only for Android based projects */
    public ProjectHelper withRedBlueFlavors() {
        if (projectType == ProjectType.JAVA) {
            throw new UnsupportedOperationException('Not supported with Java project')
        }

        def customFlavors = [
                red : [applicationId: 'com.example.red'],
                blue: [applicationId: 'com.example.blue']
        ]

        def variants = customFlavors.collect { flavorName, config ->
            project.android.buildTypes.collect { buildType ->
                def variant = new MockFor(BaseVariant)
                variant.metaClass.getBuildType = {
                    def type = new MockFor(BuildType)
                    type.metaClass.getName = { buildType.name }
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
                project.android.applicationVariants.metaClass.all = { delegate.each(it) }
                break
            case ProjectType.ANDROID_LIBRARY:
                project.android.metaClass.libraryVariants = variants
                project.android.libraryVariants.metaClass.all = { delegate.each(it) }
                break
        }

        return this
    }

    public Project get() {
        return project
    }

    public enum ProjectType {
        ANDROID_APPLICATION('com.android.application'),
        ANDROID_LIBRARY('com.android.library'),
        JAVA('java');

        private final String pluginName;

        ProjectType(String pluginName) {
            this.pluginName = pluginName;
        }
    }
}
