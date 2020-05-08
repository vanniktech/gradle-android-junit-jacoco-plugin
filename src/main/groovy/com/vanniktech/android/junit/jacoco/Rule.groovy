package com.vanniktech.android.junit.jacoco

import org.gradle.api.Action
import org.gradle.testing.jacoco.tasks.rules.JacocoLimit

class Rule {
  Boolean enabled = false
  String element = "BUNDLE"
  List<String> includes = new ArrayList<String>()
  List<String> excludes = new ArrayList<String>()
  List<Limit> limits = new ArrayList<Limit>()

  Limit limit(Action<? super Limit> configureAction) {
    Limit limit = Limit()
    limits.add(limit)
    configureAction(limit)
    return limit
  }
}
