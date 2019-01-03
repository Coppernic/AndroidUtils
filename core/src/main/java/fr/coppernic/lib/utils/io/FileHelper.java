package fr.coppernic.lib.utils.io;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.coppernic.lib.utils.result.RESULT;
import timber.log.Timber;

import static fr.coppernic.lib.utils.BuildConfig.DEBUG;

/**
 * Class used for file manipulation
 *
 * @author bastien.paul
 */
public class FileHelper {

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";
    public static final String HIDDEN_PREFIX = ".";
    /**
     * The extension separator character.
     */
    public static final String EXTENSION_SEPARATOR = ".";
    /**
     * The Unix separator character.
     */
    private static final String UNIX_SEPARATOR = "/";

    /**
     * The Windows separator character.
     */
    private static final String WINDOWS_SEPARATOR = "\\";

    private static final int NOT_FOUND = -1;


    /**
     * Returns the index of the last directory separator character.
     * <p>
     * This method will handle a file in either Unix or Windows format.
     * The position of the last forward or backslash is returned.
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to find the last path separator in, null returns -1
     * @return the index of the last separator character, or -1 if there
     * is no such character
     */
    public static int indexOfLastSeparator(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        final int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

    /**
     * Returns the index of the last extension separator character, which is a dot.
     * <p>
     * This method also checks that there is no directory separator after the last dot. To do this it uses
     * {@link #indexOfLastSeparator(String)} which will handle a file in either Unix or Windows format.
     * </p>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     * </p>
     *
     * @param filename the filename to find the last extension separator in, null returns -1
     * @return the index of the last extension separator character, or -1 if there is no such character
     */
    public static int indexOfExtension(final String filename) {
        if (filename == null) {
            return NOT_FOUND;
        }
        final int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        final int lastSeparator = indexOfLastSeparator(filename);
        return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
    }

    /**
     * Gets the extension of a filename.
     * <p>
     * This method returns the textual part of the filename after the last dot.
     * There must be no directory separator after the dot.
     * <pre>
     * foo.txt      --&gt; "txt"
     * a/b/c.jpg    --&gt; "jpg"
     * a/b.txt/c    --&gt; ""
     * a/b/c        --&gt; ""
     * </pre>
     * <p>
     * The output will be the same irrespective of the machine that the code is running on.
     *
     * @param filename the filename to retrieve the extension of.
     * @return the extension of the file or an empty string if none exists or {@code null}
     * if the filename is {@code null}.
     */
    public static String getExtension(final String filename) {
        if (filename == null) {
            return null;
        }
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    /**
     * Combine strings ensuring that there is a "/" between them
     * "foo" + "bar" = "foo/bar"
     * "foo/" + "/bar" = "foo/bar"
     *
     * @param paths List of paths
     * @return Combined path
     */
    public static String combinePath(String... paths) {
        StringBuilder sb = new StringBuilder();
        boolean endsBySlash = false;
        for (String temp : paths) {
            String path = temp.trim();
            if (sb.length() == 0) {
                sb.append(path);
            } else if (path.length() == 0) {
                continue;
            } else if (endsBySlash) {
                if (path.startsWith(UNIX_SEPARATOR)) {
                    if (path.length() > 1) {
                        sb.append(path.substring(1));
                    }
                } else {
                    sb.append(path);
                }
            } else if (path.startsWith(UNIX_SEPARATOR)) {
                sb.append(path);
            } else {
                sb.append(UNIX_SEPARATOR).append(path);
            }
            endsBySlash = path.endsWith(UNIX_SEPARATOR);
        }
        return sb.toString();
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
        }

        return "application/octet-stream";
    }

    public static File getShortestWritablePath(File f) {
        if (f.canWrite()) {
            String path = f.getPath();
            path = path.substring(0, path.lastIndexOf('/'));
            File ret = getShortestWritablePath(new File(path));
            if (ret == null) {
                return f;
            } else {
                return ret;
            }
        } else {
            return null;
        }
    }

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read
     */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
               Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Get a writable cache dir.
     * <p>If no writable cache dir is found, then return null</p>
     *
     * @param context Context
     * @return A writable dir or null if not found or writable
     */
    public static File getWritableCacheDir(Context context) {
        File fl = context.getExternalCacheDir();
        if (fl != null && fl.canWrite()) {
            return fl;
        }

        fl = context.getCacheDir();
        if (fl != null && fl.canWrite()) {
            return fl;
        }

        Timber.e("No writable cache dir found");
        return null;
    }

    /**
     * Get a writable Files dir
     *
     * @param context Context
     * @return A writable files dir or null if not found or writable
     */
    public static File getWritableFilesDir(Context context) {
        File dir = context.getFilesDir();
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        if (dir.canWrite()) {
            return dir;
        }
        return null;
    }

    /**
     * Calculate the sha1 signature of a file
     *
     * @param path File's path
     * @return Sha1 string
     */
    public static String getSha1FromFile(String path) {
        return getSha1FromFile(new File(path));
    }

    /**
     * Calculate the sha1 signature of a file
     *
     * @param f File
     * @return Sha1 string
     */
    public static String getSha1FromFile(File f) {
        String ret = "";
        try {
            FileInputStream fis = new FileInputStream(f);
            ret = getSha1FromStream(fis);
            Closeables.closeQuietly(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Calculate the sha1 signature of data from stream
     *
     * @param is InputStream
     * @return Sha1 string
     */
    public static String getSha1FromStream(InputStream is) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] buffer = new byte[8192];
            int nRead;

            while ((nRead = is.read(buffer)) > 0) {
                md.update(buffer, 0, nRead);
            }

            byte[] mdBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte mdByte : mdBytes) {
                sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Calculate the sha1 signature of data from resource
     *
     * @param c     Android context
     * @param resId Resource id
     * @return Sha1 string
     */
    public static String getSha1FromRes(Context c, int resId) {
        InputStream is = c.getResources().openRawResource(resId);
        String ret = getSha1FromStream(is);
        Closeables.closeQuietly(is);
        return ret;
    }

    /**
     * Copy file content
     *
     * @param src  File source
     * @param dest File destination
     * @return OK or ERROR
     */
    public static RESULT copyFile(Uri src, Uri dest) {
        return copyFile(new File(src.getPath()), new File(dest.getPath()));
    }

    /**
     * Copy content of a file into another.
     *
     * @param src  Source file
     * @param dest Destination file
     * @return OK or ERROR :
     * <ul>
     * <li>FILE_NOT_FOUND</li>
     * <li>IO</li>
     * </ul>
     */
    public static RESULT copyFile(File src, File dest) {
        RESULT res = RESULT.OK;

        Timber.d(
            "Copy from " + src.getAbsolutePath() + " into "
            + dest.getAbsolutePath());

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);

            // Copy the bits from in stream to out stream
            byte[] buf = new byte[8048];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            res = RESULT.FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            res = RESULT.IO;
        } finally {
            Closeables.closeQuietly(in);
            Closeables.closeQuietly(out);
        }
        return res;
    }

    /**
     * *Return true if file exists and is non empty
     *
     * @param path file's path
     * @return true if file exists and is non empty
     */
    public static boolean fileExistAndNonEmpty(String path) {
        File f = new File(path);
        return f.exists() && f.length() > 0;
    }

    public static boolean fileExistAndNonEmpty(List<Uri> lUri) {
        for (Uri uri : lUri) {
            if (!fileExistAndNonEmpty(uri.getPath())) {
                return false;
            }
        }
        return true;
    }

    /**
     * *Return true if file exists and is non empty
     *
     * @param f file
     * @return true if file exists and is non empty
     */
    public static boolean fileExistAndNonEmpty(File f) {
        return f.exists() && f.length() > 0;
    }

    /**
     * Delete the file designed by uri
     *
     * @param uri Representing a file
     * @return true if file has been deleted, false otherwise
     */
    public static boolean deleteFileFromUri(Uri uri) {
        boolean ok = false;
        if (uri != null) {
            if (DEBUG) {
                Timber.v("Delete %s", uri.toString());
            }
            String path = uri.getPath();
            if (path != null) {
                File f = new File(path);
                ok = f.delete();
            }
        }
        return ok;
    }

    public static boolean deleteFileFromUri(List<Uri> lUri) {
        for (Uri uri : lUri) {
            deleteFileFromUri(uri);
        }

        return true;
    }

    /**
     * Delete f if it exists
     *
     * @param f File to be deleted
     */
    public static void deleteFile(File f) {
        if (f != null && f.exists()) {
            if (!f.delete()) {
                Timber.v("Error in deleting %s", f.getName());
            }
        }
    }

    /**
     * Store data bytes in file designed by Uri
     *
     * @param uri  file's uri
     * @param data data to write
     * @return OK or ERROR :
     * <ul>
     * <li>FILE_NOT_FOUND</li>
     * <li>IO</li>
     * </ul>
     */
    public static RESULT saveFile(Uri uri, byte[] data) {
        RESULT res = RESULT.OK;
        File f = new File(uri.getPath());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            out.write(data);
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            res = RESULT.FILE_NOT_FOUND;
        } catch (IOException e) {
            e.printStackTrace();
            res = RESULT.IO;
        } finally {
            Closeables.closeQuietly(out);
        }
        return res;
    }

