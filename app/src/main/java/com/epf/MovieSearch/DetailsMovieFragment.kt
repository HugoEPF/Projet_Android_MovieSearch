package com.epf.MovieSearch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class DetailsMovieFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var title : TextView
    lateinit var overview : TextView
    lateinit var popularity : TextView
    lateinit var imageView : ImageView
    lateinit var switchFavoris : Switch

    lateinit var genres : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_details, container, false)
        imageView = root.findViewById(R.id.imageView)
        title = root.findViewById(R.id.titleTv)
        overview = root.findViewById(R.id.overviewTv)
        popularity = root.findViewById(R.id.popularityTv)
        genres = root.findViewById(R.id.genresTv)
        recyclerView = root.findViewById(R.id.recyclerViewDetails)
        switchFavoris = root.findViewById(R.id.SwitchAddMovieFavourite)
        val args = arguments
        if (args != null) {
            val movieID = args.getInt("movieId")
            getToDetailsMovie(movieID)
            getToRecommendationsMovie(movieID)
            isMovieInFavourite(movieID)
            switchFavoris.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    addFavourite(movieID)
                } else {
                    removeFavourite(movieID)
                }

            }
        }

        val backButton: Button = root.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return root
    }

    fun getToDetailsMovie(movieId : Int) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(Service::class.java)
        val result = movieService.getServiceDetailsMovie(movieId)

        result.enqueue(object : Callback<movieJsonObject> {
            override fun onResponse(call: Call<movieJsonObject>, response: Response<movieJsonObject>) {
                var result = response.body()
                title.text = result?.original_title + " (" +result?.release_date +")"
                overview.text = result?.overview
                popularity.text = result?.popularity
                val genreNames = result?.genres?.map { it.name }
                val genreString = genreNames?.joinToString(", ")
                genres.text = genreString
                val icon = result?.poster_path
                Picasso.get().load("https://image.tmdb.org/t/p/w188_and_h282_bestv2$icon").into(imageView)
            }

            override fun onFailure(call: Call<movieJsonObject>, t: Throwable) {
                Toast.makeText(requireContext().applicationContext, "fail ", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun getToRecommendationsMovie(movieId : Int) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        recyclerView.layoutManager = GridLayoutManager(requireContext().applicationContext, GridLayoutManager.VERTICAL)
        val movieService = retrofit.create(Service::class.java)
        val result = movieService.getServiceRecommendationsMovie(movieId)

        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                    val result = response.body()
                val adapter = result?.results?.let {
                    MovieAdapterDetails(it, requireContext().applicationContext)
                }
                adapter?.setOnItemClickListener(object: MovieAdapterDetails.OnItemClickListener {
                    override fun onItemClick(movie: movieJsonObject) {
                        //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                        val bundle = Bundle()
                        bundle.putInt("movieId", movie.id)
                        findNavController().navigate(R.id.navigation_detail_movie, bundle)
                    }


            })
                recyclerView.adapter = adapter
            }


            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext().applicationContext, "Fail server", Toast.LENGTH_LONG).show()

            }

        })


    }

    fun addFavourite(movie : Int) {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiZGEyNTA2Yjg3ZWVmYThlYjdkNmJkZjQxNjZjZDY2MiIsInN1YiI6IjY0NjYzMWNiMzNhMzc2MDEwMWY5YjFlYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.4M5heTMC1A7QK14ZsRzf_Yc8DjZfbOzUAOq3PmfRvIA"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val addToFavoritesRequest = AddToFavoritesRequest(
            mediaType = "movie", // Remplacez par le type de média approprié si nécessaire
            mediaId = movie, // Remplacez par l'ID du film que vous souhaitez ajouter en favoris
            favorite = true // Définissez sur "true" pour ajouter le film en favoris
        )
        val movieService = retrofit.create(Service::class.java)
        lifecycleScope.launch {
            val result = movieService.addFavoriteMovies(19533693,addToFavoritesRequest)
            if(result.isSuccessful) {
                Log.d("TAG", result.message())
            } else {
                Log.e("TAG", "Request failed: ${result.code()} - ${result.message()}")
            }

        }
    }

    fun removeFavourite(movie : Int) {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(
                        "Authorization",
                        "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiZGEyNTA2Yjg3ZWVmYThlYjdkNmJkZjQxNjZjZDY2MiIsInN1YiI6IjY0NjYzMWNiMzNhMzc2MDEwMWY5YjFlYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.4M5heTMC1A7QK14ZsRzf_Yc8DjZfbOzUAOq3PmfRvIA"
                    )
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val addToFavoritesRequest = AddToFavoritesRequest(
            mediaType = "movie", // Remplacez par le type de média approprié si nécessaire
            mediaId = movie, // Remplacez par l'ID du film que vous souhaitez ajouter en favoris
            favorite = false // Définissez sur "true" pour ajouter le film en favoris
        )
        val movieService = retrofit.create(Service::class.java)
        lifecycleScope.launch {
            val result = movieService.addFavoriteMovies(19533693,addToFavoritesRequest)
            if(result.isSuccessful) {
                Log.d("TAG", result.message())
            } else {
                Log.e("TAG", "Request failed: ${result.code()} - ${result.message()}")
            }

        }
    }

    fun isMovieInFavourite(movieId : Int) {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiZGEyNTA2Yjg3ZWVmYThlYjdkNmJkZjQxNjZjZDY2MiIsInN1YiI6IjY0NjYzMWNiMzNhMzc2MDEwMWY5YjFlYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.4M5heTMC1A7QK14ZsRzf_Yc8DjZfbOzUAOq3PmfRvIA")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        val Service = retrofit.create(Service::class.java)
        val result = Service.getFavoriteMovies(19533693)
        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                val result = response.body()
                val isMovieInFavourites = result?.results?.any {
                    it.id == movieId
                }
                switchFavoris.isChecked = isMovieInFavourites == true
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext().applicationContext, "fail server", Toast.LENGTH_LONG).show()
            }

        })



    }

    class MovieAdapterDetails(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<MovieDetailsViewHolder>() {

        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: movieJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieDetailsViewHolder {
            return MovieDetailsViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieDetailsViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val textView = view.findViewById<TextView>(R.id.movieItemDetails)
            val imageView = view.findViewById<ImageView>(R.id.imageViewTv)
            textView.text = movie.original_title
            val icon = movie.poster_path
            Picasso.get().load("https://image.tmdb.org/t/p/w188_and_h282_bestv2$icon").into(imageView)
            view.setOnClickListener {
                itemClickListener?.onItemClick(movie)
            }

        }


    }

    class MovieDetailsViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_movie_details, viewGroup, false))


        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ComptesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


    }

}