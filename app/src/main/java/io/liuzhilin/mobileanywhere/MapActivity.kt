package io.liuzhilin.mobileanywhere

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : Activity() , AMap.OnMyLocationChangeListener , AMap.OnMarkerClickListener{

    lateinit var amap : AMap
    val locationStyle = MyLocationStyle()
    var location : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        map.onCreate(savedInstanceState)
        initMap()
        startLocation()
    }

    private fun initMap() {
        amap = map.map
        amap.setOnMyLocationChangeListener(this)
        amap.setOnMarkerClickListener(this)
    }


    fun startLocation(){
        locationStyle.interval(50000)
        locationStyle.myLocationType(LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        amap.myLocationStyle = locationStyle
        amap.uiSettings.isMyLocationButtonEnabled = true
        amap.isMyLocationEnabled = true
        amap.uiSettings.isScaleControlsEnabled = false
    }

    fun addMaker(){
        val longitude = location!!.longitude
        val latitude = location!!.latitude
        val la = LatLng(latitude+1,longitude+1)
        val marker = amap.addMarker(MarkerOptions().position(la).title("TEST").snippet("DefaultMaker"))

    }

    override fun onMyLocationChange(location: Location?) {
        this.location = location
        val longitude = location!!.longitude
        val latitude = location.latitude
        Log.e("onMyLocationChanged","longitude : $longitude  latitude : $latitude")
        addMaker()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        Log.e("onMarkerClick : ",marker.toString())

        return true
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }


    companion object{
        fun startActivity(context : Context){
            val intent = Intent(context,MapActivity::class.java)
            context.startActivity(intent)
        }
    }

}