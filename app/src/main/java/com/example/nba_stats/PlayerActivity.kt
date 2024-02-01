package com.example.nba_stats

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nba_stats.ResultsViewHolder.PlayerAdapter
import com.example.nba_stats.ResultsViewHolder.RecyclerViewInterface
import com.example.nba_stats.StatsViewHolder.StatsAdapter
import com.example.nba_stats.databinding.ActivityPlayer2Binding
import com.example.nba_stats.databinding.ItemSearchedPlayerBinding
import com.example.nba_stats.databinding.ShimmerPlayerStatsBinding
import com.facebook.shimmer.ShimmerFrameLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlayerActivity : AppCompatActivity() {

    private var jugador:Player? = null
    private lateinit var idPlayer:String
    //Componentes que cambiarán al abrir la activity
    private lateinit var binding:ActivityPlayer2Binding
    private lateinit var adapter:StatsAdapter
    private val playerStats = mutableListOf<PlayerData>()

    //Para usar el shimmer
    private lateinit var shimmerPlayerStats: ShimmerFrameLayout
    private lateinit var shimmerPlayerPresentation: ShimmerFrameLayout

    private lateinit var FavoritoDao: FavoritoDao

    //Saber si el usuario es favorito
    private var isFavorito:Boolean = false
    //Nombre del usuario que ha iniciado sesion
    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player2)

        // Inicializar la base de datos
        val database = AppDatabase.getInstance(this)
        FavoritoDao = database.FavoritoDao()

        binding = ActivityPlayer2Binding.inflate(layoutInflater)
        idPlayer = intent.extras?.getString("IDPLAYER").orEmpty()

        //Recibir el nombre del usuario, si hay nombre la sesión está iniciada
        userName = intent.extras?.getString("NAMEUSER").orEmpty()

        comprobarFavorito()

        shimmerPlayerStats = binding.frameShimmerStats
        shimmerPlayerPresentation = binding.frameShimmerPlayerPresentation

        searchPlayerById()

        setContentView(binding.root)

        //inicializar recyclerView de las estadísticas
        initRecyclerView()
        searchStats()

        initTeamImageOnClick()

        initButtonStar()

        if(isFavorito)
            binding.ivStar.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.full_star))
    }

    private fun comprobarFavorito()
    {
        CoroutineScope(Dispatchers.IO).launch{
            if(FavoritoDao.esJugadorFavorito(idPlayer,userName) > 0)
                isFavorito = true

            ejecutarDespuesDeComprobarFavorito()
        }
    }

    private suspend fun ejecutarDespuesDeComprobarFavorito() {
        withContext(Dispatchers.Main) {
            if (isFavorito) {
                binding.ivStar.setImageDrawable(ContextCompat.getDrawable(this@PlayerActivity, R.drawable.full_star))
            }
        }
    }

    private fun initButtonStar()
    {
        binding.ivStar.setOnClickListener{
            if(!userName.isNullOrEmpty())
            {
                if(!isFavorito)
                {
                    CoroutineScope(Dispatchers.IO).launch{
                        val favorito = Favorito(jugadorId = jugador?.id.toString(), usuarioName = userName)

                        FavoritoDao.insertarFavorito(favorito)

                        isFavorito = true
                    }

                    binding.ivStar.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.full_star))
                }
                else
                {
                    CoroutineScope(Dispatchers.IO).launch{
                        FavoritoDao.borrarFavorito(idPlayer, userName)

                        isFavorito = false
                    }

                    binding.ivStar.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.star_empty))
                }
            }
        }
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
        binding.presentationPlayer.isVisible = false
        shimmerPlayerPresentation.startShimmer()

        CoroutineScope(Dispatchers.IO).launch{
            val call = getRetrofit().create(APIService::class.java).getPlayersById("players/$idPlayer")
            //obtener respuesta
            val jugadores = call.body()

            //ejecutar en el hilo principal
            runOnUiThread{
                //si la llamada ha funcionado
                if(call.isSuccessful)
                {
                    jugador = jugadores
                    changeData()
                }
                else
                {
                    Log.d("ERROR","NO HAY RESPUESTA")
                    //algo ha salido mal, mostrar error
                    showError()
                }

                binding.presentationPlayer.isVisible = true
                shimmerPlayerPresentation.isVisible = false
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
        binding.textPlayerHeigth.text = resources.getString(R.string.player_heigth)+" "+jugador?.heightFeet.toString()+"'"+jugador?.heightInches

        //establecer peso del jugador
        binding.textPlayerPounds.text = resources.getString(R.string.player_pounds)+" "+jugador?.weightPounds.toString()

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
        //shimmerPlayerStats.isVisible = true
        binding.rvPlayerStats.isVisible = false
        shimmerPlayerStats.startShimmer()

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

                binding.rvPlayerStats.isVisible = true
                shimmerPlayerStats.isVisible = false
            }
        }
    }

    private fun initTeamImageOnClick()
    {
        binding.iVplayerTeam.setOnClickListener{
            val intent = Intent(this@PlayerActivity, TeamActivity::class.java)

            intent.putExtra("IDTEAM",jugador?.team?.id.toString())

            startActivity(intent)
        }
    }
}