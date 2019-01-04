package fr.coppernic.app.sample.install

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import fr.bipi.tressence.common.utils.Info
import fr.coppernic.lib.utils.debug.SimpleProfiler
import fr.coppernic.lib.utils.io.FileHelper
import fr.coppernic.lib.utils.pm.Constants
import fr.coppernic.lib.utils.pm.PackageManagerHelper
import fr.coppernic.lib.utils.pm.PackageObserver
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var packageManagerHelper: PackageManagerHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        btnOne.setOnClickListener { install("one.apk") }
        btnTwo.setOnClickListener { install("two.apk") }
        btnThree.setOnClickListener { install("three.apk") }
        btnUninstall1.setOnClickListener { uninstall("fr.coppernic.app.test.install.one") }
        btnUninstall2.setOnClickListener { uninstall("fr.coppernic.app.test.install.two") }
        btnUninstall3.setOnClickListener { uninstall("fr.coppernic.app.test.install.three") }

        packageManagerHelper = PackageManagerHelper(this)
        packageManagerHelper.packageObserver = object : PackageObserver {
            override fun onPackageDeleted(packageName: String, returnCode: Int) {
                val m = "${Info.getMethodName()} : $packageName, ${Constants.codeToString(returnCode)}"
                Timber.v(m)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, m, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onPackageInstalled(packageName: String, returnCode: Int) {
                val m = "${Info.getMethodName()} : $packageName, ${Constants.codeToString(returnCode)}"
                Timber.v(m)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, m, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        packageManagerHelper.dispose()
        super.onDestroy()
    }

    private fun uninstall(p: String) {
        packageManagerHelper.uninstallPackage(p)
    }

    private fun install(name: String) {
        copyApk()
        packageManagerHelper.installPackage(getDir().resolve(name))
    }

    private fun copyApk() {
        SimpleProfiler.begin(BuildConfig.DEBUG, Info.getMethodName())
        val dir = getDir()
        if (dir.exists()) {
            val one = dir.resolve("one.apk")
            val two = dir.resolve("two.apk")
            val three = dir.resolve("three.apk")

            if (!FileHelper.fileExistAndNonEmpty(one)) {
                FileHelper.saveFile(one, assets.open("one.apk"))
            }
            if (!FileHelper.fileExistAndNonEmpty(two)) {
                FileHelper.saveFile(two, assets.open("two.apk"))
            }
            if (!FileHelper.fileExistAndNonEmpty(three)) {
                FileHelper.saveFile(three, assets.open("three.apk"))
            }
        }
        SimpleProfiler.end(BuildConfig.DEBUG, Info.getMethodName())
    }

    private fun getDir(): File {
        val dirs = ContextCompat.getExternalFilesDirs(this, null)
        return if (dirs.isEmpty()) {
            Toast.makeText(this, "No storage available", Toast.LENGTH_SHORT).show()
            File("doNotExist")
        } else {
            dirs[0]
        }
    }

}
