package com.example.multiplatformmaps.android

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Segment
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(), UserLocationObjectListener {

    private var mapView: MapView? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var markPosition: PlacemarkMapObject? = null
    private var newPoint: Point? = null
    private var segment: Segment? = null
    private var mapObjects: MapObjectCollection? = null

    override fun onCreate(savedInstanceState: Bundle?) {


        MapKitFactory.setApiKey("e32d0322-1b71-454f-8170-f4615bfb6472")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapView = findViewById(R.id.mapview)
        mapView!!.map.isRotateGesturesEnabled = false
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 14F, 0F, 0F))

        //Map mark
        /*markPosition = mapView!!.map.mapObjects.addPlacemark(
            Point(58.5942, 49.6839),
            ImageProvider.fromResource(this, R.drawable.search_result)
        )*/
        mapObjects = mapView!!.map.mapObjects.addCollection()
        createMarkMapObjects()

        var mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true

        userLocationLayer!!.setObjectListener(this)

        segment = Segment(Point(58.5940, 49.6819),Point(58.5938, 49.6856))
        newPoint = Geo.closestPoint(Point(58.5941, 49.6826), segment)

    }

    override fun onStop() {
        mapView!!.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView!!.onStart()
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationLayer!!.setAnchor(
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.5).toFloat()),
            PointF((mapView!!.width * 0.5).toFloat(), (mapView!!.height * 0.83).toFloat())
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this, R.drawable.user_arrow
            )
        )
        //var info: InputStream = assets.open("map_mark")
        //Log.v("Test", info.toString())
        val pinIcon = userLocationView.pin.useCompositeIcon()
        pinIcon.setIcon(
            "icon",
            ImageProvider.fromResource(this, R.drawable.icon),
            IconStyle().setAnchor(PointF(0f, 0f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(0f)
                .setScale(1f)
        )
        pinIcon.setIcon(
            "pin",
            ImageProvider.fromResource(this, R.drawable.search_result),
            IconStyle().setAnchor(PointF(0.5f, 0.5f))
                .setRotationType(RotationType.ROTATE)
                .setZIndex(1f)
                .setScale(0.5f)
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE and -0x66000001
    }

    class MarkMapObjectData(markName: String, description: String) {
        var markName: String = markName
        var description: String = description


    }

    private fun createMarkMapObjects(){
        val jsonData = applicationContext.resources.openRawResource(
            applicationContext.resources.getIdentifier(
                "map_marks",
                "raw",applicationContext.packageName
            )
        ).bufferedReader().use{it.readText()}

        val outputJsonString = JSONObject(jsonData)
        val marks = outputJsonString.getJSONArray("marks") as JSONArray

        for (i in 0 until marks.length()){
            val latitude = marks.getJSONObject(i).get("latitude")
            val longitude = marks.getJSONObject(i).get("longitude")
            val name = marks.getJSONObject(i).get("name")
            val description = marks.getJSONObject(i).get("description")

            var markObject: PlacemarkMapObject? = mapObjects?.addPlacemark(
                Point(latitude as Double, longitude as Double),
                ImageProvider.fromResource(this, R.drawable.search_result))

            markObject?.userData = MarkMapObjectData(name as String,description as String)
            markObject?.addTapListener(MarkMapObjectTapListener)

        }
    }

    private val MarkMapObjectTapListener: MapObjectTapListener = object : MapObjectTapListener {
        override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
            if (mapObject is PlacemarkMapObject) {
                val markData: Any? = mapObject.userData
                if (markData is MarkMapObjectData) {
                    val textView = findViewById<TextView>(R.id.markName)
                    textView.text = markData.markName
                    val toast = Toast.makeText(
                        applicationContext,
                        "Mark " + markData.markName + " and description "
                                + markData.description,
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }
            return true
        }

    }

    fun onFactButtonClick(view: View) {
        val toast = Toast.makeText(
            applicationContext,
            "Fact",
            Toast.LENGTH_SHORT
        )
        toast.show()
    }

    fun onMusicButtonClick(view: View){
        val toast = Toast.makeText(
            applicationContext,
            "Music",
            Toast.LENGTH_SHORT
        )
        toast.show()
    }

    fun onVerseButtonClick(view: View){
        val toast = Toast.makeText(
            applicationContext,
            "Verse",
            Toast.LENGTH_SHORT
        )
        toast.show()
    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }
}
