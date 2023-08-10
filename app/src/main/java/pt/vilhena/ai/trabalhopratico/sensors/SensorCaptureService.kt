package pt.vilhena.ai.trabalhopratico.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pt.vilhena.ai.trabalhopratico.sensors.gps.DefaultLocationClient
import pt.vilhena.ai.trabalhopratico.sensors.gps.LocationClient

class SensorCaptureService(private val context: Context) : SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private lateinit var locationClient: LocationClient
    val accelerometerLiveData = MutableLiveData<FloatArray>()

    val gyroscopeLiveData = MutableLiveData<FloatArray>()

    var location: String = "null"

    //  Register Sensor for Capturing
    fun registerSensors() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //  Registering Listeners for each sensor
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        startGpsLocation()
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("MissingPermission")
    fun startGpsLocation() {
        locationClient = DefaultLocationClient(context, LocationServices.getFusedLocationProviderClient(context))
        locationClient.getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val long = location.longitude
                this.location = "$lat, $long"
            }.launchIn(CoroutineScope(Dispatchers.IO))
    }

    //  OnSensorChange reads the values from the corresponding sensor
    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                // Log.v(TAG, "Acelerometer ${event.values[2]}")
                accelerometerLiveData.value = event.values
            }
            Sensor.TYPE_GYROSCOPE -> {
                // Log.v(TAG, "Gyroscope ${event.values[2]}")
                gyroscopeLiveData.value = event.values
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
