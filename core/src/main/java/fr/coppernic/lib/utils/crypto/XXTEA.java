package fr.coppernic.lib.utils.crypto;

import android.util.Base64;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 1/7/14.
 */
/* XXTEA encryption arithmetic library.
 *
 * Copyright: Ma Bingyao <andot@ujn.edu.cn>
 * Version: 3.0.2
 * LastModified: Apr 12, 2010
 * This library is free.  You can redistribute it and/or modify it under GPL.
 */

public final class XXTEA {

    private static final int delta = 0x9E3779B9;

    private XXTEA() {
    }

    private static int MX(int sum, int y, int z, int p, int e, int[] k) {
        return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
    }

    /**
     * Encrypt data with key.
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
            encrypt(toIntArray(data, true), toIntArray(key, false)), false);
    }

    /**
     * Decrypt data with key.
     *
     * @param data
     * @param key
     * @return
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
            decrypt(toIntArray(data, false), toIntArray(key, false)), true);
    }

    /**
     * Encrypt data with key.
     *
     * @param v
     * @param k
     * @return
     */
    private static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n], y = v[0], sum = 0, e;
        int p, q = 6 + 52 / (n + 1);

        while (q-- > 0) {
            sum = sum + delta;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = v[p + 1];
                z = v[p] += MX(sum, y, z, p, e, k);
            }
            y = v[0];
            z = v[n] += MX(sum, y, z, p, e, k);
        }
        return v;
    }

    /**
     * Decrypt data with key.
     *
     * @param v
     * @param k
     * @return
     */
    private static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n], y = v[0], sum, e;
        int p, q = 6 + 52 / (n + 1);

        sum = q * delta;
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= MX(sum, y, z, p, e, k);
            }
            z = v[n];
            y = v[0] -= MX(sum, y, z, p, e, k);
            sum = sum - delta;
        }
        return v;
    }

    /**
     * Convert byte array to int array.
     *
     * @param data
     * @param includeLength
     * @return
     */
    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0)
                 ? (data.length >>> 2)
                 : ((data.length >>> 2) + 1));
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }
        n = data.length;
        for (int i = 0; i < n; i++) {
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }

    /**
     * Convert int array to byte array.
     *
     * @param data
     * @param includeLength
     * @return
     */
    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;

        if (includeLength) {
            int m = data[data.length - 1];

            if (m > n) {
                return null;
            } else {
                n = m;
            }
        }
        byte[] result = new byte[n];

        for (int i = 0; i < n; i++) {
            result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
        }
        return result;
    }

    public static List<String> GetPosIdAndKey(String result) {

        Log.v("TESTING", "Inside GetPosIdAndKey");
        List<String> posIdAndKey = new ArrayList<String>();

        StringBuilder keybuild = new StringBuilder();
        String firstDecrypted = null;
        String key = null;
        String PosId = null;
        String PosKey = null;
        String ComKey = null;
        String secondEncrpyted = null;
        String secondDecrypted = null;
        firstDecrypted = new String(XXTEA.decrypt(Base64.decode(result, Base64.DEFAULT), "intragate".getBytes()));
        Log.v("TESTING", "firstDecrypted =" + firstDecrypted);
        StringBuilder outbuild = new StringBuilder(firstDecrypted);
        for (int i = 0; i < 16; i++) {
            keybuild.append(firstDecrypted.charAt(2 * i));
            outbuild.deleteCharAt(i);
        }

        key = keybuild.toString();
        Log.v("TESTING", "key = " + key);
        secondEncrpyted = outbuild.toString();
        Log.v("TESTING", "secondEncrpyted = " + secondEncrpyted);
        secondDecrypted = new String(XXTEA.decrypt(Base64.decode(secondEncrpyted, Base64.DEFAULT), key.getBytes()));
        Log.v("TESTING", "secondDecrypted = " + secondDecrypted);
        PosId = secondDecrypted.substring(secondDecrypted.lastIndexOf('=') + 1);
        ComKey = secondDecrypted.substring(0, secondDecrypted.lastIndexOf('=') + 1);
        posIdAndKey.add(PosId);
        Log.v("TESTING", "posIDAndKey.get(0) PosId  = " + posIdAndKey.get(0));
        PosKey =
            "" + PosId.charAt(0) + key.charAt(0) + PosId.charAt(1) + key.charAt(1) + PosId.charAt(2) + key.charAt(2) +
            PosId.charAt(3) + key.charAt(3);

        if (PosKey == null) {
            Log.v("TESTING", "Decryption failed. PosKey is null");
        } else {
            ComKey = new String(XXTEA.decrypt(Base64.decode(ComKey, Base64.DEFAULT), PosKey.getBytes()));
            if (ComKey == null) {
                Log.v("TESTING", "Decryption failed. Comkey is null");
            } else {
                posIdAndKey.add(ComKey);
            }
        }
        Log.v("TESTING", "posIdAndKey = " + posIdAndKey);
        return posIdAndKey;

    }
}