package com.vanniktech.android.junit.jacoco

import org.junit.Test

public class JunitJacocoExtensionTest {
    @Test
    public void defaults() {
        def extension = new JunitJacocoExtension()

        assert extension.jacocoVersion == '0.7.7.201606060606'
        assert extension.ignoreProjects.size() == 0
        assert extension.excludes == null
        assert !extension.includeNoLocationClasses
    }
}
