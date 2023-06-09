package com.epf.MovieSearch

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
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
/**
 * A simple [Fragment] subclass.
 * Use the [ComptesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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


    fun getToMovie() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Récupérer la RecyclerView pour les films populaires




        val recyclerViewPopular: RecyclerView = requireView().findViewById(R.id.movieTitlePopular)
        val movieServicePopular = retrofit.create(Service::class.java)
        val resultPopular = movieServicePopular.getServicePopular()

        resultPopular.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val moviesPopularList = result?.results ?: emptyArray()
                    val adapterPopular = MovieAdapter(moviesPopularList, requireContext())
                    adapterPopular?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)
                        }
                    })
                    recyclerViewPopular.adapter = adapterPopular
                }
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })

        // Récupérer la RecyclerView pour les films en cours
        val recyclerViewNow: RecyclerView = requireView().findViewById(R.id.movieTitleNow)
        val movieServiceNow = retrofit.create(Service::class.java)
        val resultNow = movieServiceNow.getServiceNow()

        resultNow.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val moviesNowList = result?.results ?: emptyArray()
                    val adapterNow = MovieAdapter(moviesNowList, requireContext())
                    adapterNow?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)
                        }
                    })
                    recyclerViewNow.adapter = adapterNow
                }
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })
        val recyclerViewTopRated: RecyclerView = requireView().findViewById(R.id.movieTitleTopRated)
        val movieServiceTopRated = retrofit.create(Service::class.java)
        val resultTopRated = movieServiceTopRated.getServiceTopRated()

        resultTopRated.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val moviesTopRatedList = result?.results ?: emptyArray()
                    val adapterTopRated = MovieAdapter(moviesTopRatedList, requireContext())
                    adapterTopRated?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)
                        }
                    })
                    recyclerViewTopRated.adapter = adapterTopRated

                }
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })
        val recyclerViewUpComing: RecyclerView = requireView().findViewById(R.id.movieTitleUpComing)
        val movieServiceUpComing = retrofit.create(Service::class.java)
        val resultUpComing = movieServiceUpComing.getServiceUpComing()

        resultUpComing.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val moviesUpComingList = result?.results ?: emptyArray()
                    val adapterUpComing = MovieAdapter(moviesUpComingList, requireContext())
                    adapterUpComing?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                        override fun onItemClick(movie: movieJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("movieId", movie.id)
                            findNavController().navigate(R.id.navigation_detail_movie, bundle)
                        }
                    })
                    recyclerViewUpComing.adapter = adapterUpComing

                }
            }

            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Appeler getToNowMovie() ici
        getToMovie()

    }
    class MovieAdapter(private val movies : Array<movieJsonObject>, val context : Context) : RecyclerView.Adapter<HomeFragment.MovieViewHolder>() {

        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: movieJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragment.MovieViewHolder {
            return HomeFragment.MovieViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: HomeFragment.MovieViewHolder, position: Int) {
            val movie : movieJsonObject = movies[position]
            val view = holder.itemView
            val poster = view.findViewById<ImageView>(R.id.movieHomeItem)
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
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_home_movie, viewGroup, false))








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