package com.vanniktech.android.junit.jacoco

import org.junit.jupiter.api.Test

class JunitJacocoExtensionTest {
  @Test void defaults() {
    def extension = new JunitJacocoExtension()

    assert extension.jacocoVersion == '0.8.7'
    assert extension.ignoreProjects.size() == 0
    assert extension.excludes != null
    assert !extension.includeNoLocationClasses
    assert !extension.includeInstrumentationCoverageInMergedReport
    assert extension.xml.enabled
    assert extension.csv.enabled
    assert extension.html.enabled
  }
}
