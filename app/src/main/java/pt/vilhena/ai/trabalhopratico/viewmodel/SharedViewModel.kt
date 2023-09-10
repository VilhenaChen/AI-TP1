package pt.vilhena.ai.trabalhopratico.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pt.vilhena.ai.trabalhopratico.data.common.Constants.AUTOMATIC_INTERVAL
import pt.vilhena.ai.trabalhopratico.data.common.Constants.SENSOR_CAPTURE_REFRESH_RATE
import pt.vilhena.ai.trabalhopratico.data.file.FileUtils
import pt.vilhena.ai.trabalhopratico.data.weka.WekaService
import pt.vilhena.ai.trabalhopratico.sensors.SensorCaptureService
import java.time.LocalDateTime
import java.util.Calendar
import java.util.UUID
import kotlin.system.measureTimeMillis

class SharedViewModel(private val application: Application) : AndroidViewModel(application) {

    var activityStarted = false
    private var isAutomatic = true
    private var firstTime = true

    private var sensorRecordingJob: Job? = null
    private var timerJob: Job? = null
    private var wekaJob: Job? = null
    private val _currentActivity = MutableLiveData<String>()
    val currentActivity = _currentActivity
    private val _elapsedTime = MutableLiveData<String>()
    val elapsedTime = _elapsedTime

    lateinit var sessionID: String
    private var sensorCaptureService = SensorCaptureService(getApplication())

    private val calendar = Calendar.getInstance()
    private lateinit var startDate: LocalDateTime

    private lateinit var fileUtils: FileUtils
    private lateinit var wekaService: WekaService

    private val _fileSentFlag = MutableLiveData<Boolean>()
    val fileSentFlag = _fileSentFlag

    fun setSelectedActivity(activity: String) {
        _currentActivity.value = activity
    }

    //  Start Capturing sensor data
    fun startCapture() {
        activityStarted = true
        fileUtils = FileUtils(isAutomatic)
        wekaService = WekaService(application)
        createSessionID()
        sensorCaptureService.registerSensorsListeners()
        sensorRecordingJob = GlobalScope.launch(Dispatchers.IO) {
            recordSensorData().collect { }
        }
        timerJob = GlobalScope.launch {
            startTimer().collect() {
                _elapsedTime.postValue(formatTime(it))
            }
        }
        if (isAutomatic) {
            fileUtils.createFileModel("automatic", "automatic")
            wekaJob = GlobalScope.launch { guessActivity().collect { } } }
    }

    //  Capture sensor data in 50Hz/20ms
    private fun recordSensorData(): Flow<String> = flow {
        while (true) {
            val timeElapsed = measureTimeMillis {
                val lat = sensorCaptureService.location.latitude
                val long = sensorCaptureService.location.longitude
                val altitude = sensorCaptureService.location.altitude
                val accuracy = sensorCaptureService.location.accuracy
                val bearing = sensorCaptureService.location.bearing
                val timestamp = sensorCaptureService.location.time
                val x_acc = sensorCaptureService.accelerometerData[0]
                val y_acc = sensorCaptureService.accelerometerData[1]
                val z_acc = sensorCaptureService.accelerometerData[2]
                val x_gyro = sensorCaptureService.gyroscopeData[0]
                val y_gyro = sensorCaptureService.gyroscopeData[1]
                val z_gyro = sensorCaptureService.gyroscopeData[2]
                val x_mag = sensorCaptureService.magneticFieldData[0]
                val y_mag = sensorCaptureService.magneticFieldData[0]
                val z_mag = sensorCaptureService.magneticFieldData[0]
                if (!isAutomatic) {
                    currentActivity.value?.let {
                        fileUtils.addRow(
                            sessionID,
                            lat.toString(),
                            long.toString(),
                            altitude.toString(),
                            accuracy.toString(),
                            bearing.toString(),
                            timestamp.toString(),
                            x_acc.toString(),
                            y_acc.toString(),
                            z_acc.toString(),
                            x_gyro.toString(),
                            y_gyro.toString(),
                            z_gyro.toString(),
                            x_mag.toString(),
                            y_mag.toString(),
                            z_mag.toString(),
                            it,
                        )
                    }
                } else {
                    fileUtils.addRowAuto(
                        altitude.toString(),
                        accuracy.toString(),
                        bearing.toString(),
                        x_acc.toString(),
                        y_acc.toString(),
                        z_acc.toString(),
                        x_gyro.toString(),
                        y_gyro.toString(),
                        z_gyro.toString(),
                        x_mag.toString(),
                        y_mag.toString(),
                        z_mag.toString(),
                    )
                }
            }

            val delayTime = SENSOR_CAPTURE_REFRESH_RATE - timeElapsed
            if (delayTime > 0) {
                delay(delayTime)
            }
        }
    }

    private fun guessActivity(): Flow<String> = flow {
        while (true) {
            val timeElapsed = measureTimeMillis {
                if (!firstTime) {
                    val activity =
                        wekaService.wekaClassifyFromModel(fileUtils.prepLastLineForWeka())
                    _currentActivity.postValue((activity))
                } else {
                    firstTime = false
                    _currentActivity.postValue("none")
                }
            }

            val delayTime = AUTOMATIC_INTERVAL - timeElapsed
            if (delayTime > 0) {
                delay(delayTime)
            }
        }
    }

    //  Stop capture sensor data
    fun stopCapture() {
        activityStarted = false
        firstTime = true
        sessionID = ""
        sensorCaptureService.unregisterSensorsListeners()
        sensorRecordingJob?.cancel()
        timerJob?.cancel()
        wekaJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            _fileSentFlag.postValue(fileUtils.writeFile())
        }.invokeOnCompletion { if (fileSentFlag.value == true) fileUtils.deleteLocalFile() }
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

        sessionID = startDate.toString() + UUID.randomUUID().toString().takeLast(3)
        sessionID = sessionID.replace("([T,:-])".toRegex(), "")
        currentActivity.value?.let { fileUtils.createFileModel(sessionID, it) }
    }

    private fun startTimer(): Flow<Int> = flow {
        var secondsElapsed = 0
        while (true) {
            emit(secondsElapsed)
            delay(1000)
            secondsElapsed++
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
