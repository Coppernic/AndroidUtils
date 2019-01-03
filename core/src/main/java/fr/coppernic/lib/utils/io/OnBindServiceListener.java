package fr.coppernic.lib.utils.io;


import java.util.EventListener;

/**
 * Created by Michael Reynier.
 * Date : 23/09/2016.
 */
public interface OnBindServiceListener extends EventListener {
    void onBindService(boolean value);
}
