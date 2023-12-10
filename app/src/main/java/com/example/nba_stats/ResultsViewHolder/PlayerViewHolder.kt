package com.example.nba_stats.ResultsViewHolder

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.nba_stats.Player
import com.example.nba_stats.R
import com.example.nba_stats.TeamsResources
import com.example.nba_stats.databinding.ItemSearchedPlayerBinding
import com.squareup.picasso.Picasso

class PlayerViewHolder(view: View, listener:RecyclerViewInterface):RecyclerView.ViewHolder(view)
{
    private val binding = ItemSearchedPlayerBinding.bind(view)

    init{
        view.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }

    @SuppressLint("SetTextI18n")
    fun bind(player: Player)
    {
        val imageLink:String = "https://www.hispanosnba.com/imagenes/jugadores_img/${player.firstName}-"+player.lastName.replace("'","")+".jpg"

        // Actualiza las vistas con los datos del jugador
        binding.namePlayerSearched.text = "${player.firstName} ${player.lastName}"
        binding.teamPlayerSearched.text= player.team.abbreviation
        binding.positionPlayerSearched.text = player.position
        Picasso.get().load(imageLink).into(binding.ivPlayerSearched)
        binding.cvPlayerSearched.setCardBackgroundColor(getPlayerTeamColor(player))
    }

    fun getPlayerTeamColor(player: Player):Int
    {
        var backgroundColor:Int = R.color.searched_player

        backgroundColor = TeamsResources.teamColors[player.team.abbreviation] ?: R.color.searched_player

        return ContextCompat.getColor(itemView.context, backgroundColor)
    }
}