package pt.vilhena.ai.trabalhopratico.data

import android.hardware.Sensor
import android.util.Log

class SensorCalculatorService() {
    private var acelerometerData = ArrayList<FloatArray>()
    private var gyroscopeData = ArrayList<FloatArray>()

    //  Calculate the approated data of each sensor before writing the line on the cvs file
    private fun calculateSensorData(sensor: Sensor) {
        when(sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {}
            Sensor.TYPE_GYROSCOPE -> {}
            // GPS
            else -> {
                Log.v("TESTE", "Not expected")
            }
        }
    }

}
