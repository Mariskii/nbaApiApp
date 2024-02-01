package com.example.nba_stats

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "favoritos",
    foreignKeys = [ForeignKey(entity = Usuario::class, parentColumns = ["nombre"], childColumns = ["usuarioName"])])
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jugadorId: String,
    val usuarioName: String
)