package fr.coppernic.lib.utils.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.coppernic.lib.utils.io.BytesHelper;
import fr.coppernic.lib.utils.log.LogDefines;

@SuppressWarnings("WeakerAccess")
public final class Aes {
    static final byte[] IV =
        new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] RB =
        new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                   (byte) 0x87};

    private Aes() {
    }

    public static byte[] encrypt(byte[] plainText, byte[] encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(plainText);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] encryptionKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        SecretKeySpec key = new SecretKeySpec(encryptionKey, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(cipherText);
    }

    public static byte[] getCmac(byte[] key, byte[] message) {
        int constBsize = 16;
        boolean flag;
        byte[] mLast;
        byte r = (byte) (message.length % constBsize);
        // Step 1.
        byte[][] k = generateSubkey(key);

        // Step 2.
        float val = message.length / (float) constBsize;
        int n = (int) Math.ceil(val);

        // Step 3.
        if (n == 0) {
            n = 1;
            flag = false;
        } else {
            flag = r == 0;
        }

        // Step 4.
        if (flag) {

            byte[] mN = new byte[constBsize];

            System.arraycopy(message, constBsize * (n - 1), mN, 0, constBsize);

            mLast = xor(mN, k[0]);
        } else {
            byte[] mN = new byte[constBsize];
            System.arraycopy(message, (n - 1) * constBsize, mN, 0, r);
            mN[r] = (byte) 0x80;
            mLast = xor(mN, k[1]);
        }

        // Step 5.
        byte[] x = new byte[constBsize];
        System.arraycopy(IV, 0, x, 0, constBsize);
        byte[] y;
        // Step6.
        for (int i = 0; i < n - 1; i++) {

            byte[] mI = new byte[constBsize];
            System.arraycopy(message, 16 * i, mI, 0, constBsize);

            y = xor(x, mI);
            try {
                x = encrypt(y, key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        y = xor(mLast, x);

        try {
            return encrypt(y, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }

    /**
     * Returns MAC truncation used for SAM AV2 host authentication
     *
     * @param key     Key
     * @param message Message
     * @return bytes
     */
    public static byte[] getMact(byte[] key, byte[] message) {
        byte[] cmac = getCmac(key, message);
        byte[] mact = new byte[8];
        for (int i = 0; i < 8; i++) {
            int index = 2 * i + 1;
            mact[i] = cmac[index];
        }
        return mact;
    }

    private static byte[] oneBitToLeft(byte[] array) {

        byte msb = 0x00;
        byte[] ret = new byte[array.length];
        for (int i = array.length - 1; i >= 0; i--) {
            byte temp = (byte) (((array[i] << 1) & 0xFF) + msb);

            if ((array[i] & 0x80) == 0x80) {
                msb = 0x01;
            } else {
                msb = 0x00;
            }

            ret[i] = temp;
        }

        return ret;
    }

    private static byte[] xor(byte[] array1, byte[] array2) {
        byte[] ret = new byte[array1.length];
        for (int i = 0; i < array1.length; i++) {
            ret[i] = (byte) ((array1[i] ^ array2[i]) & 0xFF);
        }

        return ret;
    }

    public static byte[][] generateSubkey(byte[] key) {

        // Step 1.
        byte[] l = new byte[16];
        try {
            l = encrypt(IV, key);
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[][] k = new byte[2][16];
        k[0] = oneBitToLeft(l);
        // Step 2.
        if ((l[0] & 0x80) == 0x80) {
            k[0] = xor(k[0], RB);
        }

        k[1] = oneBitToLeft(k[0]);
        // Step 3.
        if ((k[1][0] & 0x80) == 0x80) {
            k[1] = xor(k[1], RB);
        }

        // Step 4.
        LogDefines.LOG.debug("L = " + BytesHelper.byteArrayToString(l, l.length));
        LogDefines.LOG.debug("K1 = " + BytesHelper.byteArrayToString(k[0], k[0].length));
        LogDefines.LOG.debug("K2 = " + BytesHelper.byteArrayToString(k[1], k[1].length));

        return k;
    }
}
