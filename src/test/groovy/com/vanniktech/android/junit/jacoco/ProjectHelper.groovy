package com.vanniktech.android.junit.jacoco

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;

/**
 * Provides projects for testing
 */
public class ProjectHelper {

    private final ProjectType projectType;
    private final Project project;

    public ProjectHelper(ProjectType projectType) {
        this.projectType = projectType;

        switch (projectType){
            case ProjectType.JAVA:
                def rootProject = ProjectBuilder.builder().withName('root').build()
                project = ProjectBuilder.builder().withName('java').withParent(rootProject).build()
                project.plugins.apply('java')
                break

            case ProjectType.ANDROID_APPLICATION:
                project = ProjectBuilder.builder().withName('android app').build()
                project.plugins.apply('com.android.application')
                break

            case ProjectType.ANDROID_LIBRARY:
                project = ProjectBuilder.builder().withName('android library').build()
                project.plugins.apply('com.android.library')
                break
        }
    }

    /**
     * Adds flavors to project, only for Android based projects
     *
     * @return ProjectHelper instance
     */
    public ProjectHelper withRedBlueFlavors(){

        def customFlavors = [
                red: [
                        applicationId: "com.example.red"
                ],
                blue: [
                        applicationId: "com.example.blue"
                ]
        ]

        project.android.productFlavors {
            customFlavors.each {name, config ->
                "$name" {
                    applicationId config.applicationId
                }
            }
        }

        return this
    }

    /**
     * Access configured project
     *
     * @return
     */
    public Project get(){
        return project
    }

    /**
     * Type of project
     */
    public enum ProjectType {
        ANDROID_APPLICATION("com.android.application"),
        ANDROID_LIBRARY("com.android.library"),
        JAVA("java");

        private final String pluginName;

        ProjectType(String pluginName) {
            this.pluginName = pluginName;
        }
    }
}
