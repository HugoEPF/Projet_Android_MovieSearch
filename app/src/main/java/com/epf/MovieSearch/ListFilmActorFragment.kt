package com.epf.MovieSearch

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ListFilmActorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var name : TextView

    lateinit var imageView : ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = arguments
        if (args != null) {
            val peopleId = args.getInt("peopleId")
            getToDetailsPeople(peopleId)
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_list_movie, container, false)
        val backButton: Button = root.findViewById(R.id.backButton)
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }




        return root
    }

    fun getToDetailsPeople(peopleId : Int) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val recyclerViewPopular: RecyclerView = requireView().findViewById(R.id.moviePeopleTitle)
        val peopleService = retrofit.create(Service::class.java)
        val result = peopleService.getServicePeopleMovies(peopleId)

        result.enqueue(object : Callback<MovieObject> {
            override fun onResponse(call: Call<MovieObject>, response: Response<MovieObject>) {
                var result = response.body()
                val moviesPopularList = result?.cast ?: emptyArray()

                val adapterPopular = MovieAdapter(moviesPopularList, requireContext())
                adapterPopular?.setOnItemClickListener(object: MovieAdapter.OnItemClickListener {
                    override fun onItemClick(movie: castJsonObject) {
                        val bundle = Bundle()
                        bundle.putInt("movieId", movie.id)
                        findNavController().navigate(R.id.navigation_detail_movie, bundle)
                    }
                })
                recyclerViewPopular.adapter = adapterPopular

            }
            override fun onFailure(call: Call<MovieObject>, t: Throwable) {
                Toast.makeText(requireContext().applicationContext, "fail server", Toast.LENGTH_LONG).show()
            }

        })
    }


    class MovieAdapter(private val movies : Array<castJsonObject>, val context : Context) : RecyclerView.Adapter<ListFilmActorFragment.MovieViewHolder>() {

        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(movie: castJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListFilmActorFragment.MovieViewHolder {
            return ListFilmActorFragment.MovieViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: ListFilmActorFragment.MovieViewHolder, position: Int) {
            val movie : castJsonObject = movies[position]
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
            ListFilmActorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }


    }

}