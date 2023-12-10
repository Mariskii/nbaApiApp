package com.example.nba_stats

import java.util.Date

data class PlayerStatsResponse(
    val data: List<PlayerData>
)

data class PlayerData(
    val player_id: Int,
    val season: Int,
    val min: String,
    val fgm: Double,
    val fga: Double,
    val fg3m: Double,
    val fg3a: Double,
    val ftm: Double,
    val fta: Double,
    val oreb: Double,
    val dreb: Double,
    val reb: Double,
    val ast: Double,
    val stl: Double,
    val blk: Double,
    val turnover: Double,
    val pf: Double,
    val pts: Double,
    val fg_pct: Double,
    val fg3_pct: Double,
    val ft_pct: Double,
    val game: Game
)

data class Game(
    val id: Int,
    val date: Date,
    val home_team_id: Int,
    val home_team_score: Int,
    val season: Int,
    val visitor_team_id: Int,
    val visitor_team_score: Int
)