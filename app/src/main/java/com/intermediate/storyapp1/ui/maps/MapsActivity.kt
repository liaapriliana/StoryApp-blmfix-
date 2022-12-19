package com.intermediate.storyapp1.ui.maps

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.intermediate.storyapp1.R
import com.intermediate.storyapp1.data.model.Story
import com.intermediate.storyapp1.data.presenter.StoriesPresenter
import com.intermediate.storyapp1.data.presenter.StoriesView
import com.intermediate.storyapp1.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity() , OnMapReadyCallback, StoriesView {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private lateinit var sharedPreferences: SharedPreferences
    private var token: String? = ""
    private lateinit var storiesPresenter: StoriesPresenter
    private lateinit var tourismPlace: ArrayList<Story>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences("user_details", MODE_PRIVATE);

        token = sharedPreferences.getString("token", null);
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val location = 1

        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        storiesPresenter = StoriesPresenter(this)
        storiesPresenter.stories(token, location)
    }

    private fun addManyMarker() {
        tourismPlace.forEach { tourism ->
            val latLng = LatLng(tourism.lat.toString().toDouble(), tourism.lon.toString().toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(tourism.name).snippet(tourism.description))
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onSuccessStories(msg: String?, data: ArrayList<Story>?) {
        if (data != null) {
            tourismPlace = data
            addManyMarker()
        }
    }

    override fun onFailedStories(msg: String?) {
        Toast.makeText(this@MapsActivity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}