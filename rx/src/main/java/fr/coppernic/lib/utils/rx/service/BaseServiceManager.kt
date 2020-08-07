package fr.coppernic.lib.utils.rx.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import fr.coppernic.lib.utils.rx.error
import fr.coppernic.lib.utils.rx.error.RxUtilsException
import fr.coppernic.lib.utils.rx.log.LogDefines
import fr.coppernic.lib.utils.rx.log.LogDefines.LOG
import fr.coppernic.lib.utils.rx.success
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.io.Closeable
import java.lang.ref.WeakReference
import java.util.*

abstract class BaseServiceManager<T> {
    companion object {
        var counter = 0
    }

    private val connectorMap: HashMap<T, LocalServiceConnection> = HashMap()
    var emitter: SingleEmitter<T>? = null

    @Synchronized
    fun getConnector(c: Context): Single<T>? {
        return Single.create { singleEmitter ->
            emitter = singleEmitter
            connectToService(c)
        }
    }

    @Synchronized
    fun close(connector: T) {
        connectorMap.remove(connector)?.close()
    }

    @Synchronized
    fun isClosed(connector: T): Boolean {
        return connectorMap[connector] == null
    }

    abstract fun getAction(): String?

    abstract fun createConnector(service: IBinder?): T

    abstract fun getServicePackage(context: Context?): String

    private fun connectToService(c: Context) {
        if (LogDefines.verbose) {
            LOG.trace("Connecting to service")
        }
        val bindIntent = Intent()
        bindIntent.action = getAction()
        val pack = getServicePackage(c)
        if (pack.isNotEmpty()) {
            bindIntent.setPackage(pack)
        }
        //Use a new Service connection each time
        if (!c.bindService(bindIntent, LocalServiceConnection(c), Context.BIND_AUTO_CREATE)) {
            emitter.error(RxUtilsException("Service not found"))
        } else {
            if (LogDefines.verbose) {
                LOG.trace("Connect to service OK")
            }
        }
    }

    inner class LocalServiceConnection(context: Context) : ServiceConnection, Closeable {
        private val contextRef: WeakReference<Context> = WeakReference(context)
        private val tag = "LocalServiceConnection" + ++counter
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            if (LogDefines.verbose) {
                LOG.trace("$tag: Create new connector")
            }
            //Create a new GpioConnection with this binder
            val connector = createConnector(service)
            //Register this connection for future disconnection.
            connectorMap[connector] = this
            emitter.success(connector)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            if (LogDefines.verbose) {
                LOG.trace("$tag: Service disconnected")
            }
            //Notify that an error occurred. Will be discarded if single already completed.
            emitter.error(RxUtilsException("Service unexpectedly disconnected"))
            //Remove this service connection from map because there is no more connection.
            val it: MutableIterator<Map.Entry<T, LocalServiceConnection>> = connectorMap.entries.iterator()
            while (it.hasNext()) {
                val entry = it.next()
                if (entry.value === this) {
                    it.remove()
                }
            }
        }

        override fun close() {
            if (LogDefines.verbose) {
                LOG.trace("$tag: Close")
            }
            val c = contextRef.get()
            if (c != null) {
                c.unbindService(this)
                contextRef.clear()
            }
        }
    }
}
