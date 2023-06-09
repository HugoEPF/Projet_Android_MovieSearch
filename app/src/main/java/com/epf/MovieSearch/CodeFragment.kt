package com.epf.MovieSearch
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.epf.MovieSearch.R
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

class CodeFragment : Fragment() {
    private val API_KEY = "bda2506b87eefa8eb7d6bdf4166cd662"
    private lateinit var scan_button: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_code, container, false)

        scan_button = view.findViewById(R.id.scan_button)
        scan_button.setOnClickListener {
            launchQRScanner()
        }

        return view
    }

    private fun launchQRScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setOrientationLocked(false)
        integrator.setPrompt("Scan QR code")
        integrator.initiateScan()
    }

    private fun handleResult(result: IntentResult?) {
        if (result != null && result.contents != null) {
            val scannedId = result.contents
            // Faites quelque chose avec l'ID récupéré à partir du QR code
            Toast.makeText(requireContext(), "Scanned ID: $scannedId", Toast.LENGTH_SHORT).show()
            val bundle = Bundle()
            bundle.putInt("movieId", scannedId.toInt())
            findNavController().navigate(R.id.navigation_detail_movie, bundle)


        } else {
            Toast.makeText(requireContext(), "Scan failed", Toast.LENGTH_SHORT).show()
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result: IntentResult? =
            IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        handleResult(result)
    }
}