    /**
     * Return bytes contained in file
     *
     * @param uri Uri containing path of file
     * @return byte[] data or null if something goes wrong
     */
    public static byte[] getBytesFromFile(Uri uri) {
        File f = new File(uri.getPath());
        return getBytesFromFile(f);
    }

    /**
     * Return bytes contained in file that is in resource
     *
     * @param c     Context
     * @param resId Resource id
     * @return byte[] data or null if something goes wrong
     */
    public static byte[] getBytesFromFile(Context c, int resId) {
        InputStream is = c.getResources().openRawResource(resId);
        byte[] data = BytesHelper.getBytesFromInputStream(is);
        Closeables.closeQuietly(is);
        return data;
    }

    /**
     * Return bytes contained in file
     *
     * @param path path of File
     * @return byte[] data or null if something goes wrong
     */
    public static byte[] getBytesFromFile(String path) {
        return getBytesFromFile(new File(path));
    }

    /**
     * Return bytes contained in file
     *
     * @param f File
     * @return byte[] data or null if something goes wrong
     */
    public static byte[] getBytesFromFile(File f) {
        byte[] data = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(f);
            data = BytesHelper.getBytesFromInputStream(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closeables.closeQuietly(in);
        }
        return data;
    }

    /**
     * Return String contained in file
     *
     * @param c     Context
     * @param resId Resource id
     * @return String contained in file
     */
    public static String getStringFromFile(Context c, int resId) {
        byte[] data = getBytesFromFile(c, resId);
        if (data != null) {
            return new String(data, Charset.defaultCharset());
        } else {
            return "";
        }
    }

