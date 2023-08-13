package pt.vilhena.ai.trabalhopratico.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pt.vilhena.ai.trabalhopratico.data.common.Constants.SENSOR_CAPTURE_REFRESH_RATE
import pt.vilhena.ai.trabalhopratico.data.common.Constants.TAG
import pt.vilhena.ai.trabalhopratico.sensors.SensorCaptureService
import java.time.LocalDateTime
import java.util.Calendar
import java.util.UUID
import kotlin.system.measureTimeMillis

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private var sensorRecordingJob: Job? = null
    private val _currentActivity = MutableLiveData<String>()
    val currentActivity = _currentActivity

    lateinit var sessionID: String
    private var sensorCaptureService = SensorCaptureService(getApplication())

    private val calendar = Calendar.getInstance()
    private lateinit var startDate: LocalDateTime

    fun changeSelectedActivity(activity: String) {
        _currentActivity.value = activity
    }

    //  Start Capturing sensor data
    fun startCapture() {
        createSessionID()
        sensorCaptureService.registerSensorsListeners()
        sensorRecordingJob = viewModelScope.launch {
            recordSensorData().collect {
                Log.d(TAG, it)
            }
        }
    }

    //  Capture sensor data in 50Hz/20ms
    private fun recordSensorData(): Flow<String> = flow {
        while (true) {
            val timeElapsed = measureTimeMillis {
                val ace = sensorCaptureService.accelerometerData[0]
                val gyro = sensorCaptureService.gyroscopeData[0]
                val mag = sensorCaptureService.magneticFieldData[0]
                val loc = sensorCaptureService.location.latitude
                emit("ace: $ace gyro: $gyro mag: $mag loc: $loc")
            }

            val delayTime = SENSOR_CAPTURE_REFRESH_RATE - timeElapsed
            if (delayTime > 0) {
                delay(delayTime)
            }
        }
    }

    //  Stop capture sensor data
    fun stopCapture() {
        sessionID = ""
        sensorCaptureService.unregisterSensorsListeners()
        sensorRecordingJob?.cancel()
    }

    //  Create SessionID based on date, time and a random 3 characters
    private fun createSessionID() {
        startDate = LocalDateTime.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
        )

        sessionID =
            "ActivityDetectorISEC" + "_" + startDate.toString() + "_" + UUID.randomUUID().toString().takeLast(3)
        sessionID = sessionID.replace("([T,:-])".toRegex(), "")
    }

    //  File Section

    //  Write on the CSV file the data
    private fun writeFile() {
    }
}
