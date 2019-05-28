package fr.coppernic.sample.utils.core.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import fr.coppernic.sample.utils.core.App
import fr.coppernic.sample.utils.core.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        App.appComponents.inject(this)
    }
}
