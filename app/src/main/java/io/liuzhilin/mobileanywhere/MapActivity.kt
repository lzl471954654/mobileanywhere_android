package io.liuzhilin.mobileanywhere

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.amap.api.maps.AMap
import com.amap.api.maps.model.*
import com.amap.api.maps.model.MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER
import com.google.gson.reflect.TypeToken
import io.liuzhilin.mobileanywhere.bean.Point
import io.liuzhilin.mobileanywhere.callback.GetPointCallBack
import io.liuzhilin.mobileanywhere.callback.RequestCallback
import io.liuzhilin.mobileanywhere.fragments.ShareMenuFragment
import io.liuzhilin.mobileanywhere.ui.map.MapViewModel
import io.liuzhilin.mobileanywhere.util.GsonUtils
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.lang.Exception

class MapActivity : AppCompatActivity() , AMap.OnMyLocationChangeListener , AMap.OnMarkerClickListener , GetPointCallBack{

    lateinit var amap : AMap
    val locationStyle = MyLocationStyle()
    var location : Location? = null
    lateinit var viewModel: MapViewModel
    val markerMap = HashMap<Marker,Point>()

    private var lastTimeUpdateMarkerTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        map.onCreate(savedInstanceState)
        initMap()
        initMapViewModel()
        startLocation()
    }

    fun initMapViewModel() {
        viewModel = MapViewModel(handler)
        supportActionBar?.hide()
        add_button.setOnClickListener {
            val fragment = ShareMenuFragment()
            fragment.setGetPointCallBack(this)
            fragment.show(supportFragmentManager,"ShareMenu")
        }
        refresh_button.setOnClickListener {
            doAsync {
                if (System.currentTimeMillis() - lastTimeUpdateMarkerTime < 10 * 1000){
                    runOnUiThread {
                        Toast.makeText(this@MapActivity,"已经是最新数据",Toast.LENGTH_SHORT).show()
                    }
                    return@doAsync
                }else{
                    for (entry in markerMap.entries) {
                        entry.key.remove()
                    }
                    markerMap.clear()
                    getAllPoint()
                    lastTimeUpdateMarkerTime = System.currentTimeMillis()
                }
            }
        }
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
        getAllPoint()
    }

    fun addMaker(point: Point) : Marker{
        val longitude = point.longitude
        val latitude = point.latitude
        val la = LatLng(latitude,longitude)
        return amap.addMarker(MarkerOptions().position(la).snippet("DefaultMaker"))
    }

    fun getAllPoint(){
        viewModel.getAllPoint(object : RequestCallback {
            override fun failed(e: Exception?) {
                handler.post { Toast.makeText(this@MapActivity,"获取附近地点失败",Toast.LENGTH_LONG).show() }
            }

            override fun success(json: String?) {
                handler.post { getAllPointSuccess(json!!) }
            }
        })
    }

    fun getAllPointSuccess(json : String){
        val jsonObject = JSONObject(json)
        val jsonArray = jsonObject.getJSONArray("data")
        val pointList = GsonUtils.gson.fromJson<List<Point>>(jsonArray.toString(),(object : TypeToken<List<Point>>(){}).type)
        for (point in pointList) {
            val marker = addMaker(point)
            markerMap[marker] = point
        }
    }

    override fun onMyLocationChange(location: Location?) {
        this.location = location
        val longitude = location!!.longitude
        val latitude = location.latitude
        Log.e("onMyLocationChanged","longitude : $longitude  latitude : $latitude")
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = "onMarkerClick :"
        Log.e(tag,marker.toString())
        val point = markerMap[marker]
        if (point == null){
            Log.e(tag,"point is not in markerMap")
        }else{
            Log.e(tag,point.toString())
        }
        val map = hashMapOf<String,String>()
        map["pointId"] = "${point!!.longitude}:${point.latitude}"
        PYQActivity.startActivityWithParam(this,map)
        return true
    }

    override fun getPointData(): String {
        val longitude = location!!.longitude
        val latitude = location!!.latitude
        return "$longitude:$latitude"
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
        handler.removeCallbacksAndMessages(null)
        viewModel.destory()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map.onSaveInstanceState(outState)
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {

        }
    }


    companion object{
        fun startActivity(context : Context){
            val intent = Intent(context,MapActivity::class.java)
            context.startActivity(intent)
        }
    }

}