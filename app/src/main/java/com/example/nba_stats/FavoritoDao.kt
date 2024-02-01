package com.example.nba_stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoritoDao {
    @Insert
    suspend fun insertarFavorito(favorito: Favorito)

    // En FavoritoDao
    @Query("SELECT COUNT(*) FROM favoritos WHERE usuarioName = :nombreUsuario AND jugadorId = :idJugador")
    suspend fun esJugadorFavorito(idJugador: String, nombreUsuario: String): Int


    @Query("DELETE FROM favoritos WHERE jugadorId = :idJugador AND usuarioName = :nombreUsuario")
    suspend fun borrarFavorito(idJugador: String, nombreUsuario: String)
}