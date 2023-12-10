package com.example.nba_stats.StatsViewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nba_stats.PlayerData
import com.example.nba_stats.R

class StatsAdapter(val seasonStats:List<PlayerData>): RecyclerView.Adapter<StatsViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatsViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        return StatsViewHolder((layoutInflater.inflate(R.layout.item_player_stats, parent, false)))
    }

    override fun getItemCount(): Int = seasonStats.size

    override fun onBindViewHolder(holder: StatsViewHolder, position: Int)
    {
        val item: PlayerData = seasonStats[position]
        holder.bind(item)
    }

}