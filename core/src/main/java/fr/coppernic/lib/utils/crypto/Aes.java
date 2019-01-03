package fr.coppernic.lib.utils.crypto;

import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import fr.coppernic.lib.utils.io.BytesHelper;

public class Aes {
    public static final String TAG = "AES Coppernic";

    static final byte[] IV =
        new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
    static final byte[] RB =
        new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                   (byte) 0x87};

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
        boolean flag = true;
        byte[] mLast = new byte[constBsize];
        byte r = (byte) (message.length % constBsize);
        // Step 1.
        byte[][] K = generateSubkey(key);

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

            mLast = xor(mN, K[0]);
        } else {
            byte[] mN = new byte[constBsize];
            System.arraycopy(message, (n - 1) * constBsize, mN, 0, r);
            mN[r] = (byte) 0x80;
            mLast = xor(mN, K[1]);
        }

        // Step 5.
        byte[] X = new byte[constBsize];
        System.arraycopy(IV, 0, X, 0, constBsize);
        byte[] Y;
        // Step6.
        for (int i = 0; i < n - 1; i++) {

            byte[] mI = new byte[constBsize];
            System.arraycopy(message, 16 * i, mI, 0, constBsize);

            Y = xor(X, mI);
            try {
                X = encrypt(Y, key);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Y = xor(mLast, X);

        try {
            return encrypt(Y, key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Returns MAC truncation used for SAM AV2 host authentication
     *
     * @param key
     * @param message
     * @return
     */
    public static byte[] getMact(byte[] key, byte[] message) {
        byte[] cmac = getCmac(key, message);
        byte[] mact = mact = new byte[8];
        for (int i = 0; i < 8; i++) {
            mact[i] = cmac[2 * i + 1];
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
        byte[] L = new byte[16];
        try {
            L = encrypt(IV, key);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[][] K = new byte[2][16];
        K[0] = oneBitToLeft(L);
        // Step 2.
        if ((L[0] & 0x80) == 0x80) {
            K[0] = xor(K[0], RB);
        }

        K[1] = oneBitToLeft(K[0]);
        // Step 3.
        if ((K[1][0] & 0x80) == 0x80) {
            K[1] = xor(K[1], RB);
        }

        // Step 4.
        Log.d(TAG, "L = " + BytesHelper.byteArrayToString(L, L.length));
        Log.d(TAG, "K1 = " + BytesHelper.byteArrayToString(K[0], K[0].length));
        Log.d(TAG, "K2 = " + BytesHelper.byteArrayToString(K[1], K[1].length));

        return K;
    }
}
