package com.example.realhealth.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realhealth.R
import com.example.realhealth.model.Gym
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.example.realhealth.util.FavoriteManager
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.content.Context
import kotlin.math.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class HomeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var toggleButton: Button
    private lateinit var btnSearchHere: Button
    private lateinit var favoritesButton: ImageButton
    private lateinit var btnSort: ImageButton
    private lateinit var recyclerView: FastScrollRecyclerView
    private lateinit var placesClient: PlacesClient
    private lateinit var backPressedCallback: OnBackPressedCallback
    private var isMapMode = true
    private val gymList = mutableListOf<Gym>()
    private var isFirstMapIdle = true
    private var hasMarkers = false

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

    private fun sortGymsByDistance() {
        gymList.sortBy {
            val distanceStr = it.distance ?: return@sortBy Double.MAX_VALUE
            when {
                distanceStr.contains("km") -> {
                    val value = distanceStr.replace("km", "").trim().toDoubleOrNull() ?: Double.MAX_VALUE
                    value * 1000 // km → m 변환
                }
                distanceStr.contains("m") -> {
                    distanceStr.replace("m", "").trim().toDoubleOrNull() ?: Double.MAX_VALUE
                }
                else -> Double.MAX_VALUE
            }
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun sortGymsByRating() {
        gymList.sortByDescending { it.rating ?: 0.0 }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun resizeMarkerIcon(context: Context, resId: Int, width: Int, height: Int): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
        val resized = Bitmap.createScaledBitmap(bitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(resized)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        toggleButton = view.findViewById(R.id.toggle_button)
        recyclerView = view.findViewById(R.id.gym_list)
        btnSearchHere = view.findViewById(R.id.btn_search_here)
        btnSort = view.findViewById(R.id.btn_sort)
        favoritesButton = view.findViewById(R.id.btn_show_favorites)

        recyclerView.visibility = View.GONE
        btnSort.visibility = View.GONE

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = GymAdapter(gymList) { gym ->
            val action = HomeFragmentDirections.actionNavigationHomeToGymDetailFragment(gym)
            findNavController().navigate(action)
        }

        toggleButton.setOnClickListener {
            val center = mMap.cameraPosition.target

            if (isMapMode) {
                searchNearbyGyms(mMap.cameraPosition.target)
                toggleButton.text = "목록"
                recyclerView.visibility = View.GONE
                btnSort.visibility = View.GONE
            } else {
                toggleButton.text = getString(R.string.workout) // "운동"으로 바꿈
                recyclerView.visibility = View.VISIBLE
                btnSort.visibility = View.VISIBLE
                recyclerView.adapter?.notifyDataSetChanged()

                if (!hasMarkers) {
                    mMap.clear()
                    gymList.clear()
                    searchNearbyGyms(center)
                }
            }
            isMapMode = !isMapMode
        }

        btnSort.setOnClickListener {
            val popup = PopupMenu(requireContext(), btnSort)
            popup.menuInflater.inflate(R.menu.sort_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_sort_distance -> { sortGymsByDistance(); true }
                    R.id.menu_sort_rating -> { sortGymsByRating(); true }
                    else -> false
                }
            }
            popup.show()
        }

        favoritesButton.setOnClickListener {
            val favoriteGyms = gymList.filter {
                FavoriteManager.isFavorite(requireContext(), it.placeId)
            }

            if (favoriteGyms.isEmpty()) {
                Toast.makeText(requireContext(), "즐겨찾기 목록이 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mMap.clear()
            val markerIcon = resizeMarkerIcon(requireContext(), R.drawable.favorites, 80, 80)
            favoriteGyms.forEach { gym ->
                mMap.addMarker(
                    MarkerOptions()
                        .position(LatLng(gym.lat, gym.lng))
                        .title(gym.name)
                        .icon(markerIcon)
                )
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 상세화면에서 돌아왔을 때 "목록보기"로 전환
        parentFragmentManager.setFragmentResultListener("show_list_mode", viewLifecycleOwner) { _, _ ->
            isMapMode = false
            recyclerView.visibility = View.VISIBLE
            btnSort.visibility = View.VISIBLE
            toggleButton.text = "지도보기"
            recyclerView.adapter?.notifyDataSetChanged()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        hasMarkers -> {
                            mMap.clear()
                            hasMarkers = false
                            recyclerView.visibility = View.GONE
                            btnSort.visibility = View.GONE
                            toggleButton.text = getString(R.string.workout)
                            isMapMode = true
                        }

                        !isMapMode -> {
                            isMapMode = true
                            recyclerView.visibility = View.GONE
                            btnSort.visibility = View.GONE
                            toggleButton.text = "목록"
                            mMap.clear()
                        }

                        // ✅ 지도 모드일 때 → 앱 종료
                        else -> {
                            isEnabled = false
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    }
                   }
            }
        )

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(requireContext())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                mMap.isMyLocationEnabled = true
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.5563, 126.9723), 14f))

        mMap.setOnCameraMoveStartedListener {
            btnSearchHere.visibility = View.GONE
        }

        mMap.setOnCameraIdleListener {
            if (isFirstMapIdle) {
                isFirstMapIdle = false
            } else {
                // isMapMode일 때는 버튼 안 보이게, 목록 모드일 때만 보이게
                btnSearchHere.visibility = if (isMapMode) View.GONE else View.VISIBLE
            }
        }

        btnSearchHere.setOnClickListener {
            val center = mMap.cameraPosition.target
            mMap.clear()
            gymList.clear()
            recyclerView.adapter?.notifyDataSetChanged()
            searchNearbyGyms(center)
            btnSearchHere.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mMap.isInitialized) {
            mMap.clear()
            recyclerView.adapter?.notifyDataSetChanged()
            recyclerView.visibility = if (isMapMode) View.GONE else View.VISIBLE
            toggleButton.text = if (isMapMode) getString(R.string.workout) else "지도보기"
            isFirstMapIdle = true
        }
    }

    fun calculateStraightDistanceMeter(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val latDistance = (lat2 - lat1) * 111000 // 위도 1도 당 대략 111km = 111,000m
        val lngDistance = (lng2 - lng1) * 111000 * cos(Math.toRadians(lat1)) // 경도는 위도에 따라 변함
        return sqrt(latDistance * latDistance + lngDistance * lngDistance)
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

                            val fullAddress = detailJson.optString("formatted_address", "주소 정보 없음")
                            val address = fullAddress.replaceFirst("^대한민국\\s*".toRegex(), "")
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

                            val rating = detailJson.optDouble("rating", -1.0).takeIf { it >= 0 }

                            val origin = center // 검색 중심 좌표
                            val destination = LatLng(lat, lng)

                            // 직선 거리(m) 계산
                            val distanceMeters = calculateStraightDistanceMeter(origin.latitude, origin.longitude, lat, lng)

                            // 보기 편하게 km 혹은 m 단위 문자열로 변환
                            val distanceText = if (distanceMeters >= 1000) {
                                String.format("%.2f km", distanceMeters / 1000)
                            } else {
                                String.format("%.0f m", distanceMeters)
                            }

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

                            val gymicon = resizeMarkerIcon(requireContext(), R.drawable.muscles, 80, 80)

                            handler.post {
                                val alreadyExists = gymList.any { it.placeId == gym.placeId }
                                if (!alreadyExists) {
                                    gymList.add(gym)
                                    recyclerView.adapter?.notifyItemInserted(gymList.size - 1)
                                    mMap.addMarker(
                                        MarkerOptions()
                                            .position(destination)
                                            .title(name)
                                            .icon(gymicon)
                                    )
                                    hasMarkers = true
                                }
                            }
                        }
                    })
                }
            }
        })
    }
}