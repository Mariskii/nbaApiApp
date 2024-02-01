package com.example.nba_stats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nba_stats.databinding.ActivityLoginBinding
import com.example.nba_stats.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la base de datos
        val database = AppDatabase.getInstance(this)
        usuarioDao = database.usuarioDao()

        initNoAccountNavigation()
        initButtonLogin()
    }

    private fun initNoAccountNavigation()
    {
        binding.noAccount.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initButtonLogin()
    {
        binding.buttonLogin.setOnClickListener{
            CoroutineScope(Dispatchers.IO).launch {
                if(login())
                {
                        navigateToMainActivity()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USERNAME", binding.editTextName.text.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private suspend fun login(): Boolean {
        var loged = false

        val job = CoroutineScope(Dispatchers.Main).async {
            val inicioSesionExitoso = searchUser()

            if (inicioSesionExitoso) {
                loged = true
            } else {
                binding.tvInformation.text = "No se ha podido iniciar sesión, revise los datos"
            }
        }

        job.await() // Esperar a que la operación asíncrona se complete

        return loged
    }


    private suspend fun searchUser(): Boolean
    {
        return withContext(Dispatchers.IO) {
            val user = async {
                // Realizar la verificación en la base de datos
                usuarioDao.login(binding.editTextName.text.toString(), binding.editTextPasswd.text.toString())
            }.await()

            // Devolver true si el usuario no es nulo (inicio de sesión exitoso), false de lo contrario
            user != null
        }
    }
}