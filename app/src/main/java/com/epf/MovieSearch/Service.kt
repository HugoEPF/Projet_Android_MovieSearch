package com.epf.MovieSearch

import android.text.Editable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
companion object {
    const val API_KEY = "bda2506b87eefa8eb7d6bdf4166cd662"
}

    @GET("movie?api_key=$API_KEY")
    fun getService(@Query("query") movie: Editable): Call<MovieObject>

    @GET("movie/now_playing?language=en-US&page=1&api_key=$API_KEY")
    fun getServiceNow(): Call<MovieObject>
    @GET("movie/popular?language=en-US&page=1&api_key=$API_KEY")
    fun getServicePopular(): Call<MovieObject>
    @GET("person/popular?language=en-US&page=1&api_key=$API_KEY")
    fun getServicePeople(): Call<PeopleObject>
    @GET("movie/top_rated?language=en-US&page=1&api_key=$API_KEY")
    fun getServiceTopRated(): Call<MovieObject>
    @GET("movie/upcoming?language=en-US&page=1&api_key=$API_KEY")
    fun getServiceUpComing(): Call<MovieObject>
    @GET("movie/{movie_id}?api_key=$API_KEY")
    fun getServiceDetailsMovie(
        @Path("movie_id") movieId: Int
    ) : Call<movieJsonObject>
    @GET("person/{person_id}?api_key=$API_KEY")
    fun getServiceDetailsPeople(
        @Path("person_id") peopleId: Int
    ) : Call<peopleJsonObject>
    @GET("movie/{movie_id}/recommendations?api_key=$API_KEY")
    fun getServiceRecommendationsMovie(
        @Path("movie_id") movieId: Int
    ) : Call<MovieObject>

    @GET ("account/{accountId}/favorite/movies")
    fun getFavoriteMovies (
        @Path("accountId") accountId: Int
    ) : Call<MovieObject>

    @POST("account/{account_id}/favorite")
    suspend fun addFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Body request: AddToFavoritesRequest
    ) : Response<AddFavorite>

    @GET("person/{person_id}/movie_credits?api_key=$API_KEY")
    fun getServicePeopleMovies(
        @Path("person_id") peopleId: Int
    ) : Call<MovieObject>

}