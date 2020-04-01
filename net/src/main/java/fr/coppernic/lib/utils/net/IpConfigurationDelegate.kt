package fr.coppernic.lib.utils.net

import android.net.IpConfiguration
import android.net.ProxyInfo
import android.net.StaticIpConfiguration
import android.os.Build
import androidx.annotation.RequiresApi
import org.apache.commons.lang3.ClassUtils
import org.apache.commons.lang3.reflect.MethodUtils
import java.lang.reflect.Method

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class IpConfigurationDelegate(val ipConfiguration: IpConfiguration) {

    private val getStaticIpConfigurationMethod: Method
    private val getHttpProxyMethod: Method
    private val getIpAssignmentMethod: Method
    private val getProxySettingsMethod: Method
    private val setStaticIpConfigurationMethod: Method
    private val setHttpProxyMethod: Method
    private val setIpAssignmentMethod: Method
    private val setProxySettingsMethod: Method

    var staticIpConfiguration: StaticIpConfiguration?
        get() = getStaticIpConfigurationMethod.invoke(ipConfiguration) as StaticIpConfiguration?
        set(staticIpConfiguration) {
            setStaticIpConfigurationMethod.invoke(ipConfiguration, staticIpConfiguration)
        }

    var httpProxy: ProxyInfo?
        get() = getHttpProxyMethod.invoke(ipConfiguration) as ProxyInfo?
        set(httpProxy) {
            setHttpProxyMethod.invoke(ipConfiguration, httpProxy)
        }

    var ipAssignment: IpConfiguration.IpAssignment?
        get() = getIpAssignmentMethod.invoke(ipConfiguration) as IpConfiguration.IpAssignment?
        set(ipAssignment) {
            setIpAssignmentMethod.invoke(ipConfiguration, ipAssignment)
        }

    var proxySettings: IpConfiguration.ProxySettings?
        get() = getProxySettingsMethod.invoke(ipConfiguration) as IpConfiguration.ProxySettings?
        set(proxySettings) {
            setProxySettingsMethod.invoke(ipConfiguration, proxySettings)
        }


    init {
        getStaticIpConfigurationMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass, "getStaticIpConfiguration")
        getHttpProxyMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass, "getHttpProxy")
        getIpAssignmentMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass, "getIpAssignment")
        getProxySettingsMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass, "getProxySettings")


        setStaticIpConfigurationMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass,
                "setStaticIpConfiguration", ClassUtils.getClass("android.net.StaticIpConfiguration"))

        setHttpProxyMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass,
                "setHttpProxy", ClassUtils.getClass("android.net.ProxyInfo"))

        setIpAssignmentMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass,
                "setIpAssignment", ClassUtils.getClass("android.net.IpConfiguration\$IpAssignment"))

        setProxySettingsMethod = MethodUtils.getAccessibleMethod(ipConfiguration.javaClass,
                "setProxySettings", ClassUtils.getClass("android.net.IpConfiguration\$ProxySettings"))

    }
}
