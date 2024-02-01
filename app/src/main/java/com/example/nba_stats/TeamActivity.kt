package com.example.nba_stats

import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.nba_stats.databinding.ActivityPlayer2Binding
import com.example.nba_stats.databinding.ActivityTeamBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Arrays

class TeamActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityTeamBinding
    private lateinit var teamId:String
    private var equipo:Team? = null
    private lateinit var placesClient: PlacesClient
    private lateinit var mMap: GoogleMap
    private var cityCoordinates: LatLng? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamBinding.inflate(layoutInflater)
        setContentView(binding.root)

        teamId = intent.extras?.getString("IDTEAM").orEmpty()

        searchTeamById()

        // Inicializa Places SDK
        Places.initialize(applicationContext, "AIzaSyAsqqAbvw-4Jz-ffocjSNjtrc6SfAa-R50")
        placesClient = Places.createClient(this)


    }

    private fun getRetrofit(): Retrofit
    {
        return Retrofit.Builder()
            .baseUrl("https://www.balldontlie.io/api/v1/teams/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchTeamById()
    {

        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(APIService::class.java).getTeamById(teamId)
            //obtener respuesta
            val equipos = call.body()

            //ejecutar en el hilo principal
            runOnUiThread{
                //si la llamada ha funcionado
                if(call.isSuccessful)
                {
                    equipo = equipos
                    changeData()

                    val loc = equipo?.city ?: "Estados Unidos"

                    // Obtener las coordenadas de la ciudad
                    cityCoordinates = getCityCoordinates(loc)

                    // Verificar si se obtuvieron las coordenadas antes de mostrar el mapa
                    if (cityCoordinates != null) {
                        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
                        mapFragment.getMapAsync(this@TeamActivity)
                    }
                }
            }
        }
    }

    private fun changeData()
    {
        binding.teamName.text = equipo?.fullName
        binding.teamConference.text = equipo?.conference
        binding.teamDivision.text = equipo?.division
        binding.teamImage.setImageDrawable(ContextCompat.getDrawable(this,TeamsResources.teamImages[equipo?.abbreviation] ?: R.drawable.no_image_avilable))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Verificar si se obtuvieron las coordenadas antes de mostrar el mapa
        if (cityCoordinates != null) {
            // Centrar el mapa en las coordenadas de la ciudad
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cityCoordinates))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f)) // Ajusta el nivel de zoom
        }
    }

    private fun getCityCoordinates(cityName: String): LatLng? {
        val geocoder = Geocoder(this)
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocationName(cityName, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    return LatLng(latitude, longitude)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
}