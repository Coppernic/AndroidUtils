package fr.coppernic.lib.utils.io;

/**
 * <p>Created on 16/06/16
 *
 * @author Bastien PAUL
 */
public interface Disposable {
    /**
     * Dispose the object. No future interactions with this object shall be done.
     */
    void dispose();

    boolean isDisposed();
}
