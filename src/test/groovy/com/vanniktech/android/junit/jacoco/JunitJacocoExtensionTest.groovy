package com.vanniktech.android.junit.jacoco

import org.junit.Test

public class JunitJacocoExtensionTest {
    @Test
    public void jacocoVersion() {
        assert new JunitJacocoExtension().jacocoVersion == '0.7.2.201409121644'
    }
}