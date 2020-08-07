package android.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import androidx.test.annotation.UiThreadTest;
import androidx.test.core.app.ApplicationProvider;
import fr.coppernic.lib.utils.net.IpConfigurationDelegate;
import fr.coppernic.lib.utils.net.TestUtils;

public class EthernetManagerAndroidTest {

    private static final String TAG = "EthernetTest";
    private Context context;
    private EthernetManager ethernetManager;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private TestUtils testUtils;

    @UiThreadTest
    @Before
    public void before() {
        context = ApplicationProvider.getApplicationContext();
        testUtils = new TestUtils();
        ethernetManager = (EthernetManager) context.getSystemService("ethernet");

    }

    @UiThreadTest
    @Test
    public void test() {
        //printClassInfo(ethernetManager);

        IpConfiguration ipConfig = ethernetManager.getConfiguration();
        printClassInfo(ipConfig);

        IpConfigurationDelegate delegate = new IpConfigurationDelegate(ipConfig);
        //StaticIpConfiguration staticConfig = delegate.getStaticIpConfiguration();

        printClassInfo(delegate.getStaticIpConfiguration());
        printClassInfo(delegate.getHttpProxy());

        Log.d(TAG, String.valueOf(delegate.getIpAssignment()));
        Log.d(TAG, String.valueOf(delegate.getProxySettings()));

        delegate.setIpAssignment(IpConfiguration.IpAssignment.STATIC);
        Log.d(TAG, String.valueOf(delegate.getIpAssignment()));

        delegate.setProxySettings(IpConfiguration.ProxySettings.STATIC);
        Log.d(TAG, String.valueOf(delegate.getProxySettings()));

        ethernetManager.setConfiguration(delegate.getIpConfiguration());
    }

    private void printClassInfo(@Nullable Object obj) {
        if(obj == null) {
            Log.d(TAG, "No info for null object");
        } else {
            Class clazz = obj.getClass();

            Log.v(TAG, clazz.getCanonicalName());

            Method[] methods = clazz.getMethods();
            for (Method m : methods) {
                Log.v(TAG, m.toString());
            }
        }
    }
}
