package fr.coppernic.lib.utils.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.annotation.NonNull;

/**
 * Provide hashing functions using the Modified Bernstein hash
 */
public final class HashHelpers {

    private HashHelpers() {
    }

    /**
     * Hash every element uniformly using the Modified Bernstein hash.
     *
     * <p>Useful to implement a {@link Object#hashCode} for uniformly distributed data.</p>
     *
     * @param array a non-{@code null} array of integers
     * @return the numeric hash code
     */
    public static int hashCode(int... array) {
        if (array == null) {
            return 0;
        }

        /*
         *  Note that we use 31 here instead of 33 since it's preferred in Effective Java
         *  and used elsewhere in the runtime (e.g. Arrays#hashCode)
         *
         *  That being said 33 and 31 are nearly identical in terms of their usefulness
         *  according to http://svn.apache.org/repos/asf/apr/apr/trunk/tables/apr_hash.c
         */
        int h = 1;
        for (int x : array) {
            // Strength reduction; in case the compiler has illusions about divisions being faster
            h = ((h << 5) - h) ^ x; // (h * 31) XOR x
        }

        return h;
    }

    /**
     * Hash every element uniformly using the Modified Bernstein hash.
     *
     * <p>Useful to implement a {@link Object#hashCode} for uniformly distributed data.</p>
     *
     * @param array a non-{@code null} array of floats
     * @return the numeric hash code
     */
    public static int hashCode(float... array) {
        if (array == null) {
            return 0;
        }

        int h = 1;
        for (float f : array) {
            int x = Float.floatToIntBits(f);
            h = ((h << 5) - h) ^ x; // (h * 31) XOR x
        }

        return h;
    }

    /**
     * Hash every element uniformly using the Modified Bernstein hash.
     *
     * <p>Useful to implement a {@link Object#hashCode} for uniformly distributed data.</p>
     *
     * @param array a non-{@code null} array of objects
     * @return the numeric hash code
     */
    @SafeVarargs
    public static <T> int hashCodeGeneric(T... array) {
        if (array == null) {
            return 0;
        }

        int h = 1;
        for (T o : array) {
            int x = (o == null) ? 0 : o.hashCode();
            h = ((h << 5) - h) ^ x; // (h * 31) XOR x
        }

        return h;
    }

    @NonNull
    public static byte[] hashTemplate(final byte[] data, final String algorithm) {
        if (data == null || data.length <= 0) {
            return new byte[]{};
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}
