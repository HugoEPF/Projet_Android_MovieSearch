package com.epf.MovieSearch

import com.google.gson.annotations.SerializedName


data class MovieObject(
    var results : Array<movieJsonObject>,
    var cast : Array<castJsonObject>
)

data class castJsonObject(
    var poster_path: String,
    var id : Int
)
data class AddFavorite(
    var status_message : String
)



data class AddToFavoritesRequest(
    @SerializedName("media_type")
    val mediaType: String, // Type de média (par exemple, "movie")
    @SerializedName("media_id")
    val mediaId: Int, // ID du film que vous souhaitez ajouter en favoris
    @SerializedName("favorite")
    val favorite: Boolean // Indique si vous souhaitez ajouter ou supprimer le film des favoris
)
data class movieJsonObject(
    var original_title : String,
    var overview : String,
    var poster_path : String,
    var release_date : String,
    var id : Int,
    var popularity : String,
    var genres : ArrayList<genreJsonObject>
)

data class genreJsonObject(
    var id : Int,
    var name : String

)


