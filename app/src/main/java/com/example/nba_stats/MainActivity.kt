package com.example.nba_stats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nba_stats.ResultsViewHolder.PlayerAdapter
import com.example.nba_stats.ResultsViewHolder.RecyclerViewInterface
import com.example.nba_stats.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnQueryTextListener, SearchView.OnQueryTextListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter:PlayerAdapter
    private val ListaJugadores = mutableListOf<Player>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar la busqueda
        binding.svPlayers.setOnQueryTextListener(this)

        //INICIALIZAR RECYCLERVIEW
        initRecyclerView()
    }

    //Inicializar RecyclerView
    private fun initRecyclerView()
    {
        adapter = PlayerAdapter(ListaJugadores)
        binding.rvPlayers.layoutManager = LinearLayoutManager(this)
        binding.rvPlayers.adapter = adapter
    }

    //Como vamos a obtener la información
    private fun getRetrofit():Retrofit
    {
        return Retrofit.Builder()
                .baseUrl("https://www.balldontlie.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }


    //Funcion asincrona que busca a los jugadores por su nombre
    private fun searchByName(query:String)
    {
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getPlayersByName(query)
            val jugadores = call.body()

            val statusCode = call.code()

            runOnUiThread {
                //si la llamada ha funcionado
                if (call.isSuccessful)
                {
                    val players : List<Player> = jugadores?.players ?: emptyList()
                    //mostrar recyclerView
                    ListaJugadores.clear()
                    ListaJugadores.addAll(players)

                    System.out.println(ListaJugadores.size)

                    adapter.setOnItemClickListener(object : RecyclerViewInterface{
                        override fun onClick(position: Int)
                        {
                            val intent = Intent(this@MainActivity, PlayerActivity::class.java)

                            intent.putExtra("IDPLAYER",ListaJugadores[position].id.toString())

                            Log.d("ID-fuera",ListaJugadores[position].id.toString())

                            startActivity(intent)
                        }

                    })
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

    private fun showError()
    {
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean
    {
        if(!query.isNullOrEmpty())
        {
            //Método que busca a los jugadores, está arriba
            searchByName(query.lowercase())
        }

        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean
    {
        return true
    }
}