package com.example.nba_stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE nombre = :nombreUsuario AND passwd = :passwd")
    fun login(nombreUsuario: String, passwd: String): Usuario?
}