    /**
     * Delete all files contained in directory
     *
     * @param dir        Directory to clear
     * @param recursive  True if subdirectories has to be cleared
     * @param deleteSelf True if directories has to be deleted as well and not only files
     * @return <ul>
     * <li>OK</li>
     * <li>ERROR</li>
     * <li>INVALID_PARAM</li>
     * </ul>
     */
    public static RESULT clearDirectory(File dir, boolean recursive, boolean deleteSelf) {
        RESULT res = RESULT.OK;
        if (dir == null) {
            Timber.e("Dir is null");
            res = RESULT.INVALID_PARAM;
        } else if (!dir.isDirectory()) {
            Timber.e("Dir is not a directory");
            res = RESULT.INVALID_PARAM;
        } else {
            for (File f : dir.listFiles()) {
                if (f.isDirectory() && recursive) {
                    //noinspection ConstantConditions
                    clearDirectory(f, recursive, deleteSelf);
                } else //noinspection StatementWithEmptyBody
                    if (!f.isDirectory()) {
                        if (DEBUG) {
                            Timber.v("Delete %s", f.getPath());
                        }
                        res = f.delete() ? RESULT.OK : RESULT.ERROR;
                    } else {
                        //ignore
                    }
            }
            if (deleteSelf) {
                if (DEBUG) {
                    Timber.v("Delete %s", dir.getPath());
                }
                res = dir.delete() ? RESULT.OK : RESULT.ERROR;
            }
        }
        return res;
    }

    /**
     * Check if path is absolute.
     * <p> Path is absolute if it begin's by '/'
     *
     * @param path Path
     * @return true if absolute, false otherwise
     */
    public static boolean isPathAbsolute(String path) {
        return path.trim().startsWith(UNIX_SEPARATOR);
    }

    /**
     * Returns the content of a file as a string from an Uri
     *
     * @param context Context
     * @param uri     Uri of the file to be read
     * @return Content of the file
     * @throws IOException File does not exist
     */
    public static String readTextFromUri(Context context, Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        if (inputStream != null) {
            // Max is 100Mb
            if (inputStream.available() > 100000000) {
                throw new IOException("Too many data in input stream : "
                                      + inputStream.available()
                                      + " bytes instead of a max of 100M");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
        } else {
            throw new IOException("Stream is null");
        }

        return stringBuilder.toString();
    }

    @Nullable
    public static String createZip(@NonNull List<String> files, File file) {
        try {
            final int BUFFER = 2048;
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(file);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];
            for (int i = 0; i < files.size(); i++) {
                FileInputStream fi = new FileInputStream(files.get(i));
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(files.get(i).substring(files.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
            return file.toString();
        } catch (Exception ignored) {
        }
        return null;
    }

}
