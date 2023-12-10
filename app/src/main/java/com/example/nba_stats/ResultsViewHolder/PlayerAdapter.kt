package com.example.nba_stats.ResultsViewHolder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nba_stats.Player
import com.example.nba_stats.PlayerActivity
import com.example.nba_stats.R

class PlayerAdapter(val players:List<Player>): RecyclerView.Adapter<PlayerViewHolder>()
{
    private lateinit var mListener : RecyclerViewInterface

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder
    {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = (layoutInflater.inflate(R.layout.item_searched_player, parent, false))
        return  PlayerViewHolder(itemView,mListener)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int)
    {
        val item: Player = players[position]
        holder.bind(item)
    }

    fun setOnItemClickListener(listener: RecyclerViewInterface)
    {
        mListener = listener
    }
}