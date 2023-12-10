package com.example.nba_stats

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface APIService
{
    @GET("players")
    suspend fun getPlayersByName(@Query("search") search:String):Response<PlayerResponse>
    @GET
    suspend fun getPlayersById(@Url url:String):Response<Player>
    @GET("stats")
    suspend fun getStatsByPlayerId(@Query("player_ids[]") playerIds:String):Response<PlayerStatsResponse>
}