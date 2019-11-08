package fr.coppernic.lib.utils.graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import fr.coppernic.lib.utils.core.Preconditions;
import fr.coppernic.lib.utils.debug.InternalLog;
import fr.coppernic.lib.utils.io.Closeables;
import fr.coppernic.lib.utils.io.FileHelper;

/**
 * Utility class to manipulate Bitmaps
 * Created by bastien on 01/12/15.
 */
@SuppressWarnings("WeakerAccess")
public final class BitmapHelper {

    private BitmapHelper() {
    }

    /**
     * Creates a byte[] containing the PNG-compressed bitmap, or null if
     * something goes wrong.
     */
    @SuppressWarnings("unused")
    public static byte[] compressBitmap(Bitmap bitmap) {
        return compressBitmap(bitmap, Bitmap.CompressFormat.PNG, 100);
    }

    /**
     * Create a byte[] containing the compressed bitmap.
     *
     * @param bitmap Bitmap source
     * @param format Compress format
     * @param rate   Compress rate
     * @return byte[] containing compressed bitmap or null if something goes wrong
     */
    public static byte[] compressBitmap(Bitmap bitmap, Bitmap.CompressFormat format, int rate) {
        final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(size);
        try {
            bitmap.compress(format, rate, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            InternalLog.LOGGER.warn("Unable to serialize bitmap: {}", e.toString());
            return null;
        }
    }

    /**
     * Create a byte[] containing the compressed bitmap
     * (see {@link #compressBitmap(Bitmap, Bitmap.CompressFormat, int)}) and scale it.
     * See DisplayMetrics documentation for values.
     *
     * @param bitmap  Bitmap source
     * @param format  Compress format
     * @param rate    Compress rate
     * @param metrics DisplayMetrics value
     * @return byte[] containing result or null if something goes wrong
     */
    public static byte[] compressAndScale(Bitmap bitmap, Bitmap.CompressFormat format, int rate, int metrics) {
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, bitmap.getScaledWidth(metrics),
                                              bitmap.getScaledHeight(metrics), false);
        return compressBitmap(bm, format, rate);
    }

    /**
     * Read a bitmap containing in file pointed by uri and scale it by density.
     *
     * @param uri     Uri with file location
     * @param density DisplayMetrics value
     * @return Bitmap result or null if something goes wrong
     */
    public static Bitmap decodeFileAndScale(Uri uri, int density) {
        Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
        bitmap = Bitmap.createScaledBitmap(bitmap,
                                           bitmap.getScaledWidth(density),
                                           bitmap.getScaledHeight(density),
                                           false);
        return bitmap;
    }

    public static Bitmap fromBytesToGreyBitmap(byte[] array, int width, int height) {
        byte[] bits = new byte[array.length * 4];
        for (int i = 0; i < array.length; i++) {
            bits[i * 4] = array[i];
            bits[i * 4 + 1] = array[i];
            bits[i * 4 + 2] = array[i];
            bits[i * 4 + 3] = -1;
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(bits));
        return bitmap;
    }

    /**
     * Get raw bytes contained in Bitmap
     *
     * @param bitmap Bitmap source
     * @return byte[] with bitmap data or null if something goes wrong
     */
    @SuppressWarnings("WeakerAccess")
    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        final int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        ByteBuffer buf = ByteBuffer.allocate(size);
        bitmap.copyPixelsToBuffer(buf);
        InternalLog.LOGGER.debug("{} bytes was copied", size);
        return buf.array();
    }

    /**
     * Save Bitmap in file based on extension.
     * Supported extension are :
     * <ul>
     * <li>jpg</li>
     * <li>png</li>
     * <li>bmp</li>
     * <li>raw</li>
     * </ul>
     *
     * @param uri    : Path of file to write in
     * @param bitmap : Bitmap to write
     * @return true if successful, false otherwise
     */
    public static boolean saveBitmap(@NonNull Uri uri, @NonNull Bitmap bitmap) {
        boolean ret = false;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(uri.getPath());

            String path = Preconditions.ensureNotNull(uri.getPath(), "");
            String ext = FileHelper.getExtension(path);
            if (ext.contains("png")) {
                InternalLog.LOGGER.debug("Save png bitmap");
                ret = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (ext.contains("jpg")) {
                InternalLog.LOGGER.debug("Save jpg bitmap");
                ret = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } else if (ext.contains("raw")) {
                InternalLog.LOGGER.debug("Save raw bitmap");
                out.write(getBytesFromBitmap(bitmap));
            } else if (ext.contains("bmp")) {
                InternalLog.LOGGER.debug("Save bmp bitmap");
                ret = BmpHelper.save(bitmap, out);
            } else {
                InternalLog.LOGGER.error("File ext not recognized : {}", ext);
                ret = false;
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Closeables.closeQuietly(out);
        }
        return ret;
    }

    /**
     * Get a bitmap from a drawable
     *
     * @param drawable Drawable to transform in bitmap
     * @return the newly bitmap created
     */
    public static Bitmap toBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            // Single color bitmap will be created of 1x1 pixel
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                                         drawable.getIntrinsicHeight(),
                                         Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
