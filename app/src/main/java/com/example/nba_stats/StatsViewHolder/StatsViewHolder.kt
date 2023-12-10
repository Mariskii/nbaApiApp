package com.example.nba_stats.StatsViewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.nba_stats.Player
import com.example.nba_stats.PlayerData
import com.example.nba_stats.ResultsViewHolder.RecyclerViewInterface
import com.example.nba_stats.databinding.ItemPlayerStatsBinding
import com.squareup.picasso.Picasso

class StatsViewHolder(view: View) : RecyclerView.ViewHolder(view)
{
    private val binding = ItemPlayerStatsBinding.bind(view)

    fun bind(stats: PlayerData)
    {
        //establecer año de la temporada
        binding.statsSeason.text = stats.game.season.toString()
        //establecer puntos por partido
        binding.pointsPerGame.text = stats.pts.toString()
        //establecer faltas por partido
        binding.foulsPerGame.text = stats.fgm.toString()
        //establecer asistencias por partido
        binding.assistsPerGame.text = stats.ast.toString()
        //establecer rebotes por partido
        binding.reboundsPerGame.text = stats.reb.toString()
        //establecer robos por partido
        binding.stealsPerGame.text = stats.stl.toString()
        //establecer minutos por partido
        binding.minutesPerGame.text = stats.min
    }
}