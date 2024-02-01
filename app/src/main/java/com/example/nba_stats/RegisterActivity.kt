package com.example.nba_stats

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.nba_stats.databinding.ActivityMainBinding
import com.example.nba_stats.databinding.ActivityRegisterBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var usuarioDao: UsuarioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar la base de datos
        val database = AppDatabase.getInstance(this)
        usuarioDao = database.usuarioDao()

        initButtonRegister()
        initAlreadyAccountNavigation()
    }

    private fun initAlreadyAccountNavigation()
    {
        binding.alreadyAccount.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("ResourceType")
    private fun initButtonRegister()
    {
        binding.buttonRegister.setOnClickListener{
                if(!nameIsEmpty() && passwdCoinciden() && !userNameIsNullOrEmpty())
                {
                    createUser()
                    binding.information.text = "Usuario creado correctamente, volviendo al inicio"

                    Thread.sleep(3000)

                    navigateToMainActivity()
                }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USERNAME", binding.editTextName.text.toString())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    private fun userNameIsNullOrEmpty(): Boolean
    {
        var isEmpty:Boolean = true

        if(!binding.editTextName.text.isNullOrEmpty())
        {
            isEmpty = false
            binding.information.text = "No has introducido un nombre de usuario"
        }

        return isEmpty
    }

    private fun passwdCoinciden(): Boolean
    {
        val coinciden:Boolean

        if(binding.editTextPasswd.text.toString() == binding.editTextPasswdConfirm.text.toString())
        {
            coinciden = true
        }
        else
        {
            coinciden = false
            binding.information.text = "Contraseñas no coinciden"
        }

        return coinciden
    }

    private fun nameIsEmpty(): Boolean
    {
        var isEmpry:Boolean = false

        if(binding.editTextName.text.isNullOrEmpty())
            isEmpry = true

        return isEmpry
    }

    private fun createUser()
    {
        CoroutineScope(Dispatchers.IO).launch {
            // Insertar un usuario
            val nuevoUsuario = Usuario(nombre = binding.editTextName.text.toString(), passwd = binding.editTextPasswd.text.toString())
            usuarioDao.insertarUsuario(nuevoUsuario)

        }
    }
}