package fr.coppernic.lib.test.rules

import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class ConditionalIgnore

class IgnoreRule(val condition: (description: Description) -> Boolean) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        var result = base
        if (hasConditionalIgnoreAnnotation(description)) {
            if (condition(description)) {
                result = IgnoreStatement()
            }
        }
        return result
    }

    private fun hasConditionalIgnoreAnnotation(description: Description): Boolean {
        return description.getAnnotation(ConditionalIgnore::class.java) != null
    }

}

private class IgnoreStatement : Statement() {
    override fun evaluate() {
        Assume.assumeTrue("Ignored by ConditionalIgnore", false)
    }
}
