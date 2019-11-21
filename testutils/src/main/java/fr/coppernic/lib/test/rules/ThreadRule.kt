package fr.coppernic.lib.test.rules

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ThreadRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                runBlocking(context = Dispatchers.Default) {
                    base.evaluate()
                }
            }
        }
    }
}
