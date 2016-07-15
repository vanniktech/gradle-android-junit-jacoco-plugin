package com.vanniktech.android.junit.jacoco

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
                break
            case ProjectType.ANDROID_LIBRARY:
                project = ProjectBuilder.builder().withName('android library').build()
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

        project.android.productFlavors {
            customFlavors.each { name, config ->
                "$name" {
                    applicationId config.applicationId
                }
            }
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
