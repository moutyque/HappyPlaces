package small.app.happyplaces.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import kotlinx.android.synthetic.main.activity_main.*
import small.app.happyplaces.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolBar.setNavigationOnClickListener {
            onBackPressed()
        }
        if (!Places.isInitialized()) {
            Places.initialize(this@MainActivity, resources.getString(R.string.google_maps_key))
        }

    }


}