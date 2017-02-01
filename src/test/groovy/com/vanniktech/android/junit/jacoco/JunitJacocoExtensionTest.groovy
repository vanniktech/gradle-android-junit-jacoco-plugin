package com.vanniktech.android.junit.jacoco

import org.junit.Test

public class JunitJacocoExtensionTest {
    @Test
    public void defaults() {
        def extension = new JunitJacocoExtension()

        assert extension.jacocoVersion == null
        assert extension.ignoreProjects.size() == 0
        assert extension.excludes == null
    }
}
