package com.example.nba_stats

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nba_stats.ResultsViewHolder.PlayerAdapter
import com.example.nba_stats.ResultsViewHolder.RecyclerViewInterface
import com.example.nba_stats.StatsViewHolder.StatsAdapter
import com.example.nba_stats.databinding.ActivityPlayer2Binding
import com.example.nba_stats.databinding.ItemSearchedPlayerBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerActivity : AppCompatActivity() {

    private var jugador:Player? = null
    private lateinit var idPlayer:String
    //Componentes que cambiarán al abrir la activity
    private lateinit var binding:ActivityPlayer2Binding
    private lateinit var adapter:StatsAdapter
    private val playerStats = mutableListOf<PlayerData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player2)

        binding = ActivityPlayer2Binding.inflate(layoutInflater)
        idPlayer = intent.extras?.getString("IDPLAYER").orEmpty()
        Log.d("ID-dentro",idPlayer)
        searchPlayerById()

        setContentView(binding.root)

        //inicializar recyclerView de las estadísticas
        initRecyclerView()
        searchStats()
    }

    //Como vamos a obtener la información
    private fun getRetrofit(): Retrofit
    {
        return Retrofit.Builder()
            .baseUrl("https://www.balldontlie.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchPlayerById()
    {

        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(APIService::class.java).getPlayersById("players/$idPlayer")
            //obtener respuesta
            val jugadores = call.body()

            //ejecutar en el hilo principal
            runOnUiThread{
                //si la llamada ha funcionado
                if(call.isSuccessful)
                {
                    //si es nulo, devuelve lista vacia
                    jugador = jugadores
                    //Obtener el jugador del que se va a mostrar la información

                    changeData()
                }
                else
                {
                    Log.d("ERROR","NO HAY RESPUESTA")
                    //algo ha salido mal, mostrar error
                    showError()
                }
            }
        }
    }

    private fun showError()
    {
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
    }

    private fun changeData()
    {
        val imageLink:String = "https://www.hispanosnba.com/imagenes/jugadores_img/${jugador?.firstName}-"+ (jugador?.lastName?.replace("'","")
            ?: "") +".jpg"

        //Establecer nombre del jugador
        binding.namePlayer.text = jugador?.firstName+" "+jugador?.lastName

        //Pintar imagen del jugador
        Picasso.get().load(imageLink).into(binding.imagePlayer)

        //establecer posicion del jugador
        binding.playerPosition.text = jugador?.position

        //establecer altura del jugador
        binding.playerHeigth.text = jugador?.heightFeet.toString()+"'"+jugador?.heightInches

        //establecer peso del jugador
        binding.playerPounds.text = jugador?.weightPounds.toString()

        //establecer logo del equipo
        binding.iVplayerTeam.setImageDrawable(ContextCompat.getDrawable(this,TeamsResources.teamImages[jugador?.team?.abbreviation] ?: R.drawable.no_image_avilable))
    }

    private fun initRecyclerView()
    {
        adapter = StatsAdapter(playerStats)
        binding.rvPlayerStats.layoutManager = LinearLayoutManager(this)
        binding.rvPlayerStats.adapter = adapter
    }

    private fun searchStats()
    {
        //val arrayIdsPlayer: Array<String> = Array(1) { idPlayer }
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getStatsByPlayerId(idPlayer)
            val stats = call.body()

            val statusCode = call.code()

            runOnUiThread {
                //si la llamada ha funcionado
                if (call.isSuccessful)
                {
                    val playerData : List<PlayerData> = stats?.data ?: emptyList()
                    //mostrar recyclerView
                    playerStats.clear()
                    playerStats.addAll(playerData)

                    System.out.println(playerStats.size)

                    adapter.notifyDataSetChanged()
                }
                else {
                    //algo ha salido mal, mostrar error
                    showError()
                    Log.d("Retrofit1", "URL: ${call.errorBody()}")
                    Log.d("Retrofit2", "Status Code: $statusCode")
                }
            }
        }
    }
}