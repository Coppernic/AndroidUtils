package fr.coppernic.lib.utils.io;

import java.util.EventListener;

/**
 * Notify that an object is created.
 *
 * @author Bastien PAUL
 */
public interface InstanceListener<T> extends EventListener {
    /**
     * Called when the object is ready to be used
     *
     * @param instance Newly created object
     */
    void onCreated(T instance);

    /**
     * Called when the object shall not be used anymore
     *
     * @param instance The object that cannot be used anymore
     */
    void onDisposed(T instance);
}
