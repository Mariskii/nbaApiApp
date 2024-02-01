package com.example.nba_stats

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(tableName = "usuarios", indices = [Index(value = ["nombre"], unique = true)])
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val passwd: String
)
