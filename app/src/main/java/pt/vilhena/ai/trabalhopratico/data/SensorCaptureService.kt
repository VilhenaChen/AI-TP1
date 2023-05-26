package pt.vilhena.ai.trabalhopratico.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.MutableLiveData

class SensorCaptureService(val context: Context) : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    val sensorDataLiveData = MutableLiveData<String>()

    fun registerSensors() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun unregisterSensors() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            Log.v("SensorChange GYROSCOPE", event.values[2].toString())
        }
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                Log.v("SensorChange ACELEROMETER", event.values[2].toString())
            }
            Sensor.TYPE_GYROSCOPE -> {
                Log.v("SensorChange GYROSCOPE", event.values[2].toString())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
