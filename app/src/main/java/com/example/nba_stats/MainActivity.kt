package com.example.nba_stats

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import android.window.SplashScreen
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nba_stats.ResultsViewHolder.PlayerAdapter
import com.example.nba_stats.ResultsViewHolder.RecyclerViewInterface
import com.example.nba_stats.databinding.ActivityMainBinding
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnQueryTextListener, SearchView.OnQueryTextListener{

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter:PlayerAdapter
    private val ListaJugadores = mutableListOf<Player>()
    private lateinit var screenSplash: androidx.core.splashscreen.SplashScreen
    private var nameUser: String? = null
    private var login: Boolean = false
    private var contadorEjecuciones: Int = 0

    //Shared preferences
    private lateinit var sharedPreferences: SharedPreferences

    //Shimmer mostrar tarjetas jugadores
    private lateinit var shimmerPlayerCards: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        screenSplash = installSplashScreen()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shimmerPlayerCards = binding.frameShimmerPlayerCards

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        incrementarContador()

        updateDarkMode()

        initSwitch()

        getAllPlayers()

        //Inicializar la busqueda
        binding.svPlayers.setOnQueryTextListener(this)

        //INICIALIZAR RECYCLERVIEW
        initRecyclerView()

        initImagen()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null && intent.hasExtra("USERNAME"))
        {
            nameUser = intent.getStringExtra("USERNAME")

            login = true

            showUserName()
        }
    }

    private fun incrementarContador() {
        val editor = sharedPreferences.edit()
        val nuevoContador = obtenerContador() + 1
        editor.putInt("contadorEjecuciones", nuevoContador)
        editor.apply()

        binding.countMainAparences.text = getString(R.string.main_activity_aparences)+ nuevoContador
    }

    private fun obtenerContador(): Int {
        return sharedPreferences.getInt("contadorEjecuciones", 0)
    }

    private fun initSwitch()
    {
        // Configura un Listener para el cambio de estado del Switch
        binding.switchChangeMode.setOnCheckedChangeListener { _, isChecked ->
            setDarkModeEnabled(isChecked)
            recreate()
        }
    }

    private fun updateDarkMode()
    {
        // Obtiene el valor actual del modo oscuro desde SharedPreferences
        val isDarkModeEnabled = sharedPreferences.getBoolean("isDarkModeEnabled", false)

        // Actualiza el estado del Switch basándote en el valor almacenado
        binding.switchChangeMode.isChecked = isDarkModeEnabled

        // Aplica el modo oscuro basándote en el valor almacenado
        if (isDarkModeEnabled)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.switchChangeMode.text = getString(R.string.night_mode)
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.switchChangeMode.text = getString(R.string.day_mode)
        }
    }

    private fun setDarkModeEnabled(isDarkModeEnabled: Boolean) {
        // Almacena el valor del modo oscuro en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putBoolean("isDarkModeEnabled", isDarkModeEnabled)
        editor.apply()

        // Aplica el cambio en el modo oscuro inmediatamente
        updateDarkMode()
    }

    private fun showUserName()
    {
        if(!nameUser.isNullOrEmpty())
        {
            binding.userName.text = nameUser
            login = true
        }
    }

    //Inicializar RecyclerView
    private fun initRecyclerView()
    {
        adapter = PlayerAdapter(ListaJugadores)
        binding.rvPlayers.layoutManager = LinearLayoutManager(this)
        binding.rvPlayers.adapter = adapter
    }

    private fun initImagen()
    {
        binding.ivIniciarSesion.setOnClickListener{
            if(!login)
            {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }else
                showLogoutConfirmationDialog()

        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro de que quieres cerrar sesión?")
            .setPositiveButton("Sí") { dialog, which ->
                login = false
                binding.userName.text = ""
            }
            .setNegativeButton("No") { dialog, which ->
                // El usuario ha decidido no cerrar sesión, cierra el diálogo o realiza alguna acción adicional si es necesario
                dialog.dismiss()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Como vamos a obtener la información
    private fun getRetrofit():Retrofit
    {
        return Retrofit.Builder()
                .baseUrl("https://www.balldontlie.io/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun startShimmerEffect()
    {
        binding.rvPlayers.isVisible = false
        shimmerPlayerCards.startShimmer()
        shimmerPlayerCards.isVisible = true
    }

    private fun stopShimmerEffect()
    {
        binding.rvPlayers.isVisible = true
        shimmerPlayerCards.isVisible = false
    }

    private fun getAllPlayers()
    {
        startShimmerEffect()

        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(APIService::class.java).getPlayers("100")
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

                    adapter.setOnItemClickListener(object : RecyclerViewInterface{
                        override fun onClick(position: Int)
                        {
                            val intent = Intent(this@MainActivity, PlayerActivity::class.java)

                            intent.putExtra("IDPLAYER",ListaJugadores[position].id.toString())

                            if(login)
                                intent.putExtra("NAMEUSER",nameUser)

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

                stopShimmerEffect()
            }
        }

        screenSplash.setKeepOnScreenCondition{false}
    }

    //Funcion asincrona que busca a los jugadores por su nombre
    private fun searchByName(query:String)
    {
        startShimmerEffect()

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

                    adapter.setOnItemClickListener(object : RecyclerViewInterface{
                        override fun onClick(position: Int)
                        {
                            val intent = Intent(this@MainActivity, PlayerActivity::class.java)

                            intent.putExtra("IDPLAYER",ListaJugadores[position].id.toString())

                            //Log.d("ID-fuera",ListaJugadores[position].id.toString())

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
                stopShimmerEffect()
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