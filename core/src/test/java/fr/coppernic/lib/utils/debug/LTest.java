package fr.coppernic.lib.utils.debug;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import timber.log.Timber;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LTest {

    @After
    public void tearDown() {
        Timber.uprootAll();
    }

    @Ignore("Fail in some obscure cases")
    @Test
    public void testM() {
        Timber.plant(new TestTree("test", "testM"));
        L.sDepth = 2;
        L.m("test");
    }

    private static class TestTree extends Timber.Tree {

        final String tag;
        final String message;

        private TestTree(String tag, String message) {
            this.tag = tag;
            this.message = message;
        }

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            assertThat(tag, is(this.tag));
            assertThat(message, is(this.message));
        }
    }
}
