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
class PeopleFragment : Fragment() {
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


    fun getToPeople() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Récupérer la RecyclerView pour les films populaires


        val recyclerViewPeople: RecyclerView = requireView().findViewById(R.id.PeopleTrending)
        val peopleService = retrofit.create(Service::class.java)
        val resultPeople = peopleService.getServicePeople()

        resultPeople.enqueue(object : Callback<PeopleObject> {
            override fun onResponse(call: Call<PeopleObject>, response: Response<PeopleObject>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val peoplePopularList = result?.results ?: emptyArray()
                    val adapterPeople = PeopleAdapter(peoplePopularList, requireContext())
                    adapterPeople?.setOnItemClickListener(object :
                        PeopleAdapter.OnItemClickListener {
                        override fun onItemClick(people: peopleJsonObject) {
                            //Toast.makeText(requireContext().applicationContext, "clique", Toast.LENGTH_LONG).show()
                            val bundle = Bundle()
                            bundle.putInt("peopleId", people.id)
                            findNavController().navigate(R.id.navigation_list_people, bundle)
                        }
                    })
                    recyclerViewPeople.adapter = adapterPeople
                }
            }

            override fun onFailure(call: Call<PeopleObject>, t: Throwable) {
                Toast.makeText(requireContext(), "Erreur serveur", Toast.LENGTH_LONG).show()
            }


        })
    }
    // Récupérer la RecyclerView pour les films en cours




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_people, container, false)

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Appeler getToNowMovie() ici
        getToPeople()

    }
    class PeopleAdapter(private val peoples : Array<peopleJsonObject>, val context : Context) : RecyclerView.Adapter<PeopleFragment.PeopleViewHolder>() {

        private var itemClickListener: OnItemClickListener? = null

        interface OnItemClickListener {
            fun onItemClick(people: peopleJsonObject)
        }
        fun setOnItemClickListener(listener: OnItemClickListener) {
            itemClickListener = listener
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleFragment.PeopleViewHolder {
            return PeopleFragment.PeopleViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun getItemCount() = peoples.size

        override fun onBindViewHolder(holder: PeopleFragment.PeopleViewHolder, position: Int) {
            val people: peopleJsonObject = peoples[position]
            val view = holder.itemView
            val poster = view.findViewById<ImageView>(R.id.peopleTrendingItem)
            val textView = view.findViewById<TextView>(R.id.peopleItemName)
            val path = people.profile_path
            textView.text =  people.name
            Picasso.get()
                .load("https://image.tmdb.org/t/p/w188_and_h282_bestv2/$path")
                .into(poster)
            view.setOnClickListener {
                itemClickListener?.onItemClick(people)
            }
        }
    }
    class PeopleViewHolder(inflater: LayoutInflater, viewGroup: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_people, viewGroup, false))








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