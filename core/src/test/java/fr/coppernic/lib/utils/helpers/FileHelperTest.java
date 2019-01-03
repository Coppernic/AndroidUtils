package fr.coppernic.lib.utils.helpers;

import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fr.coppernic.lib.utils.io.FileHelper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileHelperTest {

    @Test
    public void testGetExtension() {
        assertEquals(null, FileHelper.getExtension(null));
        assertEquals("", FileHelper.getExtension(""));
        assertEquals("txt", FileHelper.getExtension(".txt"));
        assertEquals("txt", FileHelper.getExtension("hello.txt"));
        assertEquals("txt", FileHelper.getExtension("pouet.tata.sdf.txt"));
        assertEquals("txt", FileHelper.getExtension("/root/pouet/tata.sdf.txt"));
        assertEquals("", FileHelper.getExtension("pouet.tata.sdf.txt/"));
        assertEquals("", FileHelper.getExtension("/root/tata.sdf.txt/hello"));
    }

    @Test
    public void testGetSha1FromFile() {
        File f = null;

        try {
            f = new File("/tmp/test");
            f.createNewFile();
        } catch (IOException e) {
            try {
                f = new File("/mnt/sdcard/temp.txt");
                f.createNewFile();
            } catch (IOException e1) {
                assertTrue(false);
            }
        }

        try {
            FileWriter writer = new FileWriter(f);
            writer.write("Deus ubique adest.");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

        assertEquals("3cc28207d378cf553943d54087c97ed0441b81cb",
                     FileHelper.getSha1FromFile(f.getAbsolutePath()));

        f.delete();
    }

    @Test
    public void testCombinePath() {
        assertEquals("foo/bar", FileHelper.combinePath("foo", "bar"));
        assertEquals("foo/bar", FileHelper.combinePath("foo/", "bar"));
        assertEquals("foo/bar", FileHelper.combinePath("foo", "/bar"));
        assertEquals("foo/bar", FileHelper.combinePath("foo/", "/bar"));
        assertEquals("/foo/bar", FileHelper.combinePath("/foo", "bar"));
        assertEquals("foo/bar/", FileHelper.combinePath("foo", "bar/"));

        assertEquals("foo/bar/baz", FileHelper.combinePath("foo", "bar", "baz"));
        assertEquals("foo/bar/baz",
                     FileHelper.combinePath("foo/", "/bar/", "/baz"));
        assertEquals("foo/bar/baz", FileHelper.combinePath("foo", "/bar/", "baz"));
        assertEquals("foo/bar/baz", FileHelper.combinePath("foo/", "bar", "/baz"));
        assertEquals("foo/baz", FileHelper.combinePath("foo/", "/", "/baz"));
        assertEquals("foo/baz", FileHelper.combinePath("foo/", "", "baz"));
        assertEquals("foo/baz", FileHelper.combinePath("foo/", "", "/baz"));
        assertEquals("foo/baz", FileHelper.combinePath("foo/", " ", "/baz"));
        assertEquals("foo/ /baz", FileHelper.combinePath("foo/", "/ /", "/baz"));
    }
}