package io.liuzhilin.mobileanywhere

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : Activity(){



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        map2d.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        map2d.onResume()
    }

    override fun onPause() {
        super.onPause()
        map2d.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        map2d.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        map2d.onSaveInstanceState(outState)
    }


    companion object{
        fun startActivity(context : Context){
            val intent = Intent(context,MapActivity::class.java)
            context.startActivity(intent)
        }
    }

}