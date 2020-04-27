package com.pearldroidos.pokemon

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var character: Bitmap
    private var location: Location? = null
    private var listOfPokemon = ArrayList<Pokemon>()
    private val ACCESSLOCATION = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkPermission()
        loadPokemon()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), ACCESSLOCATION)
            }
        }else {
            getUserLocation()
        }
    }


    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        Toast.makeText(this, "User location access on", Toast.LENGTH_SHORT).show()

        val myLocation = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)

        val myThread = MyThread()
        myThread.start()
    }

    private fun loadPokemon(){
        val pokemon_one = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.pokemon_one), 290, 250, true)
        val pokemon_two = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.pokemon_two), 250, 250, true)
        val charmander = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.charmander), 250, 290, true)
        val bulbasaur = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.bulbasaur), 250, 250, true)
        val squirtle = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.squirtle), 250, 250, true)
        listOfPokemon.add(
            Pokemon("Charmander", "Charmander is living in Japan"
            , charmander, 55.0,37.5264, -122.3033)
        )
        listOfPokemon.add(Pokemon("Bulbasaur", "Bulbasaur is living in USA"
            , bulbasaur, 76.0,37.5149, -122.4362))
        listOfPokemon.add(Pokemon("Squirtle", "Squirtle is living in Thailand"
            , squirtle, 42.0,37.4127, -122.3611))

        listOfPokemon.add(Pokemon("Pokemon master", "Pokemon master is living in USA"
            , pokemon_one, 107.0,37.5815, -122.3952))

        listOfPokemon.add(Pokemon("Bulbasaur2", "Bulbasaur2 is living in USA"
            , pokemon_two, 47.0,37.4595, -122.3114))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "You cannot access your location", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        character = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.resources, R.drawable.main_character), 240, 340, true)

    }


    //Get user location
    inner class MyLocationListener : LocationListener {



        constructor() {
            location = Location("Start")
            location!!.longitude = 0.0
            location!!.latitude = 0.0
        }

        override fun onLocationChanged(_location: Location?) {
            location = _location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            TODO("Not yet implemented")
        }

        override fun onProviderEnabled(provider: String?) {
            TODO("Not yet implemented")
        }

        override fun onProviderDisabled(provider: String?) {
            TODO("Not yet implemented")
        }

    }

    var oldLocation:Location? = null
    var playerPower = 0.0

    inner class MyThread : Thread {
        constructor() : super() {
            oldLocation = Location("Start")
            oldLocation!!.longitude = 0.0
            oldLocation!!.latitude = 0.0
        }

        override fun run() {
            while (true) {
                try {
                    if(oldLocation?.distanceTo(location) == 0f){
                        continue
                    }

                    oldLocation = location

                    //Normally thread cannot connect to UI
                    //You need to declare runOnUiThread for connecting it
                    runOnUiThread(){
                        mMap.clear()
                        // Add a marker in Sydney and move the camera
                        val myCharacter = LatLng(location!!.latitude, location!!.longitude)
                        mMap.addMarker(MarkerOptions()
                                .position(myCharacter)
                                .title("Me")
                                .snippet("Here is my location").icon(BitmapDescriptorFactory.fromBitmap(character)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCharacter, 12f))


                        //Show Pokemon
                        for(pokemon in listOfPokemon){
                            if(!pokemon.isCatch){
                                val pokemonLocation = LatLng(pokemon.lat, pokemon.log)
                                mMap.addMarker(MarkerOptions()
                                    .position(pokemonLocation)
                                    .title(pokemon.name)
                                    .snippet("${pokemon.des}  |  Power: ${pokemon.power}").icon(BitmapDescriptorFactory.fromBitmap(pokemon.bitmap)))

                                val poLoc = Location(pokemon.name)
                                poLoc.longitude = pokemon.log
                                poLoc.latitude = pokemon.lat

                                Log.d("Test", "Location $poLoc   distanceTo ${location!!.distanceTo(poLoc)}")

                                if(location!!.distanceTo(poLoc) < 700){
                                    val index = listOfPokemon.indexOf(pokemon)
                                    pokemon.isCatch = true
                                    listOfPokemon[index] = pokemon
                                    playerPower += pokemon.power
                                    Log.d("Test", "Location < 4 $pokemon  |")
                                    Toast.makeText(applicationContext, "You catch new pokemon and your new power is $playerPower", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    sleep(1000)
                } catch (ex: Exception) {

                }
            }
        }
    }
}
