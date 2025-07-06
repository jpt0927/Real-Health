package com.example.realhealth.ui.home

import com.example.realhealth.model.Gym
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.realhealth.ui.home.HomeFragmentDirections
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.OnBackPressedCallback
import android.widget.TextView
import com.example.realhealth.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Call
import okhttp3.Callback
import java.io.IOException
import org.json.JSONObject
import android.os.Handler
import android.os.Looper

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var toggleButton: Button
    private lateinit var recyclerView: FastScrollRecyclerView
    private lateinit var placesClient: PlacesClient
    private var isMapMode = true
    private val gymList = mutableListOf<Gym>()

    // 위치 권한 요청 런처
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && ::mMap.isInitialized) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        toggleButton = view.findViewById(R.id.toggle_button)
        recyclerView = view.findViewById(R.id.gym_list)

        recyclerView.visibility = View.GONE
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = GymAdapter(gymList) { gym ->
            val action = HomeFragmentDirections.actionNavigationHomeToGymDetailFragment(gym)
            findNavController().navigate(action)
        }


        toggleButton.setOnClickListener {
            if (isMapMode) {
                searchNearbyGyms(mMap.cameraPosition.target)

                toggleButton.text = "목록보기"
                recyclerView.visibility = View.GONE
            } else {
                toggleButton.text = "지도보기"
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter?.notifyDataSetChanged()
            }

            isMapMode = !isMapMode
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(requireContext())

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as? SupportMapFragment

        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val seoulStation = LatLng(37.5563, 126.9723)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulStation, 14f))
    }

    override fun onResume() {
        super.onResume()
        if (::mMap.isInitialized) {
            mMap.clear()
            gymList.clear()
            recyclerView.adapter?.notifyDataSetChanged()
            recyclerView.visibility = View.GONE
            toggleButton.text = getString(R.string.workout)
            isMapMode = true
        }
    }

    private fun getWalkingDistance(origin: LatLng, destination: LatLng, callback: (String?) -> Unit) {
        val apiKey = getString(R.string.google_maps_key)
        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=walking" +
                "&language=ko" +
                "&key=$apiKey"

        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                val json = JSONObject(body ?: "")
                val routes = json.optJSONArray("routes")
                if (routes != null && routes.length() > 0) {
                    val legs = routes.getJSONObject(0).optJSONArray("legs")
                    if (legs != null && legs.length() > 0) {
                        val distance = legs.getJSONObject(0).optJSONObject("distance")?.optString("text")
                        callback(distance)
                        return
                    }
                }
                callback(null)
            }
        })
    }

    private fun searchNearbyGyms(center: LatLng) {
        val apiKey = getString(R.string.google_maps_key)
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=${center.latitude},${center.longitude}" +
                "&radius=2000" +
                "&keyword=헬스" +
                "&language=ko" +
                "&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: return
                val json = JSONObject(body)
                val results = json.getJSONArray("results")

                val handler = Handler(Looper.getMainLooper())

                for (i in 0 until results.length()) {
                    val place = results.getJSONObject(i)
                    val name = place.getString("name")
                    val location = place.getJSONObject("geometry").getJSONObject("location")
                    val lat = location.getDouble("lat")
                    val lng = location.getDouble("lng")
                    val placeId = place.getString("place_id")

                    val detailsUrl = "https://maps.googleapis.com/maps/api/place/details/json" +
                            "?place_id=$placeId" +
                            "&fields=name,formatted_address,formatted_phone_number,opening_hours,photos,rating" +
                            "&language=ko" +
                            "&key=$apiKey"

                    val detailRequest = Request.Builder().url(detailsUrl).build()

                    client.newCall(detailRequest).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            e.printStackTrace()
                        }

                        override fun onResponse(call: Call, response: Response) {
                            val detailBody = response.body?.string() ?: return
                            val detailJson = JSONObject(detailBody).optJSONObject("result") ?: return

                            val address = detailJson.optString("formatted_address", "주소 정보 없음")
                            val phone = detailJson.optString("formatted_phone_number", "전화번호 정보 없음")
                            val openingHours = detailJson.optJSONObject("opening_hours")
                                ?.optJSONArray("weekday_text")?.let { arr ->
                                    (0 until arr.length()).joinToString("\n") { arr.getString(it) }
                                } ?: "영업시간 정보 없음"

                            val photoReference = detailJson.optJSONArray("photos")
                                ?.optJSONObject(0)
                                ?.optString("photo_reference")

                            val photoUrl = if (photoReference != null) {
                                "https://maps.googleapis.com/maps/api/place/photo" +
                                        "?maxwidth=400" +
                                        "&photoreference=$photoReference" +
                                        "&key=$apiKey"
                            } else null

                            val rating = detailJson.optDouble("rating", -1.0)
                                .takeIf { it >= 0 }

                            // 도보 거리 계산
                            val origin = LatLng(center.latitude, center.longitude)
                            val destination = LatLng(lat, lng)

                            getWalkingDistance(origin, destination) { distanceText ->
                                val gym = Gym(
                                    name = name,
                                    lat = lat,
                                    lng = lng,
                                    placeId = placeId,
                                    address = address,
                                    phoneNumber = phone,
                                    openingHours = openingHours,
                                    photoReference = photoUrl,
                                    rating = rating,
                                    distance = distanceText
                                )

                                handler.post {
                                    gymList.add(gym)
                                    recyclerView.adapter?.notifyItemInserted(gymList.size - 1)

                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(destination)
                                            .title(name)
                                    )
                                }
                            }
                        }
                    })
                }
            }
        })
    }
}