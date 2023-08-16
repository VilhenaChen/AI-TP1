package pt.vilhena.ai.trabalhopratico.viewmodel

import android.app.Application
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pt.vilhena.ai.trabalhopratico.data.common.Constants.CSV_HEADER
import pt.vilhena.ai.trabalhopratico.data.common.Constants.SENSOR_CAPTURE_REFRESH_RATE
import pt.vilhena.ai.trabalhopratico.data.common.Constants.TAG
import pt.vilhena.ai.trabalhopratico.data.sftp.SftpService
import pt.vilhena.ai.trabalhopratico.sensors.SensorCaptureService
import java.io.File
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

    //  This is the documents folder path, to where the app will write the CSVs
    private val path =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

    private lateinit var dataFile: File
    private var rows = ArrayList<String>()

    private val sftpService = SftpService()

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
                val lat = sensorCaptureService.location.latitude
                val long = sensorCaptureService.location.longitude
                val altitude = sensorCaptureService.location.altitude
                val accuracy = sensorCaptureService.location.accuracy
                val bearing = sensorCaptureService.location.bearing
                val timestamp = sensorCaptureService.location.time
                val x_acc = sensorCaptureService.accelerometerData[0].toString()
                val y_acc = sensorCaptureService.accelerometerData[1].toString()
                val z_acc = sensorCaptureService.accelerometerData[2].toString()
                val x_gyro = sensorCaptureService.gyroscopeData[0].toString()
                val y_gyro = sensorCaptureService.gyroscopeData[1].toString()
                val z_gyro = sensorCaptureService.gyroscopeData[2].toString()
                val x_mag = sensorCaptureService.magneticFieldData[0].toString()
                val y_mag = sensorCaptureService.magneticFieldData[0].toString()
                val z_mag = sensorCaptureService.magneticFieldData[0].toString()
                rows.add("$sessionID,$lat,$long,$altitude,$accuracy,$bearing,$timestamp,$x_acc,$y_acc,$z_acc,$x_gyro,$y_gyro,$z_gyro,$x_mag,$y_mag,$z_mag,${currentActivity.value}")
            }

            val delayTime = SENSOR_CAPTURE_REFRESH_RATE - timeElapsed
            if (delayTime > 0) {
                delay(delayTime)
            }
        }
    }

    //  Stop capture sensor data
    fun stopCapture() {
        writeFile()
        sessionID = ""
        sensorCaptureService.unregisterSensorsListeners()
        sensorRecordingJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            sftpService.copyFileToSftp(dataFile)
        }
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

        sessionID = startDate.toString() + "_" + UUID.randomUUID().toString().takeLast(3)
        sessionID = sessionID.replace("([T,:-])".toRegex(), "")
    }

    //  File Section

    /*  Write on the CSV file the data
        The First line is being remove since the sensors are still being started
     */
    private fun writeFile() {
        rows.add(0, CSV_HEADER)
        rows.removeAt(1)
        val rowsList = rows.map { listOf(it) }
        val fileName = "${sessionID}_${currentActivity.value}.csv"
        if (!path.exists()) {
            path.mkdir()
        }
        dataFile = File(path, fileName)
        csvWriter().writeAll(rowsList, dataFile)
    }

    //  Delete CSV file when uploads ends
    private fun deleteFile() {
        if (dataFile.exists()) {
            dataFile.delete()
        } else {
            Log.d(TAG, "File ${dataFile.name} does not exist")
        }
    }
}
