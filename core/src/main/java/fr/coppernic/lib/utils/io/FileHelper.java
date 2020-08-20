package fr.coppernic.lib.utils.io;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import fr.coppernic.lib.utils.log.LogAdditionsKt;
import fr.coppernic.lib.utils.result.RESULT;
import fr.coppernic.lib.utils.result.Result;

import static fr.coppernic.lib.utils.BuildConfig.DEBUG;
import static fr.coppernic.lib.utils.log.LogDefines.LOG;

/**
 * Class used for file manipulation
 *
 * @author bastien.paul
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class FileHelper {

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";
    public static final String HIDDEN_PREFIX = ".";
    /**
     * The extension separator character.
     */
    private static final String EXTENSION_SEPARATOR = ".";
    /**
     * The Unix separator character.
     */
    private static final String UNIX_SEPARATOR = "/";

    /**
     * The Windows separator character.
     */
    private static final String WINDOWS_SEPARATOR = "\\";

    private static final int NOT_FOUND = -1;

    private static final int BUFFER_SIZE = 8192;

    private FileHelper() {
    }

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
     * @return the extension of the file or an empty string if none exists
     */
    @NonNull
    public static String getExtension(@NonNull final String filename) {
        final int index = indexOfExtension(filename);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    @NonNull
    public static String getFileName(@NonNull final String path) {
        final int index = indexOfLastSeparator(path);
        if (index == NOT_FOUND) {
            return "";
        } else {
            return path.substring(index + 1);
        }
    }

    public static String[] splitFileNameAndExt(final String fileName) {
        String name = fileName;
        String extension = "";
        int i = indexOfExtension(fileName);
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                newFile.delete();
            }
            file.renameTo(newFile);
        }
        return newFile;
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
        return Environment.MEDIA_MOUNTED.equals(state)
               || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
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
            byte[] buffer = new byte[BUFFER_SIZE];
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
    public static Result copyFile(Uri src, Uri dest) {
        String sourceScheme = src.getScheme() == null ? "" : src.getScheme();
        String destScheme = dest.getScheme() == null ? "" : dest.getScheme();
        String sourcePath = src.getPath() == null ? "" : src.getPath();
        String destPath = dest.getPath() == null ? "" : dest.getPath();

        if (sourceScheme.equals("file") && destScheme.equals("file")) {
            return copyFile(new File(sourcePath), new File(destPath));
        } else {
            LOG.warn("Cannot use other schemes than 'file' in copyFile()");
            return RESULT.FILE_NOT_FOUND.toResult();
        }
    }

    /**
     * Copy file content
     *
     * @param src  File source
     * @param dest File destination
     * @return OK or ERROR
     */
    public static Result copyFile(Context context, Uri src, Uri dest) {
        Result result = RESULT.OK.toResult();
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(src);
            os = context.getContentResolver().openOutputStream(dest);
            BytesHelper.copyStream(is, os);
        } catch (FileNotFoundException e) {
            result = RESULT.FILE_NOT_FOUND.toResult().withCause(e);
        } catch (IOException e) {
            result = RESULT.IO.toResult().withCause(e);
        } finally {
            Closeables.closeQuietly(is);
            Closeables.closeQuietly(os);
        }
        return result;
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
    public static Result copyFile(File src, File dest) {
        Result res = RESULT.OK.toResult();

        LOG.debug(
            "Copy from " + src.getAbsolutePath() + " into "
            + dest.getAbsolutePath());

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(src);
            out = new FileOutputStream(dest);

            BytesHelper.copyStream(in, out);
        } catch (FileNotFoundException e) {
            res = RESULT.FILE_NOT_FOUND.toResult().withCause(e);
        } catch (IOException e) {
            res = RESULT.IO.toResult().withCause(e);
        } finally {
            Closeables.closeQuietly(in);
            Closeables.closeQuietly(out);
        }
        return res;
    }

    /**
     * Return true if file exists and is non empty
     *
     * @param path file's path
     * @return true if file exists and is non empty
     */
    public static boolean fileExistAndNonEmpty(String path) {
        return fileExistAndNonEmpty(new File(path));
    }

    /**
     * Return true if all files exist and are non empty
     *
     * @param lUri collection of File's Uri
     * @return true if files exist and are non empty
     */
    @Deprecated // Should be in application instead of lib
    public static boolean fileExistAndNonEmpty(Collection<Uri> lUri) {
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
     * Delete f if it exists
     *
     * @param f File to be deleted
     */
    public static void deleteFile(File f) {
        if (f != null && f.exists()) {
            if (!f.delete()) {
                LOG.trace("Error in deleting {}", f.getName());
            }
        }
    }

    /**
     * Return bytes contained in file
     *
     * @param uri Uri containing path of file
     * @return byte[] data or null if something goes wrong
     */
    @Deprecated // Use public static byte[] getBytesFromFile(Context context, Uri uri) instead
    public static byte[] getBytesFromFile(Uri uri) {
        String scheme = uri.getScheme() == null ? "" : uri.getScheme();
        String path = uri.getPath() == null ? "" : uri.getPath();
        if (!scheme.equals("file")) {
            path = "Please use 'file' scheme instead of '" + scheme + "' in Uri";
        }
        File f = new File(path);
        return getBytesFromFile(f);

    }

    /**
     * Return bytes contained in file
     *
     * @param context Context needed if Uri has content scheme
     * @param uri     Uri containing path of file
     * @return byte[] data or null if something goes wrong
     */
    public static byte[] getBytesFromFile(Context context, Uri uri) {
        byte[] ret = null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            ret = BytesHelper.getBytesFromInputStream(is);
        } catch (FileNotFoundException e) {
            LogAdditionsKt.trace(LOG, e);
        } finally {
            Closeables.closeQuietly(is);
        }
        return ret;

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
            LogAdditionsKt.trace(LOG, e);
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
    @SuppressWarnings("UnusedReturnValue")
    public static Result clearDirectory(File dir, boolean recursive, boolean deleteSelf) {
        Result res = RESULT.OK.toResult();
        if (dir == null) {
            LOG.error("Dir is null");
            res = RESULT.INVALID_PARAM.toResult();
        } else if (!dir.isDirectory()) {
            LOG.error("Dir is not a directory");
            res = RESULT.INVALID_PARAM.toResult();
        } else {
            for (File f : dir.listFiles()) {
                if (f.isDirectory() && recursive) {
                    //noinspection ConstantConditions
                    clearDirectory(f, recursive, deleteSelf);
                } else //noinspection StatementWithEmptyBody
                    if (!f.isDirectory()) {
                        if (DEBUG) {
                            LOG.trace("Delete {}", f.getPath());
                        }
                        res = f.delete() ? res : RESULT.ERROR.toResult();
                    } else {
                        //ignore
                    }
            }
            if (deleteSelf) {
                if (DEBUG) {
                    LOG.trace("Delete {}", dir.getPath());
                }
                res = dir.delete() ? res : RESULT.ERROR.toResult();
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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

    /**
     * Store data bytes in file designed by Uri
     * <p>
     * A file with {@link Uri#getPath()} will be created
     *
     * @param context Context needed if Uri has content scheme
     * @param uri     file's uri
     * @param data    data to write
     * @return OK or ERROR :
     * <ul>
     * <li>FILE_NOT_FOUND</li>
     * <li>IO</li>
     * </ul>
     */
    public static Result saveFile(Context context, Uri uri, byte[] data) {
        Result res = RESULT.OK.toResult();
        InputStream is = new ByteArrayInputStream(data);
        OutputStream os = null;
        try {
            os = context.getContentResolver().openOutputStream(uri);
            if (os != null) {
                BytesHelper.copyStream(is, os);
                os.flush();
            }
        } catch (FileNotFoundException e) {
            res = RESULT.FILE_NOT_FOUND.toResult().withCause(e);
        } catch (IOException e) {
            res = RESULT.IO.toResult().withCause(e);
        } finally {
            Closeables.closeQuietly(os);
            Closeables.closeQuietly(is);
        }
        return res;
    }

    /**
     * Save all data contained in input stream into file
     * <p>
     * Input stream is closed by this method
     *
     * @param f  File to be written
     * @param is Data to write
     * @return OK or ERROR :
     * <ul>
     * <li>FILE_NOT_FOUND</li>
     * <li>IO</li>
     * </ul>
     */
    public static Result saveFile(File f, InputStream is) {
        Result res = RESULT.OK.toResult();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            BytesHelper.copyStream(is, out);
            out.flush();
        } catch (FileNotFoundException e) {
            res = RESULT.FILE_NOT_FOUND.toResult().withCause(e);
        } catch (IOException e) {
            res = RESULT.IO.toResult().withCause(e);
        } finally {
            Closeables.closeQuietly(out);
            Closeables.closeQuietly(is);
        }
        return res;
    }

    /**
     * Convert a Uri to a file
     *
     * @param context Context needed to open Uri with 'content' scheme
     * @param uri     Uri to convert
     * @return A file
     */
    public static File fromUriToFile(Context context, Uri uri) throws IOException {
        String path = "";
        String scheme = uri.getScheme() == null ? "" : uri.getScheme();
        if (scheme.equals("file")) {
            path = uri.getPath() == null ? "" : uri.getPath();
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id.matches("[0-9]+")) {
                    Uri newUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                                                            Long.parseLong(id));
                    path = getDataColumn(context, newUri);
                }
            } else if (isExternalStorageDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                String[] parts = id.split(":");
                if (parts.length > 1 && parts[0].equalsIgnoreCase("primary")) {
                    path = combinePath(Environment.getExternalStorageDirectory().getAbsolutePath(), parts[1]);
                }
            } else if (isMediaDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                String[] split = id.split(":");
                Uri contentUri;
                if (split.length > 1) {
                    switch (split[0]) {
                        case "video":
                            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                            break;
                        case "audio":
                            contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                            break;
                        default:
                            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                            break;
                    }

                    String selection = "_id=?";
                    String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }

        // Second try
        if (path.isEmpty()) {
            path = getDataColumn(context, uri);
        }

        // Third try, copy content of remote file to the local one
        if (path.isEmpty()) {
            String fileName = getFileNameFromUri(context, uri);
            String[] splitName = splitFileNameAndExt(fileName);
            File tempFile = File.createTempFile(splitName[0], splitName[1]);
            tempFile = rename(tempFile, fileName);
            tempFile.deleteOnExit();

            InputStream is = null;
            OutputStream os = null;
            try {
                is = context.getContentResolver().openInputStream(uri);
                os = new FileOutputStream(tempFile);
                BytesHelper.copyStream(is, os);
            } finally {
                Closeables.closeQuietly(is);
                Closeables.closeQuietly(os);
            }

            return tempFile;
        }

        return new File(path);
    }


    @NonNull
    public static String getDataColumn(Context context, Uri uri) {
        return getDataColumn(context, uri, null);
    }

    @NonNull
    public static String getDataColumn(Context context, Uri uri, String selection) {
        return getDataColumn(context, uri, selection, null);
    }

    @NonNull
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        return getColumnContent(context, uri, MediaStore.Files.FileColumns.DATA, selection, selectionArgs);
    }


    @NonNull
    public static String getNameColumn(Context context, Uri uri) {
        return getColumnContent(context, uri, OpenableColumns.DISPLAY_NAME, null, null);
    }


    @NonNull
    public static String getColumnContent(@NonNull Context context,
                                          @NonNull Uri uri,
                                          @NonNull String column,
                                          @Nullable String selection,
                                          @Nullable String[] selectionArgs) {
        Cursor cursor = null;
        try {
            String[] projection = new String[]{column};
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                String data = cursor.getString(cursor.getColumnIndex(column));
                if (!data.equals("null")) {
                    return data;
                }
            }
        } finally {
            Closeables.closeQuietly(cursor);
        }
        return "";
    }

    @NonNull
    public static String getFileNameFromUri(@NonNull Context context, @NonNull Uri uri) {
        String result = "";
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            result = getNameColumn(context, uri);
        }
        if (result.isEmpty()) {
            result = getFileName(uri.getPath() == null ? "" : uri.getPath());
        }
        return result;
    }

    public static boolean isMediaDocument(Uri uri) {
        return uri.getAuthority() != null && uri.getAuthority().equals("com.android.providers.media.documents");
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return uri.getAuthority() != null && uri.getAuthority().equals("com.android.providers.downloads.documents");
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return uri.getAuthority() != null && uri.getAuthority().equals("com.android.externalstorage.documents");
    }

}
