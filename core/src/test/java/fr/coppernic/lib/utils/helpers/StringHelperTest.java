package fr.coppernic.lib.utils.helpers;

import org.junit.Test;

import fr.coppernic.lib.utils.io.StringHelper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class StringHelperTest {

    @Test
    public void testStringToMac() {
        assertEquals("", StringHelper.stringToMac(""));
        assertEquals("", StringHelper.stringToMac("azertyuiop"));
        assertEquals("", StringHelper.stringToMac("0011223344"));
        assertEquals("", StringHelper.stringToMac("0011223344GG"));
        assertEquals("00:11:22:33:44:55", StringHelper.stringToMac("001122334455"));
        assertEquals("00:11:22:33:44:aa", StringHelper.stringToMac("0011223344aa"));
        assertEquals("00:11:22:33:44:FF", StringHelper.stringToMac("0011223344FF"));
        assertEquals("74:F0:7D:E2:40:D8", StringHelper.stringToMac("74F07DE240D8"));
    }

    @Test
    public void testIsBdAddr() {
        assertTrue(StringHelper.isBdAddr("00:00:00:00:00:00"));
        assertTrue(StringHelper.isBdAddr("01:cd:45:eF:89:AB"));
        assertFalse(StringHelper.isBdAddr(""));
        assertFalse(StringHelper.isBdAddr("qlosijdh"));
        assertFalse(StringHelper.isBdAddr("00:00:00:0Z:00:00"));
        assertFalse(StringHelper.isBdAddr("01:cd:45:eF:89:AB:"));
        assertFalse(StringHelper.isBdAddr(":01:cd:45:eF:89:AB"));
        assertFalse(StringHelper.isBdAddr("01:cd:45:eF:89:AB:00:00:00"));
        assertFalse(StringHelper.isBdAddr("01:cd:45:F:89:AB"));
    }

    @Test
    public void testHexToAscii() {
        assertEquals("abc", StringHelper.hexToAscii("616263"));
        assertEquals("", StringHelper.hexToAscii(""));
        assertEquals("", StringHelper.hexToAscii("qsdkifh qsdkfh qsdklfjh qsd"));
        assertEquals("", StringHelper.hexToAscii("1236547896543213qsdklfjh"));
        assertEquals("", StringHelper.hexToAscii("6162636"));
    }

    @Test
    public void testIsStringInArray() {
        String[] array = new String[]{"tata", "toto", "tutu"};
        assertTrue(StringHelper.isStringInArray(array, "tata"));
        assertTrue(StringHelper.isStringInArray(array, "toto"));
        assertTrue(StringHelper.isStringInArray(array, "tutu"));
        assertFalse(StringHelper.isStringInArray(array, "tete"));
        assertFalse(StringHelper.isStringInArray(array, "ta"));
        assertFalse(StringHelper.isStringInArray(array, "ta"));
        assertFalse(StringHelper.isStringInArray(array, null));
        assertFalse(StringHelper.isStringInArray(null, "tata"));
        assertFalse(StringHelper.isStringInArray(null, null));
    }

    @Test
    public void testRemoveCharInString() {
        assertEquals("", StringHelper.removeCharInString("", ""));
        assertEquals("", StringHelper.removeCharInString("        ", " "));
        assertEquals("qsdqsdqsdqsd",
                     StringHelper.removeCharInString(" qsdqsd   qsdqsd    ", " "));
    }

    @Test
    public void testArrayToString() {
        assertEquals("", StringHelper.arrayToString(new Object[]{}, ""));
        assertEquals("ab", StringHelper.arrayToString(new Object[]{"a", "b"}, ""));
        assertEquals("a,b,c", StringHelper.arrayToString(new Object[]{"a", "b", "c"}, ","));
        assertEquals("", StringHelper.arrayToString(new Object[]{}, "prout"));
        assertEquals("a", StringHelper.arrayToString(new Object[]{"a"}, "pouet"));
    }

    @Test
    public void getNIndexOf() {
        assertThat(StringHelper.getNIndexOf("", ' ', 0), is(-1));
        assertThat(StringHelper.getNIndexOf("/storage/emulated/0/of/path/", '/', 3), is(17));
    }
}