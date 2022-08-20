package com.example.multiplatformmaps.android

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PointF
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Segment
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.*
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
    private var mapObjects: MapObjectCollection? = null
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null

    private var mark1: Point = Point(58.5942,49.6839)
    private var mark2: Point = Point(58.5918, 49.6821)
    //private var mapObjectsList: List<Point> = listOf(mark1, mark2)
    private var markPositionData1: MarkPositionData = MarkPositionData(mark1,"music")
    private var markPositionData2: MarkPositionData = MarkPositionData(mark2,"music")
    private var mapObjectsList: List<MarkPositionData> = listOf(markPositionData1,markPositionData2)

    private var factText: String? =null
    private var factTitle: String? =null
    private var sound: MediaPlayer? = null
    private var resID: Int? = null

    var userLocationPoint: Point = Point(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {

        MapKitFactory.setApiKey("e32d0322-1b71-454f-8170-f4615bfb6472")
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        mapView = findViewById(R.id.mapview)
        mapView!!.map.isRotateGesturesEnabled = false
        mapView!!.map.move(CameraPosition(Point(0.0, 0.0), 14F, 0F, 0F))

        mapObjects = mapView!!.map.mapObjects.addCollection()
        createMarkMapObjects()

        var mapKit = MapKitFactory.getInstance()
        mapKit.resetLocationManagerToDefault()
        userLocationLayer = mapKit.createUserLocationLayer(mapView!!.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true

        locationManager = MapKitFactory.getInstance().createLocationManager()
        createLocationLister()

        userLocationLayer!!.setObjectListener(this)

        sound = MediaPlayer.create(this, R.raw.music)
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

        subscribeToLocationUpdate()
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
            val musicName = marks.getJSONObject(i).get("musicName")
            val factTitle = marks.getJSONObject(i).get("factTitle")
            val factText = marks.getJSONObject(i).get("factText")

            var markObject: PlacemarkMapObject? = mapObjects?.addPlacemark(
                Point(latitude as Double, longitude as Double),
                ImageProvider.fromResource(this, R.drawable.search_result))

            markObject?.userData = MarkMapObjectData(
                name as String,
                musicName as String,
                factTitle as String,
                factText as String)
            markObject?.addTapListener(MarkMapObjectTapListener)

            mapObjectsList.plus(markObject)
        }
    }

    private val MarkMapObjectTapListener: MapObjectTapListener = object : MapObjectTapListener {
        override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
            if (mapObject is PlacemarkMapObject) {
                val markData: Any? = mapObject.userData
                if (markData is MarkMapObjectData) {
                    val textView = findViewById<TextView>(R.id.markName)

                    textView.text = markData.markName
                    factTitle = markData.factTitle
                    factText = markData.factText
                    resID = resources.getIdentifier(markData.musicName, "raw", packageName)
                }
            }
            return true
        }
    }

    fun onFactButtonClick(view: View) {
        if (factTitle != null && factText != null){
            var builder = AlertDialog.Builder(this)
            builder.setTitle(factTitle)
            builder.setMessage(factText)
            builder.setPositiveButton("Close",DialogInterface.OnClickListener{ dialog, _ ->
                dialog.cancel()
            })

            var alert = builder.create()
            alert.show()
        } else {
            val toast = Toast.makeText(
                applicationContext,
                "Select mark first",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
    }

    fun onMusicButtonClick(view: View){
        if (factTitle != null && factText !== null){
            if (sound?.isPlaying == true) {
                sound?.stop()
            }else{

                sound = MediaPlayer.create(this, resID!!)
                sound?.start()
            }
        } else {
            val toast = Toast.makeText(
                applicationContext,
                "Select mark first",
                Toast.LENGTH_SHORT
            )
            toast.show()
        }
    }

    private fun createLocationLister(){
        locationListener = object: LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {
                Log.v("Test123", p0?.toString() ?: "No status")
            }

            override fun onLocationUpdated(location: Location) {
                userLocationPoint = location.position
                Log.v(
                    "Test123",
                    "lat=${location?.position?.latitude ?: ""} " +
                            "lon=${location?.position?.longitude ?: ""}"
                )

                mapObjectsList.forEach {
                    var distance: Double = Geo.distance(userLocationPoint, it.position)
                    Log.v("Test123", distance.toString() + "")
                    if (distance < 50) {
                        mapObjects?.addPlacemark(
                            Point(
                                it.position.latitude as Double,
                                it.position.longitude + 0.0009 as Double
                            ),
                            ImageProvider.fromResource(this@MainActivity, R.drawable.search_result)
                        )
                        resID = resources.getIdentifier(it.musicName, "raw", packageName)
                        if (sound?.isPlaying == true) {
                            sound?.stop()
                        }else{

                            sound = MediaPlayer.create(this@MainActivity, resID!!)
                            sound?.start()
                        }
                    }
                }
            }
        }
    }

    private fun subscribeToLocationUpdate() {
        if (locationManager != null && locationListener != null) {
            locationManager!!.subscribeForLocationUpdates(
                0.0,
                0,
                15.0,
                false,
                FilteringMode.OFF,
                locationListener!!
            )
        }
    }

    class MarkMapObjectData(
        var markName: String,
        var musicName: String,
        var factTitle: String,
        var factText: String
    ){

    }
    class MarkPositionData(
        var position: Point,
        var musicName: String
    ){

    }

    override fun onObjectRemoved(p0: UserLocationView) {

    }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {

    }
}
