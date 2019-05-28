package android.net;

/**
 * A class representing the IP configuration of the Ethernet network.
 * <p>
 * Stub class to be able to activate ethernet on C-One²
 */
public class EthernetManager {
    private static final String TAG = "EthernetManager";

    public static final int ETHERNET_STATE_UNKNOWN = 0;
    public static final int ETHERNET_STATE_DISABLED = 1;
    public static final int ETHERNET_STATE_ENABLED = 2;

    /**
     * Indicates whether the system currently has one or more
     * Ethernet interfaces.
     */
    public boolean isAvailable() {
        return true;
    }

    /**
     * Adds a listener.
     *
     * @param listener A {@link Listener} to add.
     * @throws IllegalArgumentException If the listener is null.
     */
    public void addListener(Listener listener) {
    }

    /**
     * Removes a listener.
     *
     * @param listener A {@link Listener} to remove.
     * @throws IllegalArgumentException If the listener is null.
     */
    public void removeListener(Listener listener) {
    }

    public int getEthernetState() {
        return 0;
    }

    public boolean isEthernetEnabled() {
        return true;
    }

    public boolean setEthernetEnabled(int enabled) {
        return true;
    }

    public IpConfiguration getConfiguration() {
        return null;
    }

    public void setConfiguration(IpConfiguration configuration) {}

    /**
     * A listener interface to receive notification on changes in Ethernet.
     */
    public interface Listener {
        /**
         * Called when Ethernet port's availability is changed.
         *
         * @param isAvailable {@code true} if one or more Ethernet port exists.
         */
        public void onAvailabilityChanged(boolean isAvailable);
    }
}
