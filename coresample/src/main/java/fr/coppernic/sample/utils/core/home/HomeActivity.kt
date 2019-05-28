package fr.coppernic.sample.utils.core.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.simplemobiletools.commons.extensions.getSDCardPath
import fr.coppernic.sample.utils.core.App
import fr.coppernic.sample.utils.core.R
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.appComponents.inject(this)
    }

    override fun onStart() {
        super.onStart()
        tv.text = getSDCardPath()
    }
}
