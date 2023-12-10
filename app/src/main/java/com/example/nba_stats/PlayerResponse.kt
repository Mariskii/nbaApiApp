package com.example.nba_stats

import com.google.gson.annotations.SerializedName

//SE CREAN DIFERENTES CLASES YA QUE EL JSON QUE DEVUELVE LA API TIENE DIFERENTES OBJETOS ANIDADOS EN EL

data class PlayerResponse(
    @SerializedName("data") val players: List<Player>,
    @SerializedName("meta") val meta: Meta
)

data class Player(
    @SerializedName("id") val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("position") val position: String,
    @SerializedName("height_feet") val heightFeet: Int,
    @SerializedName("height_inches") val heightInches: Int,
    @SerializedName("weight_pounds") val weightPounds: Int,
    @SerializedName("team") val team: Team
)

data class Team(
    @SerializedName("id") val id: Int,
    @SerializedName("abbreviation") val abbreviation: String,
    @SerializedName("city") val city: String,
    @SerializedName("conference") val conference: String,
    @SerializedName("division") val division: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("name") val name: String
)

data class Meta(
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("next_page") val nextPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_count") val totalCount: Int
)
