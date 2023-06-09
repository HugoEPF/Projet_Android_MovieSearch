package com.epf.MovieSearch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
/**
 * A simple [Fragment] subclass.
 * Use the [ComptesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavorisFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var recyclerView: RecyclerView
    lateinit var EmptyText : TextView
    lateinit var EmptyImage : ImageView

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
        val root = inflater.inflate(R.layout.fragment_favoris, container, false)
       recyclerView = root.findViewById(R.id.recyclerviewFavoris)
        EmptyText = root.findViewById(R.id.textEmpty)
        EmptyImage = root.findViewById(R.id.imageEmpty)
        getFavorisMovie()
        return root

    }


    fun getFavorisMovie() {
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
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        recyclerView.layoutManager = GridLayoutManager(requireContext().applicationContext, GridLayoutManager.VERTICAL)
        val Service = retrofit.create(Service::class.java)
        val result = Service.getFavoriteMovies(19533693)
        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                val result = response.body()
                if (result?.results?.isEmpty() == true) {
                        EmptyText.visibility = View.VISIBLE
                        EmptyImage.visibility =View.VISIBLE
                } else {
                    val adapter = result?.results?.let {
                        MovieAdapterFavoris(it, requireContext().applicationContext)
                    }
                    Log.d("TAG", result.toString())

                    adapter?.setOnItemClickListener(object: MovieAdapterFavoris.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)

                        }
                    })

                    recyclerView.adapter = adapter

                }
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(
                    requireContext().applicationContext,
                    "fail server",
                    Toast.LENGTH_LONG
                ).show()
            }

        })

    }

    class MovieAdapterFavoris(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<MovieFavorisViewHolder>() {
        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: movieJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieFavorisViewHolder {
            return MovieFavorisViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieFavorisViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val textView = view.findViewById<TextView>(R.id.movieItemDetails)
            val imageView = view.findViewById<ImageView>(R.id.imageViewTv)
            textView.text = movie.original_title
            val icon = movie.poster_path
            Picasso.get().load("https://image.tmdb.org/t/p/w188_and_h282_bestv2$icon").into(imageView)
            view.setOnClickListener() {
                itemClickListener?.onItemClick(movie)
            }

        }


    }

    class MovieFavorisViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_movie_favoris, viewGroup, false))



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
            FavorisFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}