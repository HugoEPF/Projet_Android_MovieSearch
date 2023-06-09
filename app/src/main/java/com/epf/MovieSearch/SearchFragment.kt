package com.epf.MovieSearch

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentContainer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SearchFragment : Fragment(){
    lateinit var searchMovie : EditText
    lateinit var searchMovieButton : Button
    lateinit var overview : TextView
    lateinit var  recyclerView: RecyclerView
    lateinit var movieName : View
    //var poster_path : String = ""

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)

        searchMovie = rootView.findViewById(R.id.searchMovie)
        searchMovieButton = rootView.findViewById(R.id.search)
        overview = rootView.findViewById(R.id.overview)
        movieName = rootView.findViewById(R.id.movieName)

        searchMovieButton.setOnClickListener {
            val titleMovie = searchMovie.text
            if (titleMovie.isEmpty()) {
                Toast.makeText(context, "Le champ recherche est vide", Toast.LENGTH_LONG).show()
            } else {
                getToSearchMovie(titleMovie)
            }
        }

        return rootView
    }
    private fun getRecyclerView() {
        recyclerView = requireView().findViewById(R.id.movieTitle)
        // Configurez le layoutManager et l'adaptateur ici
    }
    fun getToSearchMovie(titleMovie: Editable) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/search/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val movieService = retrofit.create(Service::class.java)
        val result = movieService.getService(titleMovie)

        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if(response.isSuccessful) {
                    var result = response.body()
                    getRecyclerView()
                    val adapter = result?.results?.let { MovieAdapter(it, requireContext().applicationContext) }
                    adapter?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)

                        }


                    })
                    recyclerView.adapter = adapter
                }

            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })
    }
    class MovieAdapter(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<MovieViewHolder>() {

        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: movieJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
            return MovieViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val poster = view.findViewById<ImageView>(R.id.movieItemPoster)
            val textView = view.findViewById<TextView>(R.id.movieItemTitle)
            textView.text = movie.original_title
            val path = movie.poster_path
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w188_and_h282_bestv2/$path")
                .into(poster)
            view.setOnClickListener {
                itemClickListener?.onItemClick(movie)
            }
        }
    }




    class MovieViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_movie, viewGroup, false))





    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
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