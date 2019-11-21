package fr.coppernic.lib.test.rules

import android.os.Handler
import android.os.HandlerThread
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class HandlerThreadRule : TestRule {

    private lateinit var handlerThread: HandlerThread /*= HandlerThread("HandlerRule")*/
    lateinit var handler: Handler

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                handlerThread = HandlerThread("HandlerRule").apply {
                    start()
                }
                handler = Handler(handlerThread.looper)
                base.evaluate()
                handlerThread.quit()
            }
        }
    }
}

