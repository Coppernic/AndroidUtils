package fr.coppernic.lib.utils.helpers;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import fr.coppernic.lib.utils.io.BytesHelper;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class BytesHelperTest {

    @Test
    public void testClearMemoryWithZero() {

        byte[] tab = new byte[100];
        Random ran = new Random();
        ran.nextBytes(tab);
        BytesHelper.clearMemoryWithZero(tab);
        for (int i = 0; i < 100; i++) {
            assertEquals(0, tab[i]);
        }
    }

    @Test
    public void testFindByteInArray() {

        byte[] tab = new byte[100];
        for (int i = 0; i < 100; i++) {
            tab[i] = (byte) i;
        }

        assertEquals(42, BytesHelper.findByteInArray(tab, (byte) 42, 0));
        assertEquals(42, BytesHelper.findByteInArray(tab, (byte) 42, 40));
        assertEquals(-1, BytesHelper.findByteInArray(tab, (byte) 123, 0));
        assertEquals(-1, BytesHelper.findByteInArray(tab, (byte) 42, 123));
        assertEquals(-1, BytesHelper.findByteInArray(tab, (byte) -1, 0));
    }

    @Test
    public void testFindCharInArray() {

        char[] tab = new char[100];
        for (int i = 0; i < 100; i++) {
            tab[i] = (char) i;
        }

        assertEquals(42, BytesHelper.findCharInArray(tab, (char) 42, 0));
        assertEquals(42, BytesHelper.findCharInArray(tab, (char) 42, 40));
        assertEquals(-1, BytesHelper.findCharInArray(tab, (char) 123, 0));
        assertEquals(-1, BytesHelper.findCharInArray(tab, (char) 42, 123));
        assertEquals(-1, BytesHelper.findCharInArray(tab, (char) -1, 0));
    }

    @Test
    public void testHexAsciiToVal() {

        assertEquals(0, BytesHelper.hexAsciiToVal('0'));
        assertEquals(7, BytesHelper.hexAsciiToVal('7'));
        assertEquals(10, BytesHelper.hexAsciiToVal('a'));
        assertEquals(15, BytesHelper.hexAsciiToVal('F'));
        assertEquals(-1, BytesHelper.hexAsciiToVal('&'));
    }

    @Test
    public void testByteArrayToAsciiString() {
        byte[] array = new byte[4];
        for (int i = 0; i < 4; i++) {
            array[i] = (byte) (97 + i);
        }

        //assertEquals("abcd",
        assertEquals("abcd",
                     BytesHelper.byteArrayToAsciiString(array, array.length));
        assertEquals("abc", BytesHelper.byteArrayToAsciiString(array, 3));
    }

    @Test
    public void testByteArrayToString() {
        byte[] array = new byte[4];
        for (int i = 0; i < 4; i++) {
            array[i] = (byte) (97 + i);
        }

        assertEquals("61626364",
                     BytesHelper.byteArrayToString(array));
        assertEquals("61 62 63 64",
                     BytesHelper.byteArrayToString(array, array.length, " "));
        //assertEquals("616263", BytesHelper.byteArrayToString(array, 3));
        assertEquals("61:62:63:64",
                     BytesHelper.byteArrayToString(array, array.length, ":"));
        assertEquals("61azerty62azerty63azerty64",
                     BytesHelper.byteArrayToString(array, array.length, "azerty"));
        assertEquals("61626364",
                     BytesHelper.byteArrayToString(array, array.length));
        assertEquals("61626364",
                     BytesHelper.byteArrayToString(array, array.length, null));
    }

    @Test
    public void testArrayCmp() {
        byte[] a1 = null;
        byte[] a2 = null;

        assertTrue(BytesHelper.arrayCmp(a1, a2));

        a1 = new byte[4];

        assertFalse(BytesHelper.arrayCmp(a1, a2));

        a2 = new byte[4];

        for (int i = 0; i < 4; i++) {
            a1[i] = a2[i] = (byte) i;
        }

        assertTrue(BytesHelper.arrayCmp(a1, a2));

        a1[3] = 42;

        assertFalse(BytesHelper.arrayCmp(a1, a2));

        a2 = new byte[8];

        for (int i = 0; i < 8; i++) {
            a2[i] = (byte) i;
        }
        a1[3] = 3;

        assertFalse(BytesHelper.arrayCmp(a1, a2));

        a2[7] = 42;

        assertFalse(BytesHelper.arrayCmp(a1, a2));
    }

    @Test
    public void testByteArrayToInt() {
        byte[] val = new byte[4];
        val[0] = 0x12;
        val[1] = 0x34;
        val[2] = 0x56;
        val[3] = 0x78;

        assertEquals(0x12345678, BytesHelper.byteArrayToInt(val, true));
        assertEquals(0x78563412, BytesHelper.byteArrayToInt(val, false));

        val = new byte[2];
        val[0] = 0x12;
        val[1] = 0x34;

        assertEquals(0x1234, BytesHelper.byteArrayToInt(val, true));
        assertEquals(0x3412, BytesHelper.byteArrayToInt(val, false));

        val = new byte[8];
        val[0] = 0x12;
        val[1] = 0x34;
        val[2] = 0x56;
        val[3] = 0x78;
        val[4] = (byte) 0x9A;
        val[5] = (byte) 0xBC;
        val[6] = (byte) 0xDE;
        val[7] = (byte) 0xF0;

        assertEquals(0x12345678, BytesHelper.byteArrayToInt(val, true));
        assertEquals(0x78563412, BytesHelper.byteArrayToInt(val, false));
    }

    @Test
    public void testIntToByteArray() {
        byte[] val = BytesHelper.intToByteArray(0x01020304, true);
        for (byte i = 0; i < 4; i++) {
            assertEquals(i + 1, val[i]);
        }
        val = BytesHelper.intToByteArray(0x04030201, false);
        for (byte i = 0; i < 4; i++) {
            assertEquals(i + 1, val[i]);
        }
    }

    @Test
    public void testCopyStream() {
        byte[] buf = new byte[2000];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) i;
        }

        ByteArrayInputStream in = new ByteArrayInputStream(buf);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            BytesHelper.copyStream(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertTrue(BytesHelper.arrayCmp(buf, out.toByteArray()));
    }

    @Test
    public void testParseHexStringToArray() {
        byte[] bytes = BytesHelper.parseHexStringToArray("");
        assertNotNull(bytes);
        assertEquals(0, bytes.length);

        bytes = BytesHelper.parseHexStringToArray("42");
        assertNotNull(bytes);
        assertEquals(1, bytes.length);
        assertEquals(0x42, bytes[0]);

        bytes = BytesHelper.parseHexStringToArray("AB");
        assertNotNull(bytes);
        assertEquals(1, bytes.length);
        assertEquals(-85, bytes[0]);

        bytes = BytesHelper.parseHexStringToArray("010203040506070809");
        assertNotNull(bytes);
        assertEquals(9, bytes.length);
        for (int i = 1; i < 10; i++) {
            assertEquals(i, bytes[i - 1]);
        }

        bytes = BytesHelper.parseHexStringToArray("01020304050607089");
        assertNotNull(bytes);
        assertEquals(8, bytes.length);
        for (int i = 1; i < bytes.length + 1; i++) {
            assertEquals(i, bytes[i - 1]);
        }

        boolean b = true;
        try {
            bytes = BytesHelper.parseHexStringToArray("010203040sdfsdf50607089");
        } catch (NumberFormatException e) {
            b = false;
        }
        assertFalse(b);
    }

    @Test
    public void concatByteArray() {
        ArrayList<byte[]> list = new ArrayList<>();
        byte[] res = new byte[0];
        assertThat(res, is(equalTo(BytesHelper.concatByteArrays(list))));

        list.add(new byte[0]);
        list.add(new byte[]{0x00, 0x01, 0x02});
        list.add(null);
        list.add(new byte[]{0x03, 0x04, 05});
        list.add(new byte[0]);
        list.add(new byte[]{0x06});
        res = new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06};

        assertThat(res, is(equalTo(BytesHelper.concatByteArrays(list))));
    }

    @Test
    public void byteArrayToString() {
        byte[] array = "0123456789".getBytes(Charset.forName("UTF-8"));
        assertThat(BytesHelper.byteArrayToString(array, array.length, ":"),
                   is("30:31:32:33:34:35:36:37:38:39"));
    }
}
