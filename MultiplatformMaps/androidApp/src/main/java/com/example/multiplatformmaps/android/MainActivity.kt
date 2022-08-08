package com.example.multiplatformmaps.android

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.multiplatformmaps.Greeting
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

fun greet(): String {
    return Greeting().greeting()
}

class MainActivity : AppCompatActivity() {

    private var mapview: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("e32d0322-1b71-454f-8170-f4615bfb6472")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        mapview = findViewById<View>(R.id.mapview) as MapView
        mapview!!.getMap().move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, .0f),
            null
        )
    }

    override fun onStop() {
        mapview!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview!!.onStart()
    }
}
