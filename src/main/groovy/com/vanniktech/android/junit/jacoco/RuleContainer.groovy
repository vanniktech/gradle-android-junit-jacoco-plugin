package com.vanniktech.android.junit.jacoco

import org.gradle.api.Action

class RuleContainer {
  Boolean failOnViolation = true
  List<Rule> rules = new ArrayList<Rule>()

  Rule rule(Action<? super Rule> configureAction) {
    Rule rule = Rule()
    rules.add(rule)
    configureAction(rule)
    return rule
  }
}
