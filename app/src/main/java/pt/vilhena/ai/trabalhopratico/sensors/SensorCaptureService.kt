package pt.vilhena.ai.trabalhopratico.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pt.vilhena.ai.trabalhopratico.data.common.Constants.GPS_INTERVAL
import pt.vilhena.ai.trabalhopratico.sensors.gps.DefaultLocationClient
import pt.vilhena.ai.trabalhopratico.sensors.gps.LocationClient

class SensorCaptureService(private val context: Context) : SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private lateinit var locationClient: LocationClient

    //  Variables for record sensor data
    var accelerometerData = floatArrayOf(0f, 0f, 0f)
    var gyroscopeData = floatArrayOf(0f, 0f, 0f)
    var magneticFieldData = floatArrayOf(0f, 0f, 0f)

    var location = Location("")

    /*
       Register Sensor for Capturing
    */
    fun registerSensorsListeners() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //  Registering Listeners for each sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL)

        startGpsLocation()
    }

    fun unregisterSensorsListeners() {
        sensorManager.unregisterListener(this)
    }

    /*
        Start getting GPS data (Latitude Longitude Altitude Accuracy Bearing) every GPS_INTERVAL
        This will stop when the coroutine stops
     */
    @SuppressLint("MissingPermission")
    private fun startGpsLocation() {
        locationClient = DefaultLocationClient(context, LocationServices.getFusedLocationProviderClient(context))
        locationClient.getLocationUpdates(GPS_INTERVAL)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                this.location = location
            }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    //  OnSensorChange reads the values from the corresponding sensor everytime there is a change
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // Log.v(TAG, "Accelerometer ${event.values[2]}")
                accelerometerData = event.values
            }
            Sensor.TYPE_GYROSCOPE -> {
                // Log.v(TAG, "Gyroscope ${event.values[2]}")
                gyroscopeData = event.values
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                // Log.v(TAG, "Magnetic Field ${event.values[2]}")
                magneticFieldData = event.values
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